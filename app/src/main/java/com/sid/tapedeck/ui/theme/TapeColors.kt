package com.sid.tapedeck.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class TapeColors(
    val bg: Color,
    val card: Color,
    val ink: Color,
    val muted: Color,
    val line: Color,
    val nav: Color,
    val onNav: Color,
    val pink: Color,
    val onPink: Color,
    val pinkText: Color,
    val track: Color,
    // Fixed accents in both themes
    val lime: Color = Color(0xFFB5C77A),
    val lime2: Color = Color(0xFF6F8240),
    val lime3: Color = Color(0xFF3F4F25),
    val lime4: Color = Color(0xFF8DA05A),
    val yellow: Color = Color(0xFFF2C94C),
    val rose: Color = Color(0xFFF08FA8),
    val onAccent: Color = Color(0xFF23242E)
)

val LightTapeColors = TapeColors(
    bg = Color(0xFFDAD3C6),
    card = Color(0xFFF2EEE3),
    ink = Color(0xFF2F3140),
    muted = Color(0xFF4E4F5A),
    line = Color(0xFF2F3140),
    nav = Color(0xFF3A3C4A),
    onNav = Color(0xFFE9E4D6),
    pink = Color(0xFFD4306B),
    onPink = Color(0xFFFFFFFF),
    pinkText = Color(0xFFB01F55),
    track = Color(0xFFF2EEE3)
)

val DarkTapeColors = TapeColors(
    bg = Color(0xFF1B1D26),
    card = Color(0xFF2A2D3A),
    ink = Color(0xFFF2EEE3),
    muted = Color(0xFFB9B8C4),
    line = Color(0xFF0A0B0F),
    nav = Color(0xFF0F1016),
    onNav = Color(0xFFCFCBBE),
    pink = Color(0xFFFF6B97),
    onPink = Color(0xFF14151B),
    pinkText = Color(0xFFFF8FB0),
    track = Color(0xFF14151B)
)

val LocalTapeColors = staticCompositionLocalOf { LightTapeColors }

