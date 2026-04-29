package com.hikari.anime.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = HikariPurple,
    onPrimary = HikariSurface,
    secondary = HikariPink,
    tertiary = HikariCyan,
    background = HikariBackground,
    onBackground = HikariInk,
    surface = HikariSurface,
    onSurface = HikariInk,
    surfaceVariant = HikariSurfaceSoft,
    onSurfaceVariant = HikariMuted
)

private val DarkColorScheme = darkColorScheme(
    primary = HikariPurpleLight,
    onPrimary = HikariDarkBackground,
    secondary = HikariPink,
    tertiary = HikariCyan,
    background = HikariDarkBackground,
    onBackground = HikariSurface,
    surface = HikariDarkSurface,
    onSurface = HikariSurface,
    surfaceVariant = HikariDarkSurface2,
    onSurfaceVariant = HikariMuted
)

@Composable
fun HikariTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = HikariTypography,
        shapes = HikariShapes,
        content = content
    )
}
