package com.sid.tapedeck.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sid.tapedeck.ui.theme.TapeTheme
import com.sid.tapedeck.ui.theme.chunkyBorder
import com.sid.tapedeck.ui.theme.hardShadow

/**
 * TapeCard: bg card, 3dp line border, radius 22dp, hardShadow 5dp.
 */
@Composable
fun TapeCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = TapeTheme.colors.card,
    borderColor: Color = TapeTheme.colors.line,
    shadowColor: Color = TapeTheme.colors.line,
    radius: Dp = 22.dp,
    shadowOffset: Dp = 5.dp,
    shape: Shape = RoundedCornerShape(radius),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .hardShadow(color = shadowColor, offsetY = shadowOffset, shape = shape)
            .background(color = backgroundColor, shape = shape)
            .chunkyBorder(color = borderColor, width = 3.dp, shape = shape),
        content = content
    )
}

