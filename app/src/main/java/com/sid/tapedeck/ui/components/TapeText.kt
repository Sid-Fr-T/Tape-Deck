package com.sid.tapedeck.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.sid.tapedeck.ui.theme.LocalTapeColors

@Composable
fun TapeText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    color: Color = LocalTapeColors.current.ink,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    softWrap: Boolean = true,
    textAlign: TextAlign? = null
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        style = style,
        maxLines = maxLines,
        overflow = overflow,
        softWrap = softWrap,
        textAlign = textAlign
    )
}

