package com.sid.tapedeck.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sid.tapedeck.data.model.Album
import com.sid.tapedeck.data.model.Song
import com.sid.tapedeck.ui.components.AlbumArt
import com.sid.tapedeck.ui.components.TapeButton
import com.sid.tapedeck.ui.components.TapeCard
import com.sid.tapedeck.ui.components.TapeIconButton
import com.sid.tapedeck.ui.components.TapeIcons
import com.sid.tapedeck.ui.components.TapeText
import com.sid.tapedeck.ui.theme.TapeTheme
import com.sid.tapedeck.ui.theme.TapeType

@Composable
fun AlbumScreen(
    albumId: String,
    albums: List<Album>,
    currentPlayingSong: Song?,
    isPlaying: Boolean,
    onBack: () -> Unit,
    onPlaySong: (Song, List<Song>) -> Unit,
    onShuffleAlbum: (List<Song>) -> Unit
) {
    val colors = TapeTheme.colors

    val album = remember(albumId, albums) {
        albums.firstOrNull { it.id.toString() == albumId }
    }

    val songs = album?.songs ?: emptyList()
    val totalMinutes = remember(songs) {
        (songs.sumOf { it.durationMs } / 60000L).coerceAtLeast(1L)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // Top bar: back button, "ALBUM" title (Space Mono 16sp), more button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(horizontal = 22.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TapeIconButton(
                icon = TapeIcons.Back,
                size = 46.dp,
                backgroundColor = colors.card,
                iconTint = colors.ink,
                shadowDistance = 4.dp,
                onClick = onBack,
                contentDescription = "Back"
            )

            TapeText(
                text = "ALBUM",
                style = TapeType.topBar,
                color = colors.ink
            )

            TapeIconButton(
                icon = TapeIcons.More,
                size = 46.dp,
                backgroundColor = colors.card,
                iconTint = colors.ink,
                shadowDistance = 4.dp,
                onClick = {},
                contentDescription = "More"
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 22.dp, end = 22.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Centered 190dp AlbumArt inside TapeCard (radius 28dp, padding 10dp)
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TapeCard(
                        modifier = Modifier.size(210.dp),
                        radius = 28.dp,
                        shadowOffset = 5.dp
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AlbumArt(
                                albumId = album?.id?.toString() ?: albumId,
                                artworkUri = album?.albumArtUri,
                                size = 190.dp
                            )
                        }
                    }
                }
            }

            // Album name (Space Mono 22sp, centered), then "Artist · N songs · M min" in 14sp muted
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TapeText(
                        text = (album?.title ?: "ALBUM").uppercase(),
                        style = TapeType.albumTitle,
                        color = colors.ink,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    TapeText(
                        text = "${album?.artist ?: "Unknown Artist"} • ${songs.size} songs • $totalMinutes min",
                        style = TapeType.body,
                        color = colors.muted,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Pink "Play" and yellow "Shuffle" buttons side by side
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TapeButton(
                        text = "Play",
                        leadingIcon = TapeIcons.Play,
                        backgroundColor = colors.pink,
                        textColor = colors.onPink,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (songs.isNotEmpty()) {
                                onPlaySong(songs.first(), songs)
                            }
                        }
                    )

                    TapeButton(
                        text = "Shuffle",
                        leadingIcon = TapeIcons.Shuffle,
                        backgroundColor = colors.yellow,
                        textColor = colors.onAccent,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (songs.isNotEmpty()) {
                                onShuffleAlbum(songs)
                            }
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Numbered track list WITHOUT cards: 50dp rows with 2dp bottom line
            itemsIndexed(songs, key = { _, song -> song.id }) { index, song ->
                val isCurrentPlaying = isPlaying && currentPlayingSong?.id == song.id
                val trackNumStr = String.format(java.util.Locale.US, "%02d", index + 1)
                val numColor = if (isCurrentPlaying) colors.pinkText else colors.ink

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .drawBehind {
                            // 2dp bottom line
                            drawLine(
                                color = colors.line.copy(alpha = 0.25f),
                                start = Offset(0f, size.height),
                                end = Offset(size.width, size.height),
                                strokeWidth = 2.dp.toPx()
                            )
                        }
                        .clickable { onPlaySong(song, songs) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Track number in Space Mono (pinkText for playing track)
                    TapeText(
                        text = trackNumStr,
                        style = TapeType.mono,
                        color = numColor,
                        modifier = Modifier.width(28.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    // Title ExtraBold 16sp
                    TapeText(
                        text = song.title,
                        style = TapeType.rowTitle,
                        color = if (isCurrentPlaying) colors.pinkText else colors.ink,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Duration on the right
                    TapeText(
                        text = song.durationFormatted,
                        style = TapeType.mono,
                        color = colors.muted
                    )
                }
            }
        }
    }
}

