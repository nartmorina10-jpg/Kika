package com.example.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.KikaRepository
import com.example.model.KikaVideo
import com.example.ui.theme.VibrantAtmosphereBackground
import com.example.ui.theme.VibrantButtonHaloGradient
import com.example.ui.theme.VibrantCyan
import com.example.ui.theme.VibrantPink
import com.example.ui.theme.VibrantSlate900
import com.example.ui.theme.VibrantSurface
import com.example.ui.theme.VibrantSurfaceVariant
import com.example.ui.theme.VibrantYellow

@Composable
fun ProfileScreen(
    onNavigateToEffectsLab: () -> Unit,
    onNavigateToCreator: () -> Unit,
    modifier: Modifier = Modifier
) {
    val videos by KikaRepository.videos.collectAsState()
    val effects by KikaRepository.effects.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0 = My Vids, 1 = Liked, 2 = Effects

    val myVideos = remember(videos) {
        videos.filter { it.authorUsername == "@you" || it.isUserUploaded }
    }
    val likedVideos = remember(videos) {
        videos.filter { it.isLiked }
    }
    val myCustomEffects = remember(effects) {
        effects.filter { it.isCustomUserCreated || it.creatorName == "@you" }
    }

    Box(modifier = modifier.fillMaxSize()) {
        VibrantAtmosphereBackground(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(bottom = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Avatar with Neon border
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .border(
                        2.5.dp,
                        Brush.sweepGradient(listOf(VibrantPink, VibrantCyan, VibrantYellow, VibrantPink)),
                        CircleShape
                    )
                    .background(VibrantSlate900),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "K",
                    color = VibrantCyan,
                    fontWeight = FontWeight.Black,
                    fontSize = 34.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "@you",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 20.sp
            )
            Text(
                text = "Kika Visual Creator ⚡ Crafting effects & vids",
                color = Color.White.copy(alpha = 0.65f),
                fontSize = 12.5.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStatItem(number = "142", label = "Following")
                ProfileStatItem(number = "8.4K", label = "Followers")
                ProfileStatItem(number = "45.2K", label = "Likes")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onNavigateToCreator,
                    colors = ButtonDefaults.buttonColors(containerColor = VibrantPink),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("profile_create_vid_btn")
                ) {
                    Text("Make Vid", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Button(
                    onClick = onNavigateToEffectsLab,
                    colors = ButtonDefaults.buttonColors(containerColor = VibrantSurfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .testTag("profile_make_effects_btn")
                ) {
                    Text("Effects Lab", color = VibrantCyan, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Profile Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = VibrantPink,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = VibrantPink,
                        height = 2.5.dp
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.VideoLibrary, contentDescription = "Vids", tint = if (selectedTab == 0) VibrantPink else Color.White.copy(alpha = 0.4f)) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Liked", tint = if (selectedTab == 1) VibrantPink else Color.White.copy(alpha = 0.4f)) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Effects", tint = if (selectedTab == 2) VibrantPink else Color.White.copy(alpha = 0.4f)) }
                )
            }

            // Grid Content
            when (selectedTab) {
                0 -> {
                    val listToShow = if (myVideos.isEmpty()) videos.take(3) else myVideos
                    VideoGrid(videos = listToShow)
                }
                1 -> {
                    VideoGrid(videos = likedVideos)
                }
                2 -> {
                    val effectsToShow = if (myCustomEffects.isEmpty()) effects else myCustomEffects
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(effectsToShow, key = { it.id }) { effect ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(VibrantSurface)
                                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(14.dp))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = VibrantPink)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(text = effect.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = effect.category, color = VibrantCyan, fontSize = 11.sp)
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
private fun ProfileStatItem(number: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = number, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
    }
}

@Composable
private fun VideoGrid(videos: List<KikaVideo>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(videos, key = { it.id }) { vid ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.75f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(VibrantSurfaceVariant)
            ) {
                if (vid.imageRes != null) {
                    Image(
                        painter = painterResource(id = vid.imageRes),
                        contentDescription = "Video Thumbnail",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                // Play count overlay
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.65f))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${vid.likesCount}",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
