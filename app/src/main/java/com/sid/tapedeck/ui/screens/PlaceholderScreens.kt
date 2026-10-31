package com.sid.tapedeck.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.sid.tapedeck.ui.components.TapeText
import com.sid.tapedeck.ui.theme.TapeType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sid.tapedeck.data.model.Song
import com.sid.tapedeck.data.repository.LibraryState
import com.sid.tapedeck.ui.components.AlbumArt
import com.sid.tapedeck.ui.components.SongRow
import com.sid.tapedeck.ui.components.TapeButton
import com.sid.tapedeck.ui.components.TapeCard
import com.sid.tapedeck.ui.components.TapeChip
import com.sid.tapedeck.ui.components.TapeIconButton
import com.sid.tapedeck.ui.components.TapeIcons
import com.sid.tapedeck.ui.components.TapeSeekBar
import com.sid.tapedeck.ui.theme.TapeTheme
import com.sid.tapedeck.ui.theme.ThemeMode
import com.sid.tapedeck.ui.theme.chunkyBorder

@Composable
fun YouScreen(
    currentTheme: ThemeMode,
    onSelectTheme: (ThemeMode) -> Unit
) {
    val colors = TapeTheme.colors
    val typography = TapeTheme.typography

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TapeText(text = "SETTINGS & PROFILE", style = TapeType.header, color = colors.ink)

        // Theme Switcher Card
        TapeCard(
            modifier = Modifier.fillMaxWidth(),
            radius = 20.dp,
            shadowOffset = 5.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TapeText(text = "DISPLAY THEME", style = TapeType.topBar, color = colors.pinkText)
                TapeText(
                    text = "Switch between Light, Dark, or System follow. No Material You gradients or blurs.",
                    style = TapeType.body,
                    color = colors.muted
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ThemeMode.entries.forEach { mode ->
                        TapeChip(
                            text = mode.name.uppercase(),
                            isSelected = currentTheme == mode,
                            onClick = { onSelectTheme(mode) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Specs & Hardware card
        TapeCard(
            modifier = Modifier.fillMaxWidth(),
            radius = 20.dp,
            shadowOffset = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TapeText(text = "TAPEDECK HARDWARE PROFILE", style = TapeType.topBar, color = colors.ink)
                TapeText(text = "Engine: Media3 ExoPlayer (Offline)", style = TapeType.body, color = colors.muted)
                TapeText(text = "Outline: 3dp chunky line", style = TapeType.body, color = colors.muted)
                TapeText(text = "Shadows: 5dp hard offset (no blur)", style = TapeType.body, color = colors.muted)
                TapeText(text = "Memory: Low-memory optimized (<2GB JVM)", style = TapeType.body, color = colors.muted)
            }
        }
    }
}

@Composable
fun AlbumDetailScreen(
    albumId: String,
    onBack: () -> Unit,
    onPlayTrack: (String) -> Unit
) {
    val colors = TapeTheme.colors
    val typography = TapeTheme.typography

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TapeIconButton(
                icon = TapeIcons.Back,
                size = 44.dp,
                onClick = onBack
            )
            TapeText(text = "ALBUM VIEW", style = TapeType.topBar, color = colors.ink)
            TapeIconButton(
                icon = TapeIcons.Heart,
                size = 44.dp,
                onClick = {}
            )
        }

        // Big album header
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            AlbumArt(
                albumId = albumId,
                artworkUri = null,
                size = 180.dp
            )
        }

        TapeText(
            text = "ALBUM: ${albumId.uppercase()}",
            style = TapeType.header,
            color = colors.ink
        )
        TapeText(
            text = "Offline Album • 8 Tracks • Released 1986",
            style = TapeType.subtitle,
            color = colors.muted
        )

        TapeButton(
            text = "PLAY CASSETTE",
            leadingIcon = TapeIcons.Play,
            onClick = { onPlayTrack("Track 1") }
        )

        TapeText(text = "TRACK LIST", style = TapeType.section, color = colors.ink)

        listOf("Side A: Track 01", "Side A: Track 02", "Side B: Track 03", "Side B: Track 04").forEachIndexed { i, title ->
            SongRow(
                title = title,
                artist = "Cassette Artist",
                duration = "03:4${i}",
                albumId = albumId,
                artworkUri = null,
                isPlaying = i == 0,
                onClick = { onPlayTrack(title) }
            )
        }
    }
}

