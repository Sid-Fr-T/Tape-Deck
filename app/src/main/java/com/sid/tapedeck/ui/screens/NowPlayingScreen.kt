package com.sid.tapedeck.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import com.sid.tapedeck.data.model.Song
import com.sid.tapedeck.ui.components.AlbumArt
import com.sid.tapedeck.ui.components.TapeCard
import com.sid.tapedeck.ui.components.TapeChip
import com.sid.tapedeck.ui.components.TapeIconButton
import com.sid.tapedeck.ui.components.TapeIcons
import com.sid.tapedeck.ui.components.TapeSeekBar
import com.sid.tapedeck.ui.components.TapeText
import com.sid.tapedeck.ui.theme.TapeTheme
import com.sid.tapedeck.ui.theme.TapeType
import com.sid.tapedeck.ui.theme.chunkyBorder

@Composable
fun NowPlayingScreen(
    song: Song?,
    isPlaying: Boolean,
    currentPositionMs: Long,
    durationMs: Long,
    shuffleModeEnabled: Boolean,
    repeatMode: Int,
    isFavorite: Boolean,
    sleepTimerMinutesLeft: Int?,
    onTogglePlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Long) -> Unit,
    onToggleShuffle: () -> Unit,
    onCycleRepeat: () -> Unit,
    onToggleFavorite: () -> Unit,
    onSetSleepTimer: (Int) -> Unit,
    onDismiss: () -> Unit,
    onNavigateToQueue: () -> Unit,
    onNavigateToLyrics: () -> Unit
) {
    val colors = TapeTheme.colors

    val safeDuration = if (durationMs > 0L) durationMs else (song?.durationMs ?: 0L)
    var isSeeking by remember { mutableStateOf(false) }
    var seekPositionFraction by remember { mutableFloatStateOf(0f) }

    val progress = if (isSeeking) {
        seekPositionFraction
    } else if (safeDuration > 0L) {
        (currentPositionMs.toFloat() / safeDuration.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    var sleepTimerMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    // Swiping down closes the screen
                    if (dragAmount > 35f) {
                        onDismiss()
                    }
                }
            }
            .padding(horizontal = 22.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top bar: down button (closes), "NOW PLAYING", queue button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TapeIconButton(
                icon = TapeIcons.Down,
                size = 46.dp,
                backgroundColor = colors.card,
                iconTint = colors.ink,
                shadowDistance = 4.dp,
                onClick = onDismiss,
                contentDescription = "Dismiss"
            )

            TapeText(
                text = "NOW PLAYING",
                style = TapeType.topBar,
                color = colors.ink
            )

            TapeIconButton(
                icon = TapeIcons.Queue,
                size = 46.dp,
                backgroundColor = colors.card,
                iconTint = colors.ink,
                shadowDistance = 4.dp,
                onClick = onNavigateToQueue,
                contentDescription = "Queue"
            )
        }

        // Large TapeCard (radius 30dp, padding 12dp) holding album art at full width
        TapeCard(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .padding(vertical = 12.dp),
            radius = 30.dp,
            shadowOffset = 5.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                AlbumArt(
                    albumId = song?.albumId?.toString() ?: "now_playing",
                    artworkUri = song?.albumArtUri,
                    size = 300.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )
            }
        }

        // Title ExtraBold 28sp, artist 17sp muted, and a favorite heart button on the right
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TapeText(
                    text = song?.title ?: "No Cassette",
                    style = TapeType.nowPlayingTitle,
                    color = colors.ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                TapeText(
                    text = song?.artist ?: "Unknown Artist",
                    style = TapeType.nowPlayingArtist,
                    color = colors.muted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Favorite heart button (stores in DataStore)
            TapeIconButton(
                icon = TapeIcons.Heart,
                size = 48.dp,
                backgroundColor = colors.card,
                iconTint = if (isFavorite) colors.pink else colors.ink,
                shadowDistance = 4.dp,
                onClick = onToggleFavorite,
                contentDescription = "Favorite"
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // TapeSeekBar, with elapsed and total time in Space Mono 13sp muted below it
        Column(modifier = Modifier.fillMaxWidth()) {
            TapeSeekBar(
                progress = progress,
                onProgressChange = { newProgress ->
                    isSeeking = true
                    seekPositionFraction = newProgress
                },
                onProgressChangeFinished = {
                    if (safeDuration > 0L) {
                        val seekTargetMs = (seekPositionFraction * safeDuration).toLong()
                        onSeek(seekTargetMs)
                    }
                    isSeeking = false
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val currentMs = if (isSeeking) (seekPositionFraction * safeDuration).toLong() else currentPositionMs
                TapeText(
                    text = formatPlaybackTime(currentMs),
                    style = TapeType.mono,
                    color = colors.muted
                )
                TapeText(
                    text = formatPlaybackTime(safeDuration),
                    style = TapeType.mono,
                    color = colors.muted
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Controls row: shuffle (50dp), previous (60dp), main play/pause (88dp pink rounded square), next (60dp), repeat (50dp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TapeIconButton(
                icon = TapeIcons.Shuffle,
                size = 50.dp,
                backgroundColor = if (shuffleModeEnabled) colors.lime else colors.card,
                iconTint = if (shuffleModeEnabled) colors.onAccent else colors.ink,
                shadowDistance = 4.dp,
                onClick = onToggleShuffle,
                contentDescription = "Shuffle"
            )

            TapeIconButton(
                icon = TapeIcons.Prev,
                size = 60.dp,
                backgroundColor = colors.card,
                iconTint = colors.ink,
                shadowDistance = 4.dp,
                onClick = onPrevious,
                contentDescription = "Previous"
            )

            // Main play/pause: 88dp, radius 28dp rounded square, pink
            TapeIconButton(
                icon = if (isPlaying) TapeIcons.Pause else TapeIcons.Play,
                size = 88.dp,
                isSquare = true,
                squareRadius = 28.dp,
                backgroundColor = colors.pink,
                iconTint = colors.onPink,
                shadowDistance = 5.dp,
                onClick = onTogglePlayPause,
                contentDescription = if (isPlaying) "Pause" else "Play"
            )

            TapeIconButton(
                icon = TapeIcons.Next,
                size = 60.dp,
                backgroundColor = colors.card,
                iconTint = colors.ink,
                shadowDistance = 4.dp,
                onClick = onNext,
                contentDescription = "Next"
            )

            TapeIconButton(
                icon = TapeIcons.Repeat,
                size = 50.dp,
                backgroundColor = if (repeatMode != Player.REPEAT_MODE_OFF) colors.lime else colors.card,
                iconTint = if (repeatMode != Player.REPEAT_MODE_OFF) colors.onAccent else colors.ink,
                shadowDistance = 4.dp,
                onClick = onCycleRepeat,
                contentDescription = "Repeat"
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Bottom chips: "Lyrics" (yellow), "Queue", "Sleep timer"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Lyrics chip (yellow background with onAccent text)
            Box(modifier = Modifier.padding(horizontal = 5.dp)) {
                TapeChip(
                    text = "Lyrics",
                    isSelected = true,
                    onClick = onNavigateToLyrics
                )
            }

            // Queue chip
            Box(modifier = Modifier.padding(horizontal = 5.dp)) {
                TapeChip(
                    text = "Queue",
                    isSelected = false,
                    onClick = onNavigateToQueue
                )
            }

            // Sleep timer chip with dropdown picker
            Box(modifier = Modifier.padding(horizontal = 5.dp)) {
                val timerLabel = if (sleepTimerMinutesLeft != null) {
                    "Sleep (${sleepTimerMinutesLeft}m)"
                } else {
                    "Sleep timer"
                }

                TapeChip(
                    text = timerLabel,
                    isSelected = sleepTimerMinutesLeft != null,
                    onClick = { sleepTimerMenuExpanded = true }
                )

                DropdownMenu(
                    expanded = sleepTimerMenuExpanded,
                    onDismissRequest = { sleepTimerMenuExpanded = false },
                    modifier = Modifier
                        .background(colors.card)
                        .chunkyBorder(colors.line, width = 3.dp, shape = RoundedCornerShape(12.dp))
                ) {
                    listOf(0 to "Off", 15 to "15 minutes", 30 to "30 minutes", 45 to "45 minutes", 60 to "60 minutes").forEach { (mins, label) ->
                        DropdownMenuItem(
                            text = {
                                TapeText(
                                    text = label,
                                    style = TapeType.rowTitle,
                                    color = if (sleepTimerMinutesLeft == mins) colors.pinkText else colors.ink
                                )
                            },
                            onClick = {
                                onSetSleepTimer(mins)
                                sleepTimerMenuExpanded = false
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

