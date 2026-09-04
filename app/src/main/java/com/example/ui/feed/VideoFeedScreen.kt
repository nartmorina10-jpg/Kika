package com.example.ui.feed

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.KikaRepository
import com.example.model.KikaEffect
import com.example.model.KikaVideo
import com.example.ui.theme.VibrantAtmosphereBackground
import com.example.ui.theme.VibrantBoltGradient
import com.example.ui.theme.VibrantCyan
import com.example.ui.theme.VibrantPink
import com.example.ui.theme.VibrantSurface
import com.example.ui.theme.VibrantSurfaceVariant

@Composable
fun VideoFeedScreen(
    onUseEffectInStudio: (KikaEffect) -> Unit,
    onNavigateToSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val videos by KikaRepository.videos.collectAsState()
    val commentsMap by KikaRepository.commentsMap.collectAsState()

    var selectedTab by remember { mutableStateOf(1) } // 0 = Following, 1 = For You
    var activeCommentVideo by remember { mutableStateOf<KikaVideo?>(null) }
    var shareDialogVideo by remember { mutableStateOf<KikaVideo?>(null) }

    val displayedVideos = remember(selectedTab, videos) {
        if (selectedTab == 0) {
            val followingList = videos.filter { it.isFollowing }
            if (followingList.isEmpty()) videos.take(2) else followingList
        } else {
            videos
        }
    }

    val pagerState = rememberPagerState(pageCount = { displayedVideos.size })

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        if (displayedVideos.isNotEmpty()) {
            VerticalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("video_feed_pager")
            ) { page ->
                val video = displayedVideos[page]
                VideoItemView(
                    video = video,
                    isActivePage = pagerState.currentPage == page,
                    onToggleLike = { KikaRepository.toggleLike(video.id) },
                    onToggleBookmark = { KikaRepository.toggleBookmark(video.id) },
                    onToggleFollow = { KikaRepository.toggleFollow(video.authorUsername) },
                    onOpenComments = { activeCommentVideo = video },
                    onOpenShare = { shareDialogVideo = video },
                    onUseEffect = { effect -> onUseEffectInStudio(effect) }
                )
            }
        }

        // Top Header Bar: Branded Bolt Logo + Following | For You + Search
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Branded Bolt Icon + Kika wordmark
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(VibrantBoltGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Kika Bolt",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "kika",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    letterSpacing = (-0.5).sp
                )
            }

            // Feed Tabs with Vibrant indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { selectedTab = 0 }
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                        .testTag("tab_following")
                ) {
                    Text(
                        text = "Following",
                        color = if (selectedTab == 0) Color.White else Color.White.copy(alpha = 0.6f),
                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Box(
                        modifier = Modifier
                            .width(28.dp)
                            .height(2.dp)
                            .clip(RoundedCornerShape(1.dp))
                            .background(if (selectedTab == 0) VibrantPink else Color.Transparent)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { selectedTab = 1 }
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                        .testTag("tab_for_you")
                ) {
                    Text(
                        text = "For You",
                        color = if (selectedTab == 1) Color.White else Color.White.copy(alpha = 0.6f),
                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Box(
                        modifier = Modifier
                            .width(28.dp)
                            .height(2.dp)
                            .clip(RoundedCornerShape(1.dp))
                            .background(if (selectedTab == 1) VibrantPink else Color.Transparent)
                    )
                }
            }

            // Search / Discover button
            IconButton(
                onClick = onNavigateToSearch,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("feed_search_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Active Comments Sheet
        activeCommentVideo?.let { vid ->
            val comments = commentsMap[vid.id] ?: emptyList()
            CommentsBottomSheet(
                comments = comments,
                onDismiss = { activeCommentVideo = null },
                onAddComment = { text ->
                    KikaRepository.addComment(vid.id, text)
                }
            )
        }

        // Share Dialog
        shareDialogVideo?.let { vid ->
            AlertDialog(
                onDismissRequest = { shareDialogVideo = null },
                containerColor = VibrantSurface,
                title = {
                    Text(
                        text = "Share Kika Video",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Share ${vid.authorUsername}'s video with friends or save to device",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ShareActionItem(
                                icon = Icons.Default.ContentCopy,
                                label = "Copy Link"
                            ) {
                                Toast.makeText(context, "Link copied to clipboard!", Toast.LENGTH_SHORT).show()
                                shareDialogVideo = null
                            }
                            ShareActionItem(
                                icon = Icons.Default.Share,
                                label = "Share"
                            ) {
                                Toast.makeText(context, "Sharing Kika clip...", Toast.LENGTH_SHORT).show()
                                shareDialogVideo = null
                            }
                            ShareActionItem(
                                icon = Icons.Default.Download,
                                label = "Save Vid"
                            ) {
                                Toast.makeText(context, "Saved to device gallery!", Toast.LENGTH_SHORT).show()
                                shareDialogVideo = null
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { shareDialogVideo = null },
                        colors = ButtonDefaults.buttonColors(containerColor = VibrantPink),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Done", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

@Composable
private fun ShareActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFF2A2A3E), RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = label, color = Color.White, fontSize = 11.sp)
    }
}