@Composable
fun NowPlayingScreen(
    song: Song?,
    isPlaying: Boolean,
    currentPositionMs: Long,
    durationMs: Long,
    shuffleModeEnabled: Boolean,
    repeatMode: Int,
    onTogglePlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Long) -> Unit,
    onToggleShuffle: () -> Unit,
    onCycleRepeat: () -> Unit,
    onDismiss: () -> Unit,
    onNavigateToQueue: () -> Unit,
    onNavigateToLyrics: () -> Unit
) {
    val colors = TapeTheme.colors
    val typography = TapeTheme.typography

    val safeDuration = if (durationMs > 0L) durationMs else (song?.durationMs ?: 0L)
    val progress = if (safeDuration > 0L) {
        (currentPositionMs.toFloat() / safeDuration.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top row: dismiss down arrow, title, queue button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TapeIconButton(
                icon = TapeIcons.Down,
                size = 44.dp,
                onClick = onDismiss
            )
            TapeText(
                text = "NOW PLAYING",
                style = TapeType.topBar,
                color = colors.ink
            )
            TapeIconButton(
                icon = TapeIcons.Queue,
                size = 44.dp,
                onClick = onNavigateToQueue
            )
        }

        // Center: Big Album Art
        AlbumArt(
            albumId = song?.albumId?.toString() ?: "current_playing",
            artworkUri = song?.albumArtUri,
            size = 240.dp
        )

        // Metadata
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            TapeText(
                text = song?.title?.uppercase() ?: "NO CASSETTE LOADED",
                style = TapeType.albumTitle,
                color = colors.ink,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            TapeText(
                text = "${song?.artist ?: "UNKNOWN"} • ${song?.album ?: "OFFLINE"}",
                style = TapeType.topBar,
                color = colors.muted,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }

        // Seekbar
        Column(modifier = Modifier.fillMaxWidth()) {
            TapeSeekBar(
                progress = progress,
                onProgressChange = { newProgress ->
                    if (safeDuration > 0L) {
                        onSeek((newProgress * safeDuration).toLong())
                    }
                }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TapeText(
                    text = formatPlaybackTime(currentPositionMs),
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

        // Player Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TapeIconButton(
                icon = TapeIcons.Shuffle,
                size = 44.dp,
                backgroundColor = if (shuffleModeEnabled) colors.lime else colors.card,
                iconTint = if (shuffleModeEnabled) colors.onAccent else colors.ink,
                onClick = onToggleShuffle
            )
            TapeIconButton(
                icon = TapeIcons.Prev,
                size = 52.dp,
                onClick = onPrevious
            )
            // Big play button: 80dp rounded square radius 28dp
            TapeIconButton(
                icon = if (isPlaying) TapeIcons.Pause else TapeIcons.Play,
                size = 80.dp,
                isSquare = true,
                squareRadius = 28.dp,
                backgroundColor = colors.pink,
                iconTint = colors.onPink,
                onClick = onTogglePlayPause
            )
            TapeIconButton(
                icon = TapeIcons.Next,
                size = 52.dp,
                onClick = onNext
            )
            TapeIconButton(
                icon = TapeIcons.Repeat,
                size = 44.dp,
                backgroundColor = if (repeatMode != androidx.media3.common.Player.REPEAT_MODE_OFF) colors.lime else colors.card,
                iconTint = if (repeatMode != androidx.media3.common.Player.REPEAT_MODE_OFF) colors.onAccent else colors.ink,
                onClick = onCycleRepeat
            )
        }

        // Lyrics row button
        TapeButton(
            text = "LYRICS",
            leadingIcon = TapeIcons.Edit,
            backgroundColor = colors.card,
            textColor = colors.ink,
            onClick = onNavigateToLyrics
        )
    }
}

private fun formatPlaybackTime(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(java.util.Locale.US, "%02d:%02d", minutes, seconds)
}

@Composable
fun QueueScreen(
    queue: List<Song>,
    currentSong: Song?,
    onPlayQueueIndex: (Int) -> Unit,
    onRemoveFromQueue: (Int) -> Unit,
    onBack: () -> Unit
) {
    val colors = TapeTheme.colors
    val typography = TapeTheme.typography

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TapeIconButton(icon = TapeIcons.Back, size = 44.dp, onClick = onBack)
            TapeText(text = "PLAY QUEUE", style = TapeType.header, color = colors.ink)
            TapeIconButton(icon = TapeIcons.Sort, size = 44.dp, onClick = {})
        }

        TapeText(text = "UP NEXT (${queue.size})", style = TapeType.section, color = colors.pinkText)

        if (queue.isEmpty()) {
            TapeText(text = "Queue is empty. Tap any song to play.", style = TapeType.body, color = colors.muted)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(queue.size, key = { index -> queue[index].id }) { index ->
                    val song = queue[index]
                    val isCurrent = currentSong?.id == song.id
                    SongRow(
                        title = song.title,
                        artist = song.artist,
                        duration = song.durationFormatted,
                        albumId = song.albumId.toString(),
                        artworkUri = song.albumArtUri,
                        isPlaying = isCurrent,
                        trailingIcon = TapeIcons.Close,
                        onTrailingIconClick = { onRemoveFromQueue(index) },
                        onClick = { onPlayQueueIndex(index) }
                    )
                }
            }
        }
    }
}

@Composable
fun LyricsScreen(onBack: () -> Unit) {
    val colors = TapeTheme.colors
    val typography = TapeTheme.typography

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TapeIconButton(icon = TapeIcons.Back, size = 44.dp, onClick = onBack)
            TapeText(text = "LYRICS", style = TapeType.header, color = colors.ink)
            TapeIconButton(icon = TapeIcons.Heart, size = 44.dp, onClick = {})
        }

        TapeCard(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            radius = 20.dp,
            shadowOffset = 5.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                TapeText(text = "SYNTHWAVE DREAMS", style = TapeType.albumTitle, color = colors.ink)
                TapeText(text = "[Verse 1]", style = TapeType.topBar, color = colors.pinkText)
                TapeText(
                    text = "City lights in the rear-view mirror,\nMidnight hums through the cassette player,\nAnalog lines across the sky,\nWe ride until the morning light.",
                    style = TapeType.rowTitle,
                    color = colors.ink
                )
                TapeText(text = "[Chorus]", style = TapeType.topBar, color = colors.pinkText)
                TapeText(
                    text = "Keep the tape rolling, rewind the night,\nTurn the dial till the signal is right,\nNo blur, no noise, just analog sound,\nIn this pocket handheld we found.",
                    style = TapeType.rowTitle,
                    color = colors.ink
                )
            }
        }
    }
}
