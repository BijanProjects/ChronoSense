package com.chronosense.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Indigo500,
    onPrimary = Slate50,
    primaryContainer = Indigo100,
    onPrimaryContainer = Indigo900,
    secondary = Amber500,
    onSecondary = Slate900,
    secondaryContainer = Amber300,
    onSecondaryContainer = Slate900,
    background = Slate50,
    onBackground = Slate900,
    surface = Slate50,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate600,
    outline = Slate300,
    outlineVariant = Slate200
)

private val DarkColorScheme = darkColorScheme(
    primary = Indigo400,
    onPrimary = Indigo900,
    primaryContainer = Indigo800,
    onPrimaryContainer = Indigo100,
    secondary = Amber400,
    onSecondary = Slate900,
    secondaryContainer = Amber500,
    onSecondaryContainer = Slate50,
    background = DarkBackground,
    onBackground = Slate100,
    surface = DarkSurface,
    onSurface = Slate100,
    surfaceVariant = DarkCard,
    onSurfaceVariant = Slate400,
    outline = Slate600,
    outlineVariant = Slate700
)

/**
 * ChronoSense Material 3 theme.
 *
 * @param darkTheme    follow system dark mode by default
 * @param dynamicColor use Material You dynamic colour on Android 12+
 */
@Composable
fun ChronoSenseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val ctx = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ChronoTypography,
        content = content
    )
}
