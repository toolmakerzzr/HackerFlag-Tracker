package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val HackerflagLightColorScheme = lightColorScheme(
    primary = CyberCyan,
    onPrimary = Color.White,
    primaryContainer = CyberSurfaceVariant,
    onPrimaryContainer = CyberCyan,
    secondary = CyberGold,
    onSecondary = Color.White,
    secondaryContainer = CyberSurfaceVariant,
    onSecondaryContainer = CyberGoldDark,
    tertiary = CyberAlertCrimson,
    onTertiary = Color.White,
    background = CyberBackground,
    onBackground = CyberTextPrimary,
    surface = CyberSurface,
    onSurface = CyberTextPrimary,
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = CyberTextSecondary,
    outline = CyberBorder,
    error = CyberAlertCrimson,
    onError = Color.White
)

@Composable
fun HackerflagTheme(
    darkTheme: Boolean = false, // Ultra Light UI
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = HackerflagLightColorScheme,
        typography = Typography,
        content = content
    )
}
