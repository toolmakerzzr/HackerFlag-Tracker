package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val HackerflagDarkColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = CyberBackground,
    primaryContainer = CyberSurfaceVariant,
    onPrimaryContainer = CyberCyan,
    secondary = CyberGold,
    onSecondary = CyberBackground,
    secondaryContainer = CyberGoldDark,
    onSecondaryContainer = CyberGold,
    tertiary = CyberAlertCrimson,
    onTertiary = CyberTextPrimary,
    background = CyberBackground,
    onBackground = CyberTextPrimary,
    surface = CyberSurface,
    onSurface = CyberTextPrimary,
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = CyberTextSecondary,
    outline = CyberBorder,
    error = CyberAlertCrimson,
    onError = CyberTextPrimary
)

@Composable
fun HackerflagTheme(
    darkTheme: Boolean = true, // Cyberpunk theme looks best in deep dark mode
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = HackerflagDarkColorScheme,
        typography = Typography,
        content = content
    )
}
