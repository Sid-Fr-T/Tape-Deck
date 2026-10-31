package com.sid.tapedeck.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Immutable
data class TapeTypography(
    // Space Mono Bold (headings, uppercase)
    val header: TextStyle = TextStyle(
        fontFamily = SpaceMonoBoldFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 32.sp
    ),
    val topBar: TextStyle = TextStyle(
        fontFamily = SpaceMonoBoldFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    val section: TextStyle = TextStyle(
        fontFamily = SpaceMonoBoldFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    val albumTitle: TextStyle = TextStyle(
        fontFamily = SpaceMonoBoldFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    // Monospace label / duration / counters
    val mono: TextStyle = TextStyle(
        fontFamily = SpaceMonoBoldFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),

    // Figtree text styles (ExtraBold, Bold, and Medium)
    val nowPlayingTitle: TextStyle = TextStyle(
        fontFamily = FigtreeFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 28.sp,
        lineHeight = 34.sp
    ),
    val nowPlayingArtist: TextStyle = TextStyle(
        fontFamily = FigtreeFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 17.sp,
        lineHeight = 22.sp
    ),
    val rowTitle: TextStyle = TextStyle(
        fontFamily = FigtreeFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    val chip: TextStyle = TextStyle(
        fontFamily = FigtreeFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    val button: TextStyle = TextStyle(
        fontFamily = FigtreeFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 17.sp,
        lineHeight = 22.sp
    ),
    val artist: TextStyle = TextStyle(
        fontFamily = FigtreeFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        lineHeight = 18.sp
    ),
    val cardTitle: TextStyle = TextStyle(
        fontFamily = FigtreeFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        lineHeight = 22.sp
    ),
    val subtitle: TextStyle = TextStyle(
        fontFamily = FigtreeFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp
    ),
    val body: TextStyle = TextStyle(
        fontFamily = FigtreeFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
)

val TapeType = TapeTypography()

val LocalTapeTypography = staticCompositionLocalOf { TapeTypography() }
