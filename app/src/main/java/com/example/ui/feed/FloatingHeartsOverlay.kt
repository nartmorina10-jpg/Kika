package com.example.ui.feed

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import kotlin.random.Random

data class HeartBurst(
    val id: Long = System.currentTimeMillis() + Random.nextLong(1000),
    val offsetX: Float = (Random.nextFloat() - 0.5f) * 160f,
    val offsetY: Float = (Random.nextFloat() - 0.5f) * 160f,
    val rotation: Float = (Random.nextFloat() - 0.5f) * 45f
)

@Composable
fun FloatingHeartsOverlay(
    bursts: List<HeartBurst>,
    onRemoveBurst: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        bursts.forEach { burst ->
            SingleHeartBurst(burst = burst, onComplete = { onRemoveBurst(burst.id) })
        }
    }
}

@Composable
private fun SingleHeartBurst(
    burst: HeartBurst,
    onComplete: () -> Unit
) {
    val scale = remember { Animatable(0.2f) }
    val alpha = remember { Animatable(1f) }
    val yOffset = remember { Animatable(burst.offsetY) }

    LaunchedEffect(burst.id) {
        // Pop in scale
        scale.animateTo(
            targetValue = 1.3f,
            animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
        )
        // Float upward and fade out
        scale.animateTo(
            targetValue = 1.1f,
            animationSpec = tween(durationMillis = 300)
        )
        alpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 400)
        )
        onComplete()
    }

    LaunchedEffect(burst.id) {
        yOffset.animateTo(
            targetValue = burst.offsetY - 120f,
            animationSpec = tween(durationMillis = 800)
        )
    }

    Icon(
        imageVector = Icons.Default.Favorite,
        contentDescription = "Floating Like Heart",
        tint = Color(0xFFFE2C55),
        modifier = Modifier
            .offset { IntOffset(burst.offsetX.roundToInt(), yOffset.value.roundToInt()) }
            .rotate(burst.rotation)
            .scale(scale.value)
            .alpha(alpha.value)
            .size(90.dp)
    )
}
