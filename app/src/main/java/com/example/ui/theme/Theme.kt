package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = VibrantPink,
    secondary = VibrantCyan,
    tertiary = VibrantPurple,
    background = VibrantBlack,
    surface = VibrantSurface,
    surfaceVariant = VibrantSurfaceVariant,
    onPrimary = VibrantTextPrimary,
    onSecondary = VibrantBlack,
    onTertiary = VibrantTextPrimary,
    onBackground = VibrantTextPrimary,
    onSurface = VibrantTextPrimary,
    onSurfaceVariant = VibrantTextSecondary
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force modern dark cinema UI for video experience
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(colorScheme = DarkColorScheme, typography = Typography, content = content)
}
