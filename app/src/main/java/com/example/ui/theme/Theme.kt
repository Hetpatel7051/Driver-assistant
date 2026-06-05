package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = HighDensityPrimary,
    onPrimary = Color.White,
    secondary = HighDensitySecondary,
    onSecondary = Color.White,
    tertiary = HighDensityTertiary,
    onTertiary = Color.White,
    background = Color(0xFF12141C), // Deep midnight background
    onBackground = Color(0xFFF1F5F9), // Light text
    surface = Color(0xFF1E2230), // Mid-midnight surface container
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF2A2E3F), 
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF384156)
)

private val LightColorScheme = lightColorScheme(
    primary = HighDensityPrimary,
    onPrimary = Color.White,
    secondary = HighDensitySecondary,
    onSecondary = Color.White,
    tertiary = HighDensityTertiary,
    onTertiary = Color.White,
    background = HighDensityBg, // F7F9FC
    onBackground = HighDensityText, // 1C1B1F
    surface = HighDensitySurface, // White
    onSurface = HighDensityText,
    surfaceVariant = DenseGreyBg, // F3F4F6
    onSurfaceVariant = HighDensityMuted, // 49454F
    outline = CardBorderColor // E2E8F0
)

@Composable
fun SafarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
