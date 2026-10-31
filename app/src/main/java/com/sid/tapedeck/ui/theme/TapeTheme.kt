package com.sid.tapedeck.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class ThemeMode {
    Light,
    Dark,
    System
}

@Composable
fun TapeTheme(
    themeMode: ThemeMode = ThemeMode.System,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
        ThemeMode.System -> isSystemInDarkTheme()
    }

    // Fixed palette without Material You dynamic colors
    val colors = if (isDark) DarkTapeColors else LightTapeColors
    val typography = TapeTypography()

    val view = LocalView.current
    if (!view.isInEditMode) {
        @Suppress("DEPRECATION")
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = colors.bg.toArgb()
            window.navigationBarColor = colors.nav.toArgb()

            val insetsController = WindowCompat.getInsetsController(window, view)
            // Light background gets dark status bar icons; dark background gets light icons
            insetsController.isAppearanceLightStatusBars = !isDark
            // Docked nav is dark slate (#3A3C4A) in light and pitch black (#0F1016) in dark
            // Navigation bar icons stay light in both themes
            insetsController.isAppearanceLightNavigationBars = false
        }
    }

    val materialScheme = if (isDark) {
        darkColorScheme(
            primary = colors.pink,
            onPrimary = colors.onPink,
            background = colors.bg,
            onBackground = colors.ink,
            surface = colors.card,
            onSurface = colors.ink,
            outline = colors.line
        )
    } else {
        lightColorScheme(
            primary = colors.pink,
            onPrimary = colors.onPink,
            background = colors.bg,
            onBackground = colors.ink,
            surface = colors.card,
            onSurface = colors.ink,
            outline = colors.line
        )
    }

    CompositionLocalProvider(
        LocalTapeColors provides colors,
        LocalTapeTypography provides typography
    ) {
        MaterialTheme(
            colorScheme = materialScheme
        ) {
            ProvideTextStyle(value = TapeType.body) {
                content()
            }
        }
    }
}

object TapeTheme {
    val colors: TapeColors
        @Composable
        @ReadOnlyComposable
        get() = LocalTapeColors.current

    val typography: TapeTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalTapeTypography.current
}
