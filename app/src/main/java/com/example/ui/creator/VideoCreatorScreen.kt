package com.example.ui.creator

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.KikaRepository
import com.example.model.KikaEffect
import com.example.model.SoundTrack
import com.example.ui.effects.EffectCanvasOverlay
import com.example.ui.theme.VibrantCyan
import com.example.ui.theme.VibrantPink
import com.example.ui.theme.VibrantSurface
import com.example.ui.theme.VibrantSurfaceVariant
import com.example.ui.theme.VibrantYellow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun VideoCreatorScreen(
    initialEffect: KikaEffect? = null,
    onClose: () -> Unit,
    onVideoPublished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val effects by KikaRepository.effects.collectAsState()

    var selectedEffect by remember {
        mutableStateOf(initialEffect ?: effects.firstOrNull() ?: KikaRepository.defaultEffects[0])
    }
    var selectedSound by remember { mutableStateOf(KikaRepository.soundTracks[0]) }
    var isRecording by remember { mutableStateOf(false) }
    var recordingProgress by remember { mutableFloatStateOf(0f) }
    var recordedDurationMs by remember { mutableFloatStateOf(0f) }
    val maxDurationMs = 15000f // 15 seconds clip

    var hasRecordedSegment by remember { mutableStateOf(false) }
    var isPreviewMode by remember { mutableStateOf(false) }
    var postCaption by remember { mutableStateOf("") }
    var showSoundPicker by remember { mutableStateOf(false) }

    // Side tool states
    var isFlashOn by remember { mutableStateOf(false) }
    var selectedSpeed by remember { mutableFloatStateOf(1.0f) }
    var timerCountdownSec by remember { mutableIntStateOf(0) }
    var currentCountdown by remember { mutableIntStateOf(0) }

    // Studio simulated viewfinder background scenes
    val cameraScenes = listOf(
        R.drawable.vid_cyber_dancer,
        R.drawable.vid_glitch_vfx,
        R.drawable.vid_skater_sunset,
        R.drawable.vid_synthwave_art
    )
    var selectedSceneIndex by remember { mutableIntStateOf(0) }

    // Viewfinder motion loop
    val infiniteTransition = rememberInfiniteTransition(label = "studio_viewfinder")
    val cameraMotion by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cam_motion"
    )

    // Recording timer loop
    LaunchedEffect(isRecording) {
        if (isRecording) {
            val stepMs = 50L
            while (isRecording && recordedDurationMs < maxDurationMs) {
                delay(stepMs)
                recordedDurationMs += stepMs * selectedSpeed
                recordingProgress = (recordedDurationMs / maxDurationMs).coerceIn(0f, 1f)
            }
            if (recordedDurationMs >= maxDurationMs) {
                isRecording = false
                hasRecordedSegment = true
                isPreviewMode = true
            }
        }
    }

    // Countdown timer logic
    fun startRecordingWithTimer() {
        if (timerCountdownSec > 0) {
            scope.launch {
                currentCountdown = timerCountdownSec
                while (currentCountdown > 0) {
                    delay(1000)
                    currentCountdown -= 1
                }
                isRecording = true
                hasRecordedSegment = true
            }
        } else {
            isRecording = true
            hasRecordedSegment = true
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 1. Live Camera / Viewfinder Visual
        Image(
            painter = painterResource(id = cameraScenes[selectedSceneIndex]),
            contentDescription = "Camera Viewfinder",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .scale(if (isRecording) cameraMotion else 1.0f)
        )

        // 2. Real-time Live VFX Filter on Viewfinder
        EffectCanvasOverlay(
            effect = selectedEffect,
            isPlaying = true,
            modifier = Modifier.fillMaxSize()
        )

        // Viewfinder dark vignette & grid guides
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.5f),
                            Color.Transparent,
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        // 3. Countdown Overlay Display
        if (currentCountdown > 0) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentCountdown.toString(),
                    color = VibrantPink,
                    fontSize = 110.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        // =====================
        // PREVIEW / POST MODE
        // =====================
        if (isPreviewMode) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.75f))
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .clip(RoundedCornerShape(20.dp))
                        .background(VibrantSurface)
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Post Your Kika Vid",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                        IconButton(onClick = { isPreviewMode = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Back", tint = Color.LightGray)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Effect & Sound summary chip
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(VibrantPink.copy(alpha = 0.2f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Effect: ${selectedEffect.name}",
                                color = VibrantPink,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(VibrantCyan.copy(alpha = 0.2f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Sound: ${selectedSound.title.take(18)}...",
                                color = VibrantCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = postCaption,
                        onValueChange = { postCaption = it },
                        placeholder = { Text("Write a caption and #tags...", color = Color.White.copy(alpha = 0.5f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = VibrantSurfaceVariant,
                            unfocusedContainerColor = VibrantSurfaceVariant,
                            focusedBorderColor = VibrantCyan,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("post_caption_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Hashtag Chips
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("#kika", "#vfxmagic", "#trending", "#fyp").forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(VibrantSurfaceVariant)
                                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                    .clickable {
                                        postCaption = if (postCaption.isEmpty()) tag else "$postCaption $tag"
                                    }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(text = tag, color = VibrantCyan, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Big Action: Post to Kika
                    Button(
                        onClick = {
                            KikaRepository.publishNewVideo(
                                description = postCaption,
                                hashtags = listOf("#kika", "#myvid"),
                                effect = selectedEffect,
                                soundTrack = selectedSound,
                                imageRes = cameraScenes[selectedSceneIndex]
                            )
                            Toast.makeText(context, "Your video has been posted to Kika!", Toast.LENGTH_LONG).show()
                            onVideoPublished()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VibrantPink),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("publish_video_btn")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Post to Kika Feed",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        } else {
            // =====================
            // CAMERA STUDIO MODE
            // =====================

            // Top Bar: Close + Add Sound + Flash
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .testTag("close_studio_btn")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }

                // Sound selector chip
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .border(1.dp, VibrantPink.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        .clickable { showSoundPicker = true }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("select_sound_chip"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.MusicNote, contentDescription = null, tint = VibrantPink, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = selectedSound.title.take(16) + "...",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Flash toggle
                IconButton(
                    onClick = { isFlashOn = !isFlashOn },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "Flash",
                        tint = if (isFlashOn) VibrantYellow else Color.White
                    )
                }
            }

            // Top Recording Duration Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = 56.dp, start = 16.dp, end = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White.copy(alpha = 0.3f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(recordingProgress)
                            .height(4.dp)
                            .background(VibrantPink)
                    )
                }
            }

            // Right-Side Creative Tools Rail
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Switch Scene / Camera Viewfinder
                StudioToolButton(
                    icon = Icons.Default.Cameraswitch,
                    label = "Scene",
                    onClick = {
                        selectedSceneIndex = (selectedSceneIndex + 1) % cameraScenes.size
                        Toast.makeText(context, "Switched Scene Viewfinder", Toast.LENGTH_SHORT).show()
                    }
                )

                // Speed Selector (0.5x, 1x, 2x)
                StudioToolButton(
                    icon = Icons.Default.Speed,
                    label = "${selectedSpeed}x",
                    isActive = selectedSpeed != 1.0f,
                    onClick = {
                        selectedSpeed = when (selectedSpeed) {
                            0.5f -> 1.0f
                            1.0f -> 2.0f
                            else -> 0.5f
                        }
                    }
                )

                // Timer (0s, 3s)
                StudioToolButton(
                    icon = Icons.Default.Timer,
                    label = if (timerCountdownSec > 0) "${timerCountdownSec}s" else "Timer",
                    isActive = timerCountdownSec > 0,
                    onClick = {
                        timerCountdownSec = if (timerCountdownSec == 0) 3 else 0
                    }
                )

                // Beauty / AutoAwesome
                StudioToolButton(
                    icon = Icons.Default.AutoAwesome,
                    label = "VFX",
                    isActive = true,
                    onClick = {
                        Toast.makeText(context, "Active VFX: ${selectedEffect.name}", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // Bottom Controls Area: Effects Carousel + Record Button
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Effects Carousel Bar ("make effects / choose effects")
                Text(
                    text = "SWIPE TO CHOOSE EFFECTS",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(effects, key = { it.id }) { effect ->
                        val isSelected = effect.id == selectedEffect.id
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { selectedEffect = effect }
                                .testTag("effect_chip_${effect.id}")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) VibrantPink else VibrantSurfaceVariant)
                                    .border(
                                        width = if (isSelected) 2.5.dp else 1.dp,
                                        color = if (isSelected) VibrantCyan else Color.White.copy(alpha = 0.25f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = effect.name,
                                    tint = if (isSelected) Color.White else VibrantCyan,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = effect.name,
                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Record Button Row (Undo | Big Shutter | Checkmark Next)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Undo Segment Button
                    if (hasRecordedSegment && !isRecording) {
                        IconButton(
                            onClick = {
                                recordedDurationMs = 0f
                                recordingProgress = 0f
                                hasRecordedSegment = false
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                        ) {
                            Icon(Icons.Default.Undo, contentDescription = "Undo Clip", tint = Color.White)
                        }
                    } else {
                        Spacer(modifier = Modifier.size(44.dp))
                    }

                    // Main Shutter / Record Button
                    Box(
                        modifier = Modifier
                            .size(84.dp)
                            .clickable {
                                if (isRecording) {
                                    isRecording = false
                                } else {
                                    startRecordingWithTimer()
                                }
                            }
                            .testTag("record_shutter_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        // Outer ring progress
                        CircularProgressIndicator(
                            progress = { recordingProgress },
                            color = VibrantPink,
                            strokeWidth = 5.dp,
                            modifier = Modifier.size(84.dp),
                            trackColor = Color.White.copy(alpha = 0.25f)
                        )

                        // Center trigger pill
                        Box(
                            modifier = Modifier
                                .size(if (isRecording) 36.dp else 66.dp)
                                .clip(if (isRecording) RoundedCornerShape(8.dp) else CircleShape)
                                .background(VibrantPink)
                        )
                    }

                    // Checkmark / Done recording Button
                    if (hasRecordedSegment && !isRecording) {
                        IconButton(
                            onClick = { isPreviewMode = true },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(VibrantCyan)
                                .testTag("finish_recording_btn")
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "Done", tint = Color.Black)
                        }
                    } else {
                        Spacer(modifier = Modifier.size(44.dp))
                    }
                }
            }
        }

        // Sound Picker Dialog
        if (showSoundPicker) {
            AlertDialog(
                onDismissRequest = { showSoundPicker = false },
                containerColor = VibrantSurface,
                title = { Text("Choose Sound Track", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        KikaRepository.soundTracks.forEach { sound ->
                            val isSelected = sound.id == selectedSound.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) VibrantPink.copy(alpha = 0.2f) else VibrantSurfaceVariant)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) VibrantPink else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        selectedSound = sound
                                        showSoundPicker = false
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.MusicNote, contentDescription = null, tint = if (isSelected) VibrantPink else Color.Gray)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(text = sound.title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Text(text = sound.artist, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showSoundPicker = false },
                        colors = ButtonDefaults.buttonColors(containerColor = VibrantPink)
                    ) {
                        Text("Close")
                    }
                }
            )
        }
    }
}

@Composable
private fun StudioToolButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isActive) VibrantPink else Color.Black.copy(alpha = 0.5f))
                .border(1.dp, if (isActive) VibrantPink else Color.White.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
