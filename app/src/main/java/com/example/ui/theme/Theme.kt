package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CyberDarkColorScheme = darkColorScheme(
    primary = CyberGreenPrimary,
    onPrimary = Color.Black,
    primaryContainer = CyberGreenDark,
    onPrimaryContainer = CyberGreenGlow,
    secondary = CyberCyanAccent,
    onSecondary = Color.Black,
    secondaryContainer = CyberDarkBgAlt,
    onSecondaryContainer = CyberCyanAccent,
    tertiary = CyberRedAlert,
    onTertiary = Color.White,
    background = CyberDarkBg,
    onBackground = CyberTextWhite,
    surface = CyberGlassCard,
    onSurface = CyberTextWhite,
    surfaceVariant = CyberDarkBgAlt,
    onSurfaceVariant = CyberTextDim,
    outline = CyberGlassBorder,
    error = CyberRedAlert,
    onError = Color.White
)

@Composable
fun CyberShieldTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CyberDarkColorScheme,
        typography = Typography,
        content = content
    )
}

// Keep HackerflagTheme alias so old references still compile cleanly if needed
@Composable
fun HackerflagTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    CyberShieldTheme(content = content)
}
