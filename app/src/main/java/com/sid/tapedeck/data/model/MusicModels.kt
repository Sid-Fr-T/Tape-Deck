package com.sid.tapedeck.data.model

import android.net.Uri

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Long,
    val durationMs: Long,
    val durationFormatted: String,
    val dateAdded: Long,
    val dateModified: Long,
    val contentUri: Uri,
    val filePath: String,
    val albumArtUri: Uri
)

data class Album(
    val id: Long,
    val title: String,
    val artist: String,
    val albumArtUri: Uri,
    val songCount: Int,
    val songs: List<Song>
)

data class Artist(
    val name: String,
    val songCount: Int,
    val albumCount: Int,
    val albums: List<Album>,
    val songs: List<Song>
)

