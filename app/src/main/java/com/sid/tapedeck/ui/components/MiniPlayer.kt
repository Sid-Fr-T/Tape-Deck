package com.sid.tapedeck.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import com.sid.tapedeck.ui.theme.TapeType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sid.tapedeck.ui.theme.TapeTheme

/**
 * MiniPlayer: DOCKED strip, full width, 68dp high, bg card, 3dp top border only
 * (no rounded corners, not floating). Art 46dp, title/artist, pink play circle (44dp),
 * next button (40dp).
 */
@Composable
fun MiniPlayer(
    title: String,
    artist: String,
    albumId: String?,
    artworkUri: Any?,
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onPreviousClick: (() -> Unit)? = null
) {
    val colors = TapeTheme.colors

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(62.dp)
            .background(colors.card)
            .drawBehind {
                // 3dp top border only
                drawLine(
                    color = colors.line,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 3.dp.toPx()
                )
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 46dp Art
            AlbumArt(
                albumId = albumId,
                artworkUri = artworkUri,
                size = 46.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Title and Artist
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                TapeText(
                    text = title,
                    style = TapeType.rowTitle,
                    color = colors.ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                TapeText(
                    text = artist,
                    style = TapeType.subtitle,
                    color = colors.muted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Optional Previous button (40dp)
            if (onPreviousClick != null) {
                TapeIconButton(
                    icon = TapeIcons.Prev,
                    size = 40.dp,
                    backgroundColor = colors.card,
                    iconTint = colors.ink,
                    onClick = onPreviousClick,
                    contentDescription = "Previous Track"
                )
                Spacer(modifier = Modifier.width(6.dp))
            }

            // Pink play circle (44dp)
            TapeIconButton(
                icon = if (isPlaying) TapeIcons.Pause else TapeIcons.Play,
                size = 44.dp,
                backgroundColor = colors.pink,
                iconTint = colors.onPink,
                onClick = onPlayPauseClick,
                contentDescription = if (isPlaying) "Pause" else "Play"
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Next button (40dp)
            TapeIconButton(
                icon = TapeIcons.Next,
                size = 40.dp,
                backgroundColor = colors.card,
                iconTint = colors.ink,
                onClick = onNextClick,
                contentDescription = "Next Track"
            )
        }
    }
}
