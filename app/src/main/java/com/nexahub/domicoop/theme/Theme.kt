package com.nexahub.domicoop.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightScheme = lightColorScheme(
    primary = CobaltPrimary,
    onPrimary = CobaltOnPrimary,
    primaryContainer = CobaltPrimaryContainer,
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF475569),
    secondaryContainer = Color(0xFFF1F5F9),
    tertiary = LightTertiary,
    tertiaryContainer = LightTertiaryContainer,
    background = LightBackground,
    surface = LightSurface,
    surfaceContainerLowest = LightSurface,
    surfaceContainerLow = LightSurfaceLow,
    surfaceContainer = LightSurfaceContainer,
    surfaceContainerHigh = LightSurfaceHigh,
    surfaceContainerHighest = LightSurfaceHighest,
    onBackground = LightOnSurface,
    onSurface = LightOnSurface,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
    error = LightError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer,
    surfaceTint = CobaltPrimary,
    inverseSurface = LightInverseSurface,
    inverseOnSurface = LightInverseOnSurface,
)

private val DarkScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = Color.White,
    primaryContainer = DarkPrimaryContainer,
    secondary = Color(0xFF4E5D87),
    tertiary = DarkTertiary,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceContainerLowest = Color(0xFF060E20),
    surfaceContainerLow = DarkSurfaceLow,
    surfaceContainer = DarkSurfaceContainer,
    surfaceContainerHigh = DarkSurfaceHigh,
    surfaceContainerHighest = DarkSurfaceHighest,
    onBackground = DarkOnSurface,
    onSurface = DarkOnSurface,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    error = DarkError,
    errorContainer = DarkErrorContainer,
    surfaceTint = DarkPrimary,
)

enum class ThemePreference { LIGHT, DARK, SYSTEM }

@Composable
fun DOMICOOPTheme(
    preference: ThemePreference = ThemePreference.SYSTEM,
    content: @Composable () -> Unit,
) {
    val systemDark = isSystemInDarkTheme()
    val darkTheme = when (preference) {
        ThemePreference.LIGHT -> false
        ThemePreference.DARK -> true
        ThemePreference.SYSTEM -> systemDark
    }
    // Dynamic color deliberately OFF: brand is Blue Cobalt Archive, not wallpaper.
    MaterialTheme(
        colorScheme = if (darkTheme) DarkScheme else LightScheme,
        typography = DomiTypography,
        content = content,
    )
}

/** True if the active theme is dark mode. */
@Composable
fun isDarkMode(): Boolean {
    return MaterialTheme.colorScheme.background == DarkBackground
}

/** Interactive/text accent — diverges in dark mode for contrast (primaryBright). */
@Composable
fun primaryBright(): Color {
    return if (isDarkMode()) DarkPrimaryBright else CobaltPrimaryBright
}

object DomiTheme {
    object colors {
        val primaryBright: Color
            @Composable get() = primaryBright()
        val success: Color
            get() = Color(0xFF22C55E)
        val warning: Color
            get() = Color(0xFFF59E0B)
        val tertiary: Color
            @Composable get() = MaterialTheme.colorScheme.tertiary
        val info: Color
            get() = Color(0xFF0EA5E9)
        val primary: Color
            @Composable get() = MaterialTheme.colorScheme.primary
        val primaryVariant: Color
            get() = Color(0xFF1E3A8A)
    }
}
