package com.sid.tapedeck.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sid.tapedeck.ui.components.TapeButton
import com.sid.tapedeck.ui.components.TapeCard
import com.sid.tapedeck.ui.components.TapeChip
import com.sid.tapedeck.ui.components.TapeIconButton
import com.sid.tapedeck.ui.components.TapeIcons
import com.sid.tapedeck.ui.components.TapeText
import com.sid.tapedeck.ui.theme.TapeTheme
import com.sid.tapedeck.ui.theme.TapeType
import com.sid.tapedeck.ui.theme.ThemeMode

@Composable
fun YouScreen(
    currentTheme: ThemeMode,
    onSelectTheme: (ThemeMode) -> Unit
) {
    val colors = TapeTheme.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TapeText(text = "SETTINGS & PROFILE", style = TapeType.header, color = colors.ink)

        // Theme Switcher Card
        TapeCard(
            modifier = Modifier.fillMaxWidth(),
            radius = 20.dp,
            shadowOffset = 5.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TapeText(text = "DISPLAY THEME", style = TapeType.topBar, color = colors.pinkText)
                TapeText(
                    text = "Switch between Light, Dark, or System follow. No Material You gradients or blurs.",
                    style = TapeType.body,
                    color = colors.muted
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ThemeMode.entries.forEach { mode ->
                        TapeChip(
                            text = mode.name.uppercase(),
                            isSelected = currentTheme == mode,
                            onClick = { onSelectTheme(mode) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Specs & Hardware card
        TapeCard(
            modifier = Modifier.fillMaxWidth(),
            radius = 20.dp,
            shadowOffset = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TapeText(text = "TAPEDECK HARDWARE PROFILE", style = TapeType.topBar, color = colors.ink)
                TapeText(text = "Engine: Media3 ExoPlayer (Offline)", style = TapeType.body, color = colors.muted)
                TapeText(text = "Outline: 3dp chunky line", style = TapeType.body, color = colors.muted)
                TapeText(text = "Shadows: 5dp hard offset (no blur)", style = TapeType.body, color = colors.muted)
                TapeText(text = "Memory: Low-memory optimized (<2GB JVM)", style = TapeType.body, color = colors.muted)
            }
        }
    }
}

@Composable
fun LyricsScreen(onBack: () -> Unit) {
    val colors = TapeTheme.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TapeIconButton(icon = TapeIcons.Back, size = 44.dp, onClick = onBack)
            TapeText(text = "LYRICS", style = TapeType.header, color = colors.ink)
            TapeIconButton(icon = TapeIcons.Heart, size = 44.dp, onClick = {})
        }

        TapeCard(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            radius = 20.dp,
            shadowOffset = 5.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                TapeText(text = "SYNTHWAVE DREAMS", style = TapeType.albumTitle, color = colors.ink)
                TapeText(text = "[Verse 1]", style = TapeType.topBar, color = colors.pinkText)
                TapeText(
                    text = "City lights in the rear-view mirror,\nMidnight hums through the cassette player,\nAnalog lines across the sky,\nWe ride until the morning light.",
                    style = TapeType.rowTitle,
                    color = colors.ink
                )
                TapeText(text = "[Chorus]", style = TapeType.topBar, color = colors.pinkText)
                TapeText(
                    text = "Keep the tape rolling, rewind the night,\nTurn the dial till the signal is right,\nNo blur, no noise, just analog sound,\nIn this pocket handheld we found.",
                    style = TapeType.rowTitle,
                    color = colors.ink
                )
            }
        }
    }
}
