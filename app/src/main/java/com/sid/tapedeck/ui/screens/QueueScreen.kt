package com.sid.tapedeck.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.sid.tapedeck.data.model.Song
import com.sid.tapedeck.ui.components.SongRow
import com.sid.tapedeck.ui.components.TapeIconButton
import com.sid.tapedeck.ui.components.TapeIcons
import com.sid.tapedeck.ui.components.TapeText
import com.sid.tapedeck.ui.theme.TapeTheme
import com.sid.tapedeck.ui.theme.TapeType
import com.sid.tapedeck.ui.theme.chunkyBorder

@Composable
fun QueueScreen(
    queue: List<Song>,
    currentSong: Song?,
    isPlaying: Boolean,
    currentPositionMs: Long,
    onPlayQueueIndex: (Int) -> Unit,
    onTogglePlayPause: () -> Unit,
    onRemoveFromQueue: (Int) -> Unit,
    onReorderQueue: (Int, Int) -> Unit,
    onClearUpNext: () -> Unit,
    onBack: () -> Unit
) {
    val colors = TapeTheme.colors

    val currentIndex = remember(queue, currentSong) {
        val idx = queue.indexOfFirst { it.id == currentSong?.id }
        if (idx >= 0) idx else 0
    }

    val upNextSongs = remember(queue, currentIndex) {
        if (currentIndex in queue.indices && currentIndex + 1 < queue.size) {
            queue.subList(currentIndex + 1, queue.size)
        } else {
            emptyList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // Top bar: back, "QUEUE", more
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
                text = "QUEUE",
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
                contentDescription = "More options"
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 22.dp, end = 22.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Label "PLAYING NOW" (Space Mono 14sp muted)
            item {
                TapeText(
                    text = "PLAYING NOW",
                    style = TapeType.mono,
                    color = colors.muted
                )
            }

            // Current song as a lime SongRow with a pause icon
            item {
                if (currentSong != null) {
                    val formattedElapsed = formatPlaybackTime(currentPositionMs)
                    SongRow(
                        title = currentSong.title,
                        artist = currentSong.artist,
                        duration = formattedElapsed,
                        albumId = currentSong.albumId.toString(),
                        artworkUri = currentSong.albumArtUri,
                        isPlaying = true,
                        trailingIcon = if (isPlaying) TapeIcons.Pause else TapeIcons.Play,
                        onTrailingIconClick = onTogglePlayPause,
                        onClick = onTogglePlayPause
                    )
                } else {
                    TapeText(
                        text = "No song is currently playing",
                        style = TapeType.body,
                        color = colors.muted
                    )
                }
            }

            // Row "UP NEXT" with a small "Clear" chip on the right
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TapeText(
                        text = "UP NEXT",
                        style = TapeType.section,
                        color = colors.ink
                    )

                    if (upNextSongs.isNotEmpty()) {
                        // Small "Clear" chip
                        val chipShape = RoundedCornerShape(12.dp)
                        Box(
                            modifier = Modifier
                                .background(colors.card, chipShape)
                                .chunkyBorder(colors.line, width = 2.dp, shape = chipShape)
                                .clickable { onClearUpNext() }
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            TapeText(
                                text = "Clear",
                                style = TapeType.rowTitle,
                                color = colors.ink
                            )
                        }
                    }
                }
            }

            // Remaining songs as SongRows with drag handle icon (drag to reorder, swipe to remove)
            if (upNextSongs.isEmpty()) {
                item {
                    TapeText(
                        text = "No upcoming songs. Tap any track to queue.",
                        style = TapeType.body,
                        color = colors.muted
                    )
                }
            } else {
                itemsIndexed(upNextSongs, key = { _, song -> song.id }) { relativeIndex, song ->
                    val absoluteIndex = currentIndex + 1 + relativeIndex

                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { dismissValue ->
                            if (dismissValue == SwipeToDismissBoxValue.EndToStart || dismissValue == SwipeToDismissBoxValue.StartToEnd) {
                                onRemoveFromQueue(absoluteIndex)
                                true
                            } else {
                                false
                            }
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(colors.pink)
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                TapeIconButton(
                                    icon = TapeIcons.Close,
                                    size = 36.dp,
                                    backgroundColor = colors.pink,
                                    iconTint = colors.onPink,
                                    onClick = { onRemoveFromQueue(absoluteIndex) }
                                )
                            }
                        }
                    ) {
                        SongRow(
                            title = song.title,
                            artist = song.artist,
                            duration = song.durationFormatted,
                            albumId = song.albumId.toString(),
                            artworkUri = song.albumArtUri,
                            isPlaying = false,
                            trailingIcon = TapeIcons.Drag,
                            onClick = { onPlayQueueIndex(absoluteIndex) },
                            onTrailingIconClick = {
                                // Reorder up if not first, or down
                                if (relativeIndex > 0) {
                                    onReorderQueue(absoluteIndex, absoluteIndex - 1)
                                } else if (relativeIndex < upNextSongs.size - 1) {
                                    onReorderQueue(absoluteIndex, absoluteIndex + 1)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun formatPlaybackTime(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(java.util.Locale.US, "%02d:%02d", minutes, seconds)
}

