package com.sid.tapedeck.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.sid.tapedeck.ui.theme.TapeTheme
import com.sid.tapedeck.ui.theme.TapeType
import com.sid.tapedeck.ui.theme.chunkyBorder
import com.sid.tapedeck.ui.theme.hardShadow

/**
 * TapeChip: 40dp height, 3dp border, hard shadow, 14sp ExtraBold, 4dp horizontal padding.
 * Content is centered, single line (maxLines = 1, softWrap = false), textAlign = Center.
 * Selected = lime background with onAccent text.
 */
@Composable
fun TapeChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = TapeTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val bg = if (isSelected) colors.lime else colors.card
    val textColor = if (isSelected) colors.onAccent else colors.ink
    val shape = RoundedCornerShape(16.dp)

    val shadowDist = if (isSelected) 3.dp else 2.dp
    val currentYOffset = if (isPressed) shadowDist else 0.dp
    val currentShadow = if (isPressed) 0.dp else shadowDist

    val density = LocalDensity.current
    val effectiveFontScale = remember(density.fontScale) {
        // Ensures 9-letter words like "Playlists" do not wrap or clip on a 360dp width screen at largest font size
        density.fontScale.coerceAtMost(1.05f)
    }

    Box(
        modifier = modifier
            .height(40.dp)
            .offset(y = currentYOffset)
            .hardShadow(color = colors.line, offsetY = currentShadow, shape = shape)
            .background(color = bg, shape = shape)
            .chunkyBorder(color = colors.line, width = 3.dp, shape = shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(
            LocalDensity provides Density(density.density, fontScale = effectiveFontScale)
        ) {
            TapeText(
                text = text,
                style = TapeType.chip,
                color = textColor,
                maxLines = 1,
                softWrap = false,
                textAlign = TextAlign.Center
            )
        }
    }
}
