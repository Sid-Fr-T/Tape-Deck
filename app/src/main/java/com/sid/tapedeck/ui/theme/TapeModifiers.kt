package com.sid.tapedeck.ui.theme

import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Hard offset shadow that draws the same shape offset downward using drawBehind.
 * Strictly zero blur, no elevation blur shader, optimal for low-end hardware.
 */
fun Modifier.hardShadow(
    color: Color,
    offsetY: Dp = 5.dp,
    shape: Shape = RectangleShape
): Modifier = this.drawBehind {
    val yOffset = offsetY.toPx()
    val outline = shape.createOutline(size, layoutDirection, this)
    translate(left = 0f, top = yOffset) {
        drawOutline(outline = outline, color = color)
    }
}

/**
 * Chunky 3dp pocket handheld outline border.
 */
fun Modifier.chunkyBorder(
    color: Color,
    width: Dp = 3.dp,
    shape: Shape = RectangleShape
): Modifier = this.border(width = width, color = color, shape = shape)
