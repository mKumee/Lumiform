package com.app.uicomponents.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.app.core.model.ThemeMode

private val LightColorScheme = lightColorScheme(
    primary = LightPalette.accent,
    secondary = LightPalette.accentSoft,
    background = LightPalette.background,
    surface = LightPalette.cardSurface,
    onBackground = LightPalette.textPrimary,
    onSurface = LightPalette.textPrimary,
    errorContainer = LightPalette.errorContainer,
    onErrorContainer = LightPalette.onErrorContainer
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPalette.accent,
    secondary = DarkPalette.accentSoft,
    background = DarkPalette.background,
    surface = DarkPalette.cardSurface,
    onBackground = DarkPalette.textPrimary,
    onSurface = DarkPalette.textPrimary,
    errorContainer = DarkPalette.errorContainer,
    onErrorContainer = DarkPalette.onErrorContainer
)

@Composable
fun LumiformTheme(
    themeMode: ThemeMode,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        ThemeMode.LIGHT -> LightColorScheme
        ThemeMode.DARK -> DarkColorScheme
    }
    val extendedColors = when (themeMode) {
        ThemeMode.LIGHT -> LightContentViewerColors
        ThemeMode.DARK -> DarkContentViewerColors
    }

    CompositionLocalProvider(LocalContentViewerColors provides extendedColors) {
        MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
    }
}
