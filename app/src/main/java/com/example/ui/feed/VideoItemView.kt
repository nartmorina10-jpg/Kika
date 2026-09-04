package com.example.ui.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.KikaEffect
import com.example.model.KikaVideo
import com.example.ui.effects.EffectCanvasOverlay
import com.example.ui.theme.KikaCyan
import com.example.ui.theme.KikaPink
import com.example.ui.theme.KikaYellow
import com.example.ui.theme.VibrantAvatarGradient
import com.example.ui.theme.VibrantCyan
import com.example.ui.theme.VibrantDiscGradient
import com.example.ui.theme.VibrantIndigo900
import com.example.ui.theme.VibrantPink
import com.example.ui.theme.VibrantPinkDark
import com.example.ui.theme.VibrantPurple900
import com.example.ui.theme.VibrantSlate900
import com.example.ui.theme.VibrantYellow
import kotlinx.coroutines.delay

@Composable
fun VideoItemView(
    video: KikaVideo,
    isActivePage: Boolean,
    onToggleLike: () -> Unit,
    onToggleBookmark: () -> Unit,
    onToggleFollow: () -> Unit,
    onOpenComments: () -> Unit,
    onOpenShare: () -> Unit,
    onUseEffect: (KikaEffect) -> Unit,
    modifier: Modifier = Modifier
) {
    var isPlaying by remember { mutableStateOf(true) }
    var showPlayPauseIcon by remember { mutableStateOf(false) }
    var playbackProgress by remember { mutableFloatStateOf(0f) }
    val heartBursts = remember { mutableStateListOf<HeartBurst>() }

    // Synchronize play state when scrolling between videos
    LaunchedEffect(isActivePage) {
        isPlaying = isActivePage
        playbackProgress = 0f
    }

    // Video playback progress simulation
    LaunchedEffect(isPlaying, isActivePage) {
        if (isPlaying && isActivePage) {
            val stepMs = 50L
            val totalMs = 15000L // 15 seconds loop
            while (isPlaying && isActivePage) {
                delay(stepMs)
                playbackProgress = (playbackProgress + (stepMs.toFloat() / totalMs)) % 1f
            }
        }
    }

    // Vinyl record spinning animation
    val infiniteTransition = rememberInfiniteTransition(label = "disc_anim")
    val discRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "disc_rot"
    )

    // Subtle video motion simulation (zoom/pan)
    val videoMotionScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "video_zoom"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { offset ->
                        onToggleLike()
                        heartBursts.add(HeartBurst(offsetX = offset.x - 400f, offsetY = offset.y - 600f))
                    },
                    onTap = {
                        isPlaying = !isPlaying
                        showPlayPauseIcon = true
                    }
                )
            }
    ) {
        // 1. Video Visual Frame (Image with motion)
        if (video.imageRes != null) {
            Image(
                painter = painterResource(id = video.imageRes),
                contentDescription = "Video Visual",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .scale(if (isPlaying) videoMotionScale else 1.0f)
            )
        } else {
            // Procedural vibrant video background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            listOf(VibrantPurple900, VibrantSlate900, VibrantIndigo900)
                        )
                    )
            )
        }

        // 2. Real-time Creative Effect Overlay
        EffectCanvasOverlay(
            effect = video.effectUsed,
            isPlaying = isPlaying,
            modifier = Modifier.fillMaxSize()
        )

        // 3. Dark Gradient Scrims (Top and Bottom)
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

        // 4. Center Play/Pause Indicator on tap
        LaunchedEffect(showPlayPauseIcon) {
            if (showPlayPauseIcon) {
                delay(800)
                showPlayPauseIcon = false
            }
        }
        AnimatedVisibility(
            visible = showPlayPauseIcon && !isPlaying,
            enter = fadeIn() + scaleIn(initialScale = 0.6f),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.65f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Paused",
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(54.dp)
                )
            }
        }

        // 5. Floating Hearts on Double Tap
        FloatingHeartsOverlay(
            bursts = heartBursts,
            onRemoveBurst = { id -> heartBursts.removeAll { it.id == id } }
        )

        // 6. Right Side Social Action Rail (Vibrant Palette styling)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 12.dp, bottom = 65.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Creator Avatar with Follow Badge
            Box(contentAlignment = Alignment.BottomCenter) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                        .background(VibrantAvatarGradient),
                    contentAlignment = Alignment.Center
                ) {
                    val initial = video.authorUsername.drop(1).take(1).uppercase().ifEmpty { "K" }
                    Text(
                        text = initial,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                }

                // Follow Badge (-bottom-2 left-1/2 border-2 border-black)
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, Color.Black, CircleShape)
                        .background(if (video.isFollowing) VibrantCyan else VibrantPink)
                        .clickable { onToggleFollow() }
                        .testTag("follow_author_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (video.isFollowing) Icons.Default.Check else Icons.Default.Add,
                        contentDescription = "Follow",
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            // Like / Heart Button
            SocialActionButton(
                icon = Icons.Default.Favorite,
                count = formatCount(video.likesCount),
                isActive = video.isLiked,
                activeColor = VibrantPink,
                testTag = "like_video_btn",
                onClick = onToggleLike
            )

            // Comment Button
            SocialActionButton(
                icon = Icons.Default.Comment,
                count = formatCount(video.commentsCount),
                isActive = false,
                testTag = "open_comments_btn",
                onClick = onOpenComments
            )

            // Effects Quick-Access Button
            SocialActionButton(
                icon = Icons.Default.AutoAwesome,
                count = "VFX",
                isActive = true,
                activeColor = VibrantCyan,
                testTag = "vfx_action_btn",
                onClick = { onUseEffect(video.effectUsed) }
            )

            // Bookmark Button
            SocialActionButton(
                icon = if (video.isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                count = formatCount(video.bookmarksCount),
                isActive = video.isBookmarked,
                activeColor = VibrantYellow,
                testTag = "bookmark_video_btn",
                onClick = onToggleBookmark
            )

            // Share Button
            SocialActionButton(
                icon = Icons.Default.Share,
                count = formatCount(video.sharesCount),
                isActive = false,
                testTag = "share_video_btn",
                onClick = onOpenShare
            )

            // Spinning Vinyl Record (slate-800 border-white/20 with pink-to-indigo gradient)
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(VibrantSlate900)
                    .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape)
                    .padding(5.dp)
                    .rotate(if (isPlaying) discRotation else 0f),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(VibrantDiscGradient)
                )
            }
        }

        // 7. Bottom Video Info & Metadata (Vibrant Palette layout)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.78f)
                .navigationBarsPadding()
                .padding(start = 16.dp, bottom = 65.dp)
        ) {
            // Author handle & Follow pill button
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = video.authorUsername,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (video.isFollowing) VibrantCyan.copy(alpha = 0.25f) else VibrantPinkDark)
                        .clickable { onToggleFollow() }
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                        .testTag("follow_author_pill")
                ) {
                    Text(
                        text = if (video.isFollowing) "FOLLOWING" else "FOLLOW",
                        color = if (video.isFollowing) VibrantCyan else Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Caption Description with highlighted hashtag styling
            Text(
                text = video.description,
                color = Color.White.copy(alpha = 0.95f),
                fontSize = 13.5.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // "Use this Effect" Interactive Badge Chip
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(alpha = 0.55f))
                    .border(1.dp, VibrantCyan.copy(alpha = 0.65f), RoundedCornerShape(16.dp))
                    .clickable { onUseEffect(video.effectUsed) }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .testTag("use_effect_chip"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = VibrantCyan,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Effect: ${video.effectUsed.name}",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(VibrantPink)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Try",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sound Track Marquee Pill (bg-white/10 rounded-full)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = VibrantCyan,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${video.soundTrackTitle} • ${video.soundArtist}",
                    color = Color.White.copy(alpha = 0.95f),
                    fontSize = 11.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // 8. Video Bottom Scrubber Progress Bar (h-1 rounded-full bg-white/20 with pink-500 fill)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 54.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth()
                .height(3.5.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.White.copy(alpha = 0.2f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(playbackProgress)
                    .clip(RoundedCornerShape(2.dp))
                    .background(VibrantPink)
            )
        }
    }
}

@Composable
private fun SocialActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: String,
    isActive: Boolean,
    activeColor: Color = KikaPink,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isActive) activeColor else Color.White,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = count,
            color = Color.White,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
        else -> count.toString()
    }
}
