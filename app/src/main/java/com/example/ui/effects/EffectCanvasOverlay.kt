package com.example.ui.effects

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.example.model.KikaEffect
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun EffectCanvasOverlay(
    effect: KikaEffect,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "effect_anim")
    
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 650, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Pre-generate sparkle base positions for stable animation
    val sparkleSeeds = remember {
        List(40) {
            Triple(Random.nextFloat(), Random.nextFloat(), Random.nextFloat() * 360f)
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // 1. Color Tint / Hue Shift
        if (effect.hueShift > 0f) {
            val hueColor = Color.hsv(
                hue = (effect.hueShift + (if (isPlaying) time * 10f else 0f)) % 360f,
                saturation = 0.55f,
                value = 0.9f,
                alpha = 0.18f
            )
            drawRect(color = hueColor, size = size)
        }

        // 2. Glow / Cyber Bloom Border
        if (effect.glowIntensity > 0.05f) {
            val glowAlpha = (effect.glowIntensity * 0.45f * (if (isPlaying) pulse else 0.8f)).coerceIn(0f, 0.8f)
            val glowColor = Color(0xFFFF0055).copy(alpha = glowAlpha)
            val cyanGlow = Color(0xFF00F2FE).copy(alpha = glowAlpha * 0.75f)

            // Top-left and bottom-right radial auras
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(glowColor, Color.Transparent),
                    center = Offset(w * 0.1f, h * 0.15f),
                    radius = w * 0.7f
                )
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(cyanGlow, Color.Transparent),
                    center = Offset(w * 0.9f, h * 0.85f),
                    radius = w * 0.7f
                )
            )

            // Pulsing border highlight
            drawRect(
                brush = Brush.sweepGradient(
                    listOf(
                        Color(0xFFFE2C55).copy(alpha = glowAlpha),
                        Color(0xFF00F2FE).copy(alpha = glowAlpha),
                        Color(0xFF9D4EDD).copy(alpha = glowAlpha),
                        Color(0xFFFE2C55).copy(alpha = glowAlpha)
                    ),
                    center = Offset(w / 2, h / 2)
                ),
                style = Stroke(width = 6f * pulse)
            )
        }

        // 3. VHS CRT Glitch & Scanlines
        if (effect.scanlines || effect.glitchAmount > 0.1f) {
            val lineGap = 10f
            val lineCount = (h / lineGap).toInt()
            val scanlineAlpha = if (effect.scanlines) 0.15f else (effect.glitchAmount * 0.15f)
            val lineOffset = if (isPlaying) (time * 15f) % lineGap else 0f

            for (i in 0 until lineCount) {
                val y = i * lineGap + lineOffset
                drawLine(
                    color = Color.Black.copy(alpha = scanlineAlpha),
                    start = Offset(0f, y),
                    end = Offset(w, y),
                    strokeWidth = 2f
                )
            }
        }

        // Glitch chromatic displacement slices
        if (effect.glitchAmount > 0.05f) {
            val glitchPhase = (sin(time * 8.0) * 10).toInt()
            if (glitchPhase % 4 == 0) {
                val sliceY = (w * (sin(time * 5.0).toFloat() * 0.5f + 0.5f) * 1.8f) % h
                val sliceHeight = 28f * effect.glitchAmount
                val shiftX = (sin(time * 12.0).toFloat()) * 30f * effect.glitchAmount

                // Cyan chromatic shift
                drawRect(
                    color = Color(0x6600F2FE),
                    topLeft = Offset(shiftX, sliceY),
                    size = Size(w, sliceHeight)
                )
                // Magenta chromatic shift
                drawRect(
                    color = Color(0x66FE2C55),
                    topLeft = Offset(-shiftX, sliceY + 4f),
                    size = Size(w, sliceHeight * 0.8f)
                )
            }
        }

        // 4. Sparkles / Starlight particles
        if (effect.sparkleCount > 0) {
            val count = effect.sparkleCount.coerceAtMost(sparkleSeeds.size)
            for (i in 0 until count) {
                val seed = sparkleSeeds[i]
                val speed = if (isPlaying) (time * 0.5f + i * 20f) else (i * 20f)
                val sparkX = (seed.first * w + sin(speed * 0.05) * 20f).toFloat() % w
                val sparkY = (seed.second * h + (speed * 8f)) % h
                val sparkScale = ((sin(speed * 0.1) + 1.2) * 0.5).toFloat() * 12f
                val rot = (seed.third + speed * 3f) % 360f

                drawSparkleStar(
                    center = Offset(sparkX, sparkY),
                    size = sparkScale,
                    rotation = rot,
                    color = if (i % 2 == 0) Color(0xFFFFF7C2) else Color.White
                )
            }
        }

        // 5. Film Vignette
        if (effect.vignette > 0.05f) {
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Transparent,
                        Color.Black.copy(alpha = (effect.vignette * 0.85f).coerceIn(0f, 0.95f))
                    ),
                    center = Offset(w / 2, h / 2),
                    radius = (w * 0.75f).coerceAtLeast(100f)
                ),
                size = size
            )
        }
    }
}

private fun DrawScope.drawSparkleStar(
    center: Offset,
    size: Float,
    rotation: Float,
    color: Color
) {
    if (size <= 1f) return
    rotate(rotation, pivot = center) {
        val path = Path().apply {
            moveTo(center.x, center.y - size)
            quadraticTo(center.x, center.y, center.x + size, center.y)
            quadraticTo(center.x, center.y, center.x, center.y + size)
            quadraticTo(center.x, center.y, center.x - size, center.y)
            quadraticTo(center.x, center.y, center.x, center.y - size)
            close()
        }
        drawPath(path = path, color = color)
        drawCircle(
            color = Color.White.copy(alpha = 0.9f),
            radius = size * 0.25f,
            center = center
        )
    }
}
