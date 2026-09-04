package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.KikaEffect
import com.example.ui.creator.VideoCreatorScreen
import com.example.ui.effects_lab.EffectsLabScreen
import com.example.ui.explore.ExploreScreen
import com.example.ui.feed.VideoFeedScreen
import com.example.ui.profile.ProfileScreen
import com.example.ui.theme.KikaCyan
import com.example.ui.theme.KikaPink
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.VibrantButtonHaloGradient
import com.example.ui.theme.VibrantCyan
import com.example.ui.theme.VibrantPink
import com.example.ui.theme.VibrantYellow

enum class KikaScreen {
    FEED,
    EXPLORE,
    CREATOR,
    EFFECTS_LAB,
    PROFILE
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                KikaAppRoot()
            }
        }
    }
}

@Composable
fun KikaAppRoot() {
    var currentScreen by remember { mutableStateOf(KikaScreen.FEED) }
    var studioEffectPreset by remember { mutableStateOf<KikaEffect?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Main Screen Area
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "screen_transition",
            modifier = Modifier.fillMaxSize()
        ) { screen ->
            when (screen) {
                KikaScreen.FEED -> {
                    VideoFeedScreen(
                        onUseEffectInStudio = { effect ->
                            studioEffectPreset = effect
                            currentScreen = KikaScreen.CREATOR
                        },
                        onNavigateToSearch = {
                            currentScreen = KikaScreen.EXPLORE
                        }
                    )
                }
                KikaScreen.EXPLORE -> {
                    ExploreScreen(
                        onSelectEffect = { effect ->
                            studioEffectPreset = effect
                            currentScreen = KikaScreen.CREATOR
                        },
                        onSelectVideo = {
                            currentScreen = KikaScreen.FEED
                        }
                    )
                }
                KikaScreen.CREATOR -> {
                    VideoCreatorScreen(
                        initialEffect = studioEffectPreset,
                        onClose = {
                            currentScreen = KikaScreen.FEED
                        },
                        onVideoPublished = {
                            currentScreen = KikaScreen.FEED
                        }
                    )
                }
                KikaScreen.EFFECTS_LAB -> {
                    EffectsLabScreen(
                        onRecordWithEffect = { customEffect ->
                            studioEffectPreset = customEffect
                            currentScreen = KikaScreen.CREATOR
                        }
                    )
                }
                KikaScreen.PROFILE -> {
                    ProfileScreen(
                        onNavigateToEffectsLab = {
                            currentScreen = KikaScreen.EFFECTS_LAB
                        },
                        onNavigateToCreator = {
                            studioEffectPreset = null
                            currentScreen = KikaScreen.CREATOR
                        }
                    )
                }
            }
        }

        // Custom Bottom Navigation Bar (Hidden when in full-bleed recording studio)
        if (currentScreen != KikaScreen.CREATOR) {
            KikaBottomNavigationBar(
                currentScreen = currentScreen,
                onSelectScreen = { screen ->
                    if (screen == KikaScreen.CREATOR) {
                        studioEffectPreset = null
                    }
                    currentScreen = screen
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun KikaBottomNavigationBar(
    currentScreen: KikaScreen,
    onSelectScreen: (KikaScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.92f))
            .border(
                width = 0.5.dp,
                color = Color.White.copy(alpha = 0.12f)
            )
            .navigationBarsPadding()
            .height(60.dp)
            .testTag("kika_bottom_navigation")
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Feed / Home
            NavItem(
                icon = if (currentScreen == KikaScreen.FEED) Icons.Default.Home else Icons.Outlined.Home,
                label = "Home",
                isSelected = currentScreen == KikaScreen.FEED,
                onClick = { onSelectScreen(KikaScreen.FEED) },
                testTag = "nav_tab_feed"
            )

            // 2. Explore / Search
            NavItem(
                icon = if (currentScreen == KikaScreen.EXPLORE) Icons.Default.Search else Icons.Outlined.Search,
                label = "Explore",
                isSelected = currentScreen == KikaScreen.EXPLORE,
                onClick = { onSelectScreen(KikaScreen.EXPLORE) },
                testTag = "nav_tab_explore"
            )

            // 3. Center Creative Plus Button ("make vids") with Vibrant Multi-stop Halo
            Box(
                modifier = Modifier
                    .clickable { onSelectScreen(KikaScreen.CREATOR) }
                    .testTag("nav_tab_create_vid"),
                contentAlignment = Alignment.Center
            ) {
                // Vibrant multi-stop gradient halo (cyan to pink to yellow)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(VibrantButtonHaloGradient)
                        .padding(2.dp)
                ) {
                    // Center high-contrast white pill with black icon
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .padding(horizontal = 14.dp, vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Create Vid",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // 4. Effects Lab ("make effects")
            NavItem(
                icon = if (currentScreen == KikaScreen.EFFECTS_LAB) Icons.Default.AutoAwesome else Icons.Outlined.AutoAwesome,
                label = "Effects",
                isSelected = currentScreen == KikaScreen.EFFECTS_LAB,
                onClick = { onSelectScreen(KikaScreen.EFFECTS_LAB) },
                testTag = "nav_tab_effects_lab"
            )

            // 5. Profile
            NavItem(
                icon = if (currentScreen == KikaScreen.PROFILE) Icons.Default.Person else Icons.Outlined.Person,
                label = "Profile",
                isSelected = currentScreen == KikaScreen.PROFILE,
                onClick = { onSelectScreen(KikaScreen.PROFILE) },
                testTag = "nav_tab_profile"
            )
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag(testTag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.50f),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.50f),
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
        if (isSelected) {
            Spacer(modifier = Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(VibrantPink)
            )
        } else {
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

