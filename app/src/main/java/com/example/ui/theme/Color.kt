package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Vibrant Palette Primary Accents (from Design HTML: pink-500, yellow-500, cyan-400, indigo-500)
val VibrantPink = Color(0xFFEC4899)
val VibrantPinkDark = Color(0xFFDB2777)
val VibrantYellow = Color(0xFFEAB308)
val VibrantYellowLight = Color(0xFFFACC15)
val VibrantCyan = Color(0xFF22D3EE)
val VibrantCyanDeep = Color(0xFF06B6D4)
val VibrantIndigo = Color(0xFF6366F1)
val VibrantPurple = Color(0xFF9333EA)

// Vibrant Atmospheric Gradient Backdrops (purple-900 via slate-900 to indigo-900)
val VibrantPurple900 = Color(0xFF3B0764)
val VibrantPurple800 = Color(0xFF581C87)
val VibrantSlate900 = Color(0xFF0F172A)
val VibrantIndigo900 = Color(0xFF1E1B4B)
val VibrantDarkBg = Color(0xFF080612)
val VibrantBlack = Color(0xFF05050A)
val VibrantSurface = Color(0xFF130E26)
val VibrantSurfaceVariant = Color(0xFF1E1738)
val VibrantSurfaceElevated = Color(0xFF291E48)
val VibrantBorder = Color(0x26FFFFFF)
val VibrantTextPrimary = Color(0xFFFFFFFF)
val VibrantTextSecondary = Color(0xB3FFFFFF)

// Standard App Theme Aliases mapped to Vibrant Palette
val KikaPink = VibrantPink
val KikaCyan = VibrantCyan
val KikaViolet = VibrantPurple
val KikaYellow = VibrantYellow

val KikaBackground = VibrantDarkBg
val KikaSurface = VibrantSurface
val KikaSurfaceVariant = VibrantSurfaceVariant
val KikaOnSurface = Color(0xFFFFFFFF)
val KikaOnSurfaceSecondary = Color(0xCCFFFFFF)
val KikaBorder = VibrantBorder

// Reusable Vibrant Palette Gradients
val VibrantMeshGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF3B0764),
        Color(0xFF0F172A),
        Color(0xFF1E1B4B)
    )
)

val VibrantBoltGradient = Brush.linearGradient(
    colors = listOf(VibrantPink, VibrantYellow)
)

val VibrantAvatarGradient = Brush.linearGradient(
    colors = listOf(VibrantYellowLight, VibrantPink)
)

val VibrantDiscGradient = Brush.linearGradient(
    colors = listOf(VibrantPink, VibrantIndigo)
)

val VibrantButtonHaloGradient = Brush.horizontalGradient(
    colors = listOf(VibrantCyan, VibrantPink, VibrantYellow)
)

val VibrantCardGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF24153D), Color(0xFF130E26))
)

