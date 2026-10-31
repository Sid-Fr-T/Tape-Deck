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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sid.tapedeck.ui.components.TapeButton
import com.sid.tapedeck.ui.components.TapeCard
import com.sid.tapedeck.ui.components.TapeIcons
import com.sid.tapedeck.ui.components.TapeText
import com.sid.tapedeck.ui.theme.TapeTheme
import com.sid.tapedeck.ui.theme.TapeType
import com.sid.tapedeck.ui.theme.chunkyBorder

@Composable
fun PermissionScreen(
    onRequestPermission: () -> Unit
) {
    val colors = TapeTheme.colors
    val typography = TapeTheme.typography

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg)
            .statusBarsPadding()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        TapeCard(
            modifier = Modifier.fillMaxWidth(),
            radius = 22.dp,
            shadowOffset = 5.dp
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TapeText(
                        text = "DECK ACCESS",
                        style = TapeType.topBar,
                        color = colors.pinkText
                    )
                    Box(
                        modifier = Modifier
                            .background(colors.lime)
                            .chunkyBorder(colors.line, width = 2.dp)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        TapeText(
                            text = "OFFLINE ONLY",
                            style = TapeType.topBar,
                            color = colors.onAccent
                        )
                    }
                }

                TapeText(
                    text = "LOAD YOUR CASSETTES",
                    style = TapeType.header,
                    color = colors.ink
                )

                TapeText(
                    text = "Tapedeck plays audio files directly from your phone's storage. There are no internet requests, no streaming, and no analytics.",
                    style = TapeType.body,
                    color = colors.muted
                )

                TapeText(
                    text = "Grant audio access so Tapedeck can scan and index your local tracks, albums, and artists.",
                    style = TapeType.body,
                    color = colors.ink
                )

                Spacer(modifier = Modifier.height(6.dp))

                TapeButton(
                    text = "ALLOW ACCESS",
                    leadingIcon = TapeIcons.Library,
                    onClick = onRequestPermission
                )
            }
        }
    }
}

