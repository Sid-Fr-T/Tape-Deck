package com.sid.tapedeck.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import com.sid.tapedeck.ui.theme.TapeType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sid.tapedeck.ui.theme.TapeTheme
import com.sid.tapedeck.ui.theme.chunkyBorder
import com.sid.tapedeck.ui.theme.hardShadow

/**
 * SongRow: 66dp high TapeCard-style row (radius 18dp, shadow 4dp) with 46dp AlbumArt,
 * title, artist, duration in Space Mono 12sp, trailing icon.
 * "Playing" variant has lime background and onAccent text (subtitle color lime3).
 */
@Composable
fun SongRow(
    title: String,
    artist: String,
    duration: String,
    albumId: String?,
    artworkUri: Any?,
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: ImageVector = TapeIcons.More,
    onTrailingIconClick: (() -> Unit)? = null
) {
    val colors = TapeTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val currentYOffset = if (isPressed) 3.dp else 0.dp
    val currentShadow = if (isPressed) 1.dp else 4.dp
    val shape = RoundedCornerShape(18.dp)

    val bg = if (isPlaying) colors.lime else colors.card
    val titleColor = if (isPlaying) colors.onAccent else colors.ink
    val subtitleColor = if (isPlaying) colors.lime3 else colors.muted
    val iconTint = if (isPlaying) colors.onAccent else colors.ink

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(66.dp)
            .offset(y = currentYOffset)
            .hardShadow(color = colors.line, offsetY = currentShadow, shape = shape)
            .background(color = bg, shape = shape)
            .chunkyBorder(color = colors.line, width = 3.dp, shape = shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 10.dp, vertical = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 46dp Album Art
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
                    color = titleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                TapeText(
                    text = artist,
                    style = TapeType.subtitle,
                    color = subtitleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Duration in Space Mono 12sp
            TapeText(
                text = duration,
                style = TapeType.mono,
                color = subtitleColor
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Trailing icon
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onTrailingIconClick?.invoke() ?: onClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

