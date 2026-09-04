package com.example.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun VibrantAtmosphereBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .drawBehind {
                // 1. Base Multi-stop Mesh Gradient: purple-900 via slate-900 to indigo-900
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            VibrantPurple900.copy(alpha = 0.85f),
                            VibrantSlate900.copy(alpha = 0.90f),
                            VibrantIndigo900.copy(alpha = 0.85f)
                        ),
                        start = Offset(size.width, 0f),
                        end = Offset(0f, size.height)
                    )
                )

                // 2. Glowing Pink Ambient Orb (top-left quadrant)
                val pinkCenter = Offset(size.width * 0.30f, size.height * 0.28f)
                val pinkRadius = size.width * 0.45f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            VibrantPink.copy(alpha = 0.22f),
                            VibrantPink.copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        center = pinkCenter,
                        radius = pinkRadius
                    ),
                    radius = pinkRadius,
                    center = pinkCenter
                )

                // 3. Glowing Cyan Ambient Orb (bottom-right quadrant)
                val cyanCenter = Offset(size.width * 0.72f, size.height * 0.68f)
                val cyanRadius = size.width * 0.45f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            VibrantCyan.copy(alpha = 0.20f),
                            VibrantCyan.copy(alpha = 0.06f),
                            Color.Transparent
                        ),
                        center = cyanCenter,
                        radius = cyanRadius
                    ),
                    radius = cyanRadius,
                    center = cyanCenter
                )
            },
        content = content
    )
}
