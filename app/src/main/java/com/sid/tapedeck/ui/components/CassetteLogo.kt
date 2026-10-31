package com.sid.tapedeck.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sid.tapedeck.ui.theme.TapeTheme
import com.sid.tapedeck.ui.theme.chunkyBorder

@Composable
fun CassetteLogo(modifier: Modifier = Modifier) {
    val colors = TapeTheme.colors
    val shape = RoundedCornerShape(8.dp)
    Box(
        modifier = modifier
            .size(width = 46.dp, height = 34.dp)
            .background(colors.lime, shape)
            .chunkyBorder(colors.line, width = 3.dp, shape = shape),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(colors.ink, CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(colors.ink, CircleShape)
            )
        }
    }
}

