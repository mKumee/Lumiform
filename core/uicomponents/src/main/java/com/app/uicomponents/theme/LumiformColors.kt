package com.app.uicomponents.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class LumiformColorScheme(
    val background: Color,
    val cardSurface: Color,
    val accent: Color,
    val accentSoft: Color,
    val accentTint: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val onAccent: Color,
    val shadowAmbient: Color,
    val shadowSpot: Color,
    val errorContainer: Color,
    val onErrorContainer: Color
)

val LightContentViewerColors = LumiformColorScheme(
    background = LightPalette.background,
    cardSurface = LightPalette.cardSurface,
    accent = LightPalette.accent,
    accentSoft = LightPalette.accentSoft,
    accentTint = LightPalette.accentTint,
    textPrimary = LightPalette.textPrimary,
    textSecondary = LightPalette.textSecondary,
    textMuted = LightPalette.textMuted,
    onAccent = LightPalette.onAccent,
    shadowAmbient = LightPalette.shadowAmbient,
    shadowSpot = LightPalette.shadowSpot,
    errorContainer = LightPalette.errorContainer,
    onErrorContainer = LightPalette.onErrorContainer
)

val DarkContentViewerColors = LumiformColorScheme(
    background = DarkPalette.background,
    cardSurface = DarkPalette.cardSurface,
    accent = DarkPalette.accent,
    accentSoft = DarkPalette.accentSoft,
    accentTint = DarkPalette.accentTint,
    textPrimary = DarkPalette.textPrimary,
    textSecondary = DarkPalette.textSecondary,
    textMuted = DarkPalette.textMuted,
    onAccent = DarkPalette.onAccent,
    shadowAmbient = DarkPalette.shadowAmbient,
    shadowSpot = DarkPalette.shadowSpot,
    errorContainer = DarkPalette.errorContainer,
    onErrorContainer = DarkPalette.onErrorContainer
)

val LocalContentViewerColors = staticCompositionLocalOf { LightContentViewerColors }

object LumiformColors {
    val current: LumiformColorScheme
        @Composable get() = LocalContentViewerColors.current
}
