package com.example.ui.effects_lab

import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.ui.effects.EffectCanvasOverlay
import com.example.ui.theme.VibrantAtmosphereBackground
import com.example.ui.theme.VibrantButtonHaloGradient
import com.example.ui.theme.VibrantCyan
import com.example.ui.theme.VibrantPink
import com.example.ui.theme.VibrantSlate900
import com.example.ui.theme.VibrantSurface
import com.example.ui.theme.VibrantSurfaceVariant
import com.example.ui.theme.VibrantYellow
import java.util.UUID

@Composable
fun EffectsLabScreen(
    onRecordWithEffect: (KikaEffect) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val savedEffects by KikaRepository.effects.collectAsState()

    // Effect Maker custom parameters
    var effectName by remember { mutableStateOf("Neon Stardust") }
    var glowIntensity by remember { mutableFloatStateOf(0.75f) }
    var hueShift by remember { mutableFloatStateOf(300f) }
    var glitchAmount by remember { mutableFloatStateOf(0.35f) }
    var sparkleCount by remember { mutableIntStateOf(18) }
    var scanlines by remember { mutableStateOf(true) }
    var vignette by remember { mutableFloatStateOf(0.4f) }

    // Preview background sample selector
    val previewModels = listOf(
        R.drawable.vid_cyber_dancer,
        R.drawable.vid_glitch_vfx,
        R.drawable.vid_skater_sunset,
        R.drawable.vid_synthwave_art
    )
    var selectedModelIndex by remember { mutableIntStateOf(0) }

    // Assembled dynamic effect object for real-time live canvas preview
    val livePreviewEffect = remember(
        effectName, glowIntensity, hueShift, glitchAmount, sparkleCount, scanlines, vignette
    ) {
        KikaEffect(
            id = "custom_preview",
            name = effectName.ifBlank { "Custom Effect" },
            category = "Custom",
            description = "Custom VFX crafted in Kika Effects Lab",
            glowIntensity = glowIntensity,
            hueShift = hueShift,
            glitchAmount = glitchAmount,
            sparkleCount = sparkleCount,
            scanlines = scanlines,
            vignette = vignette,
            isCustomUserCreated = true,
            creatorName = "@you"
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        VibrantAtmosphereBackground(modifier = Modifier.fillMaxSize())

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(bottom = 60.dp)
        ) {
            // 1. Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = VibrantPink,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Effects Lab",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Craft custom visual effects, test them live, and use them in your vids!",
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = 13.sp
                    )
                }
            }

            // 2. Live Interactive VFX Preview Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(260.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(
                            1.5.dp,
                            VibrantButtonHaloGradient,
                            RoundedCornerShape(20.dp)
                        )
                ) {
                    // Background sample photo
                    Image(
                        painter = painterResource(id = previewModels[selectedModelIndex]),
                        contentDescription = "Preview Model",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // The dynamic live canvas effect
                    EffectCanvasOverlay(
                        effect = livePreviewEffect,
                        isPlaying = true,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Top live badge & sample switcher
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black.copy(alpha = 0.7f))
                                .border(1.dp, VibrantPink.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "● LIVE: ${livePreviewEffect.name}",
                                color = VibrantPink,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black.copy(alpha = 0.7f))
                                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                .clickable {
                                    selectedModelIndex = (selectedModelIndex + 1) % previewModels.size
                                }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Switch Model ↻",
                                color = Color.White,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // 3. Quick Action Buttons: Save Effect & Record With It
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Save Effect Button
                    Button(
                        onClick = {
                            val saved = livePreviewEffect.copy(id = "eff_custom_${UUID.randomUUID().toString().take(6)}")
                            KikaRepository.saveCustomEffect(saved)
                            Toast.makeText(context, "Saved '${saved.name}' to your Effects Library!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VibrantSurfaceVariant),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
                            .testTag("save_effect_btn")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, tint = VibrantCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Effect", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }

                    // Record Vid With This Effect Button
                    Button(
                        onClick = {
                            val saved = livePreviewEffect.copy(id = "eff_custom_${UUID.randomUUID().toString().take(6)}")
                            KikaRepository.saveCustomEffect(saved)
                            onRecordWithEffect(saved)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VibrantPink),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(48.dp)
                            .testTag("record_with_effect_btn")
                    ) {
                        Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Make Vid With It", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            // 4. Effect Tuner Controls
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = VibrantSurface),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(18.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "EFFECT DESIGNER CONTROLS",
                            color = VibrantCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Effect Name Field
                        OutlinedTextField(
                            value = effectName,
                            onValueChange = { effectName = it },
                            label = { Text("Effect Name", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = VibrantSurfaceVariant,
                                unfocusedContainerColor = VibrantSurfaceVariant,
                                focusedBorderColor = VibrantCyan,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("effect_name_input")
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // 1. Glow & Bloom Intensity
                        EffectSliderRow(
                            label = "Glow & Bloom Intensity",
                            valueText = "${(glowIntensity * 100).toInt()}%",
                            value = glowIntensity,
                            onValueChange = { glowIntensity = it }
                        )

                        // 2. Color Shift & Hue
                        EffectSliderRow(
                            label = "Color Tint / Hue Shift",
                            valueText = "${hueShift.toInt()}°",
                            value = hueShift,
                            valueRange = 0f..360f,
                            onValueChange = { hueShift = it }
                        )

                        // 3. Glitch & Chromatic Jitter
                        EffectSliderRow(
                            label = "Glitch & Distortion",
                            valueText = "${(glitchAmount * 100).toInt()}%",
                            value = glitchAmount,
                            onValueChange = { glitchAmount = it }
                        )

                        // 4. Sparkles & Particles
                        EffectSliderRow(
                            label = "Starlight Sparkles Count",
                            valueText = "$sparkleCount particles",
                            value = sparkleCount.toFloat(),
                            valueRange = 0f..40f,
                            onValueChange = { sparkleCount = it.toInt() }
                        )

                        // 5. Film Vignette
                        EffectSliderRow(
                            label = "Edge Vignette Darkening",
                            valueText = "${(vignette * 100).toInt()}%",
                            value = vignette,
                            onValueChange = { vignette = it }
                        )

                        // 6. VHS CRT Scanlines Switch
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "VHS Scanlines", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text(text = "Horizontal analog camcorder lines", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                            }
                            Switch(
                                checked = scanlines,
                                onCheckedChange = { scanlines = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = VibrantPink,
                                    uncheckedTrackColor = Color(0xFF2A2045)
                                )
                            )
                        }
                    }
                }
            }

            // 5. Community & Saved Effects Showcase
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                ) {
                    Text(
                        text = "COMMUNITY EFFECTS LIBRARY",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(savedEffects, key = { it.id }) { effect ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = VibrantSurface),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .width(160.dp)
                                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                                    .clickable {
                                        // Load into editor
                                        effectName = effect.name
                                        glowIntensity = effect.glowIntensity
                                        hueShift = effect.hueShift
                                        glitchAmount = effect.glitchAmount
                                        sparkleCount = effect.sparkleCount
                                        scanlines = effect.scanlines
                                        vignette = effect.vignette
                                        Toast.makeText(context, "Loaded '${effect.name}' into editor", Toast.LENGTH_SHORT).show()
                                    }
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(VibrantPink.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = VibrantPink, modifier = Modifier.size(18.dp))
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = effect.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                                    Text(text = "By ${effect.creatorName}", color = VibrantCyan, fontSize = 11.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = effect.description, color = Color.White.copy(alpha = 0.6f), fontSize = 10.5.sp, maxLines = 2)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(VibrantPink.copy(alpha = 0.25f))
                                            .clickable { onRecordWithEffect(effect) }
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Use in Vid", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EffectSliderRow(
    label: String,
    valueText: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, color = Color.White, fontSize = 12.5.sp, fontWeight = FontWeight.Medium)
            Text(text = valueText, color = VibrantCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = VibrantPink,
                activeTrackColor = VibrantPink,
                inactiveTrackColor = Color.White.copy(alpha = 0.15f)
            )
        )
    }
}
