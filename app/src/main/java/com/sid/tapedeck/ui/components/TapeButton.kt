package com.sid.tapedeck.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import com.sid.tapedeck.ui.theme.TapeType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sid.tapedeck.ui.theme.TapeTheme
import com.sid.tapedeck.ui.theme.chunkyBorder
import com.sid.tapedeck.ui.theme.hardShadow

/**
 * TapeButton: 3dp border, radius 16dp, height 52dp, hardShadow 5dp, ExtraBold 17sp.
 * Pressed state: shadow collapses and the button moves down by the shadow offset. No ripple.
 */
@Composable
fun TapeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = TapeTheme.colors.pink,
    textColor: Color = TapeTheme.colors.onPink,
    borderColor: Color = TapeTheme.colors.line,
    shadowColor: Color = TapeTheme.colors.line,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    radius: Dp = 16.dp,
    shadowDistance: Dp = 5.dp,
    fillMaxWidth: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val currentYOffset = if (isPressed) shadowDistance else 0.dp
    val currentShadow = if (isPressed) 0.dp else shadowDistance
    val shape = RoundedCornerShape(radius)

    val widthModifier = if (fillMaxWidth) Modifier.fillMaxWidth() else Modifier

    Box(
        modifier = modifier
            .then(widthModifier)
            .height(52.dp)
            .offset(y = currentYOffset)
            .hardShadow(color = shadowColor, offsetY = currentShadow, shape = shape)
            .background(color = backgroundColor, shape = shape)
            .chunkyBorder(color = borderColor, width = 3.dp, shape = shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null, // Strictly no ripple animation beyond tactile press
                onClick = onClick
            )
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            TapeText(
                text = text,
                style = TapeType.button,
                color = textColor
            )

            if (trailingIcon != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

/**
 * TapeIconButton: circle, 3dp border, same press effect (sizes 40 to 88dp, optional rounded-square variant with radius 28dp).
 */
@Composable
fun TapeIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    backgroundColor: Color = TapeTheme.colors.card,
    iconTint: Color = TapeTheme.colors.ink,
    borderColor: Color = TapeTheme.colors.line,
    shadowColor: Color = TapeTheme.colors.line,
    shadowDistance: Dp = 4.dp,
    isSquare: Boolean = false,
    squareRadius: Dp = 28.dp,
    contentDescription: String? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val currentYOffset = if (isPressed) shadowDistance else 0.dp
    val currentShadow = if (isPressed) 0.dp else shadowDistance
    val shape: Shape = if (isSquare) RoundedCornerShape(squareRadius) else CircleShape

    Box(
        modifier = modifier
            .size(size)
            .offset(y = currentYOffset)
            .hardShadow(color = shadowColor, offsetY = currentShadow, shape = shape)
            .background(color = backgroundColor, shape = shape)
            .chunkyBorder(color = borderColor, width = 3.dp, shape = shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(size * 0.5f)
        )
    }
}

