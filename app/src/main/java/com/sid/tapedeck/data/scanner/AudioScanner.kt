package com.sid.tapedeck.data.scanner

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.sid.tapedeck.data.model.Album
import com.sid.tapedeck.data.model.Artist
import com.sid.tapedeck.data.model.Song
import java.io.File
import java.util.Locale

data class MusicLibrary(
    val songs: List<Song>,
    val albums: List<Album>,
    val artists: List<Artist>
)

class AudioScanner(private val context: Context) {

    private val albumArtBaseUri: Uri = Uri.parse("content://media/external/audio/albumart")

    fun scan(): MusicLibrary {
        val songList = mutableListOf<Song>()

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATE_MODIFIED,
            MediaStore.Audio.Media.DATA
        )

        // Ignore files shorter than 30 seconds and non-music files
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.DURATION} >= 30000"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} COLLATE NOCASE ASC"

        val queryUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        try {
            context.contentResolver.query(
                queryUri,
                projection,
                selection,
                null,
                sortOrder
            )?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val dateAddedCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
                val dateModifiedCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)
                val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

                while (cursor.moveToNext()) {
                    val filePath = cursor.getString(dataCol) ?: ""

                    // Ignore notification, ringtone, alarm, and recording directories
                    if (isExcludedPath(filePath)) {
                        continue
                    }

                    val id = cursor.getLong(idCol)
                    val rawTitle = cursor.getString(titleCol)
                    val rawArtist = cursor.getString(artistCol)
                    val rawAlbum = cursor.getString(albumCol)
                    val albumId = cursor.getLong(albumIdCol)
                    val durationMs = cursor.getLong(durationCol)
                    val dateAdded = cursor.getLong(dateAddedCol)
                    val dateModified = cursor.getLong(dateModifiedCol)

                    val cleanTitle = cleanTitle(rawTitle, filePath)
                    val cleanArtist = cleanArtist(rawArtist)
                    val cleanAlbum = cleanAlbum(rawAlbum)

                    val contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                    val albumArtUri = ContentUris.withAppendedId(albumArtBaseUri, albumId)

                    songList.add(
                        Song(
                            id = id,
                            title = cleanTitle,
                            artist = cleanArtist,
                            album = cleanAlbum,
                            albumId = albumId,
                            durationMs = durationMs,
                            durationFormatted = formatDuration(durationMs),
                            dateAdded = dateAdded,
                            dateModified = dateModified,
                            contentUri = contentUri,
                            filePath = filePath,
                            albumArtUri = albumArtUri
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Group into Albums
        val albums = songList
            .groupBy { it.albumId }
            .map { (albumId, songs) ->
                val sample = songs.first()
                Album(
                    id = albumId,
                    title = sample.album,
                    artist = sample.artist,
                    albumArtUri = sample.albumArtUri,
                    songCount = songs.size,
                    songs = songs.sortedBy { it.title.lowercase(Locale.ROOT) }
                )
            }
            .sortedBy { it.title.lowercase(Locale.ROOT) }

        // Group into Artists
        val artists = songList
            .groupBy { it.artist.lowercase(Locale.ROOT) }
            .map { (_, songs) ->
                val artistName = songs.first().artist
                val artistAlbums = songs.groupBy { it.albumId }.map { (albumId, albumSongs) ->
                    val sample = albumSongs.first()
                    Album(
                        id = albumId,
                        title = sample.album,
                        artist = artistName,
                        albumArtUri = sample.albumArtUri,
                        songCount = albumSongs.size,
                        songs = albumSongs
                    )
                }
                Artist(
                    name = artistName,
                    songCount = songs.size,
                    albumCount = artistAlbums.size,
                    albums = artistAlbums,
                    songs = songs.sortedBy { it.title.lowercase(Locale.ROOT) }
                )
            }
            .sortedBy { it.name.lowercase(Locale.ROOT) }

        return MusicLibrary(
            songs = songList,
            albums = albums,
            artists = artists
        )
    }

    private fun isExcludedPath(path: String): Boolean {
        val lower = path.lowercase(Locale.ROOT)
        return lower.contains("/ringtones/") ||
                lower.contains("/notifications/") ||
                lower.contains("/alarms/") ||
                lower.contains("/podcasts/") ||
                lower.contains("/voice recorder/") ||
                lower.contains("/call recordings/") ||
                lower.contains("/sound recorder/") ||
                lower.contains("/whatsapp/media/whatsapp audio/")
    }

    private fun cleanTitle(rawTitle: String?, filePath: String): String {
        val trimmed = rawTitle?.trim().orEmpty()
        if (trimmed.isNotEmpty() && !trimmed.equals("<unknown>", ignoreCase = true)) {
            return trimmed
        }
        return if (filePath.isNotEmpty()) {
            File(filePath).nameWithoutExtension
        } else {
            "Unknown Track"
        }
    }

    private fun cleanArtist(rawArtist: String?): String {
        val trimmed = rawArtist?.trim().orEmpty()
        if (trimmed.isEmpty() ||
            trimmed.equals("<unknown>", ignoreCase = true) ||
            trimmed.equals("unknown", ignoreCase = true)
        ) {
            return "Unknown Artist"
        }
        return trimmed
    }

    private fun cleanAlbum(rawAlbum: String?): String {
        val trimmed = rawAlbum?.trim().orEmpty()
        if (trimmed.isEmpty() ||
            trimmed.equals("<unknown>", ignoreCase = true) ||
            trimmed.equals("unknown", ignoreCase = true)
        ) {
            return "Unknown Album"
        }
        return trimmed
    }

    private fun formatDuration(ms: Long): String {
        val totalSeconds = (ms / 1000).coerceAtLeast(0)
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return if (minutes >= 60) {
            val hours = minutes / 60
            val remMinutes = minutes % 60
            String.format(Locale.US, "%d:%02d:%02d", hours, remMinutes, seconds)
        } else {
            String.format(Locale.US, "%02d:%02d", minutes, seconds)
        }
    }
}

