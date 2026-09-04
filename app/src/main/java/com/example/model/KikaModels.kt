package com.example.model

data class KikaEffect(
    val id: String,
    val name: String,
    val category: String,
    val description: String,
    val glowIntensity: Float = 0.5f,
    val hueShift: Float = 0f,
    val glitchAmount: Float = 0f,
    val sparkleCount: Int = 0,
    val scanlines: Boolean = false,
    val vignette: Float = 0.2f,
    val particleType: String = "none",
    val speedMultiplier: Float = 1.0f,
    val isCustomUserCreated: Boolean = false,
    val creatorName: String = "kika VFX"
)

data class KikaVideo(
    val id: String,
    val authorUsername: String,
    val authorName: String,
    val authorAvatarRes: Int? = null,
    val description: String,
    val hashtags: List<String> = emptyList(),
    val soundTrackTitle: String,
    val soundArtist: String,
    val effectUsed: KikaEffect,
    val imageRes: Int? = null,
    val likesCount: Int = 1240,
    val commentsCount: Int = 89,
    val sharesCount: Int = 42,
    val bookmarksCount: Int = 138,
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false,
    val isFollowing: Boolean = false,
    val createdAtFormatted: String = "2h ago",
    val isUserUploaded: Boolean = false
)

data class Comment(
    val id: String,
    val authorUsername: String,
    val text: String,
    val timeAgo: String = "just now",
    val likesCount: Int = 0,
    val isLiked: Boolean = false
)

data class SoundTrack(
    val id: String,
    val title: String,
    val artist: String,
    val durationSec: Int = 15,
    val bpm: Int = 128
)
