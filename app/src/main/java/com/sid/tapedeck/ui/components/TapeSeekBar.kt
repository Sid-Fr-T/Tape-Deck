package com.sid.tapedeck.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.sid.tapedeck.ui.theme.TapeTheme
import com.sid.tapedeck.ui.theme.chunkyBorder
import kotlin.math.roundToInt

/**
 * TapeSeekBar: 18dp high track (3dp border, radius 10dp, bg track) filled with lime,
 * plus a 28dp rounded-square pink thumb with a 3dp border. Draggable.
 */
@Composable
fun TapeSeekBar(
    progress: Float, // 0.0f to 1.0f
    onProgressChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    onProgressChangeFinished: (() -> Unit)? = null,
    enabled: Boolean = true
) {
    val colors = TapeTheme.colors
    val trackRadius = 10.dp
    val trackShape = RoundedCornerShape(trackRadius)
    val thumbSize = 28.dp
    val thumbShape = RoundedCornerShape(8.dp)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        val totalWidthPx = constraints.maxWidth.toFloat()
        val thumbSizePx = with(LocalDensity.current) { thumbSize.toPx() }
        val draggableWidth = (totalWidthPx - thumbSizePx).coerceAtLeast(1f)

        val clampedProgress = progress.coerceIn(0f, 1f)
        val thumbOffsetPx = clampedProgress * draggableWidth

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .pointerInput(enabled, draggableWidth) {
                    if (!enabled) return@pointerInput
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        down.consume()
                        val initialProgress = ((down.position.x - thumbSizePx / 2f) / draggableWidth).coerceIn(0f, 1f)
                        onProgressChange(initialProgress)

                        while (true) {
                            val event = awaitPointerEvent()
                            val pointer = event.changes.firstOrNull { it.id == down.id }
                            if (pointer == null || !pointer.pressed) {
                                break
                            }
                            pointer.consume()
                            val newProgress = ((pointer.position.x - thumbSizePx / 2f) / draggableWidth).coerceIn(0f, 1f)
                            onProgressChange(newProgress)
                        }
                        onProgressChangeFinished?.invoke()
                    }
                },
            contentAlignment = Alignment.CenterStart
        ) {
            // 18dp high track container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(18.dp)
                    .clip(trackShape)
                    .background(colors.track, trackShape)
                    .chunkyBorder(colors.line, width = 3.dp, shape = trackShape)
            ) {
                // Lime filled portion
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = clampedProgress)
                        .fillMaxHeight()
                        .background(colors.lime)
                )
            }

            // 28dp rounded-square pink thumb with 3dp border
            Box(
                modifier = Modifier
                    .offset { IntOffset(thumbOffsetPx.roundToInt(), 0) }
                    .size(thumbSize)
                    .background(colors.pink, thumbShape)
                    .chunkyBorder(colors.line, width = 3.dp, shape = thumbShape)
            )
        }
    }
}
