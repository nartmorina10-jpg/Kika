package com.example.data

import com.example.R
import com.example.model.Comment
import com.example.model.KikaEffect
import com.example.model.KikaVideo
import com.example.model.SoundTrack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object KikaRepository {

    val defaultEffects = listOf(
        KikaEffect(
            id = "eff_cyber_glow",
            name = "Cyber Glow",
            category = "Glow",
            description = "Pulsing neon perimeter with chromatic bloom and beat aura",
            glowIntensity = 0.85f,
            hueShift = 320f,
            glitchAmount = 0.1f,
            sparkleCount = 12,
            scanlines = false,
            vignette = 0.35f,
            creatorName = "kika VFX"
        ),
        KikaEffect(
            id = "eff_vhs_glitch",
            name = "VHS Glitch",
            category = "Retro",
            description = "90s camcorder aesthetic with horizontal scanlines and chromatic RGB shifts",
            glowIntensity = 0.2f,
            hueShift = 45f,
            glitchAmount = 0.75f,
            sparkleCount = 0,
            scanlines = true,
            vignette = 0.5f,
            creatorName = "RetroLab"
        ),
        KikaEffect(
            id = "eff_starlight",
            name = "Starlight Magic",
            category = "Sparkle",
            description = "Floating golden cross-star sparkles with soft ambient twinkle",
            glowIntensity = 0.4f,
            hueShift = 0f,
            glitchAmount = 0f,
            sparkleCount = 35,
            scanlines = false,
            vignette = 0.2f,
            creatorName = "StarDust"
        ),
        KikaEffect(
            id = "eff_rainbow_prism",
            name = "Rainbow Prism",
            category = "Color",
            description = "Iridescent kaleidoscopic color spectrum wave",
            glowIntensity = 0.6f,
            hueShift = 180f,
            glitchAmount = 0.15f,
            sparkleCount = 20,
            scanlines = false,
            vignette = 0.25f,
            creatorName = "PrismFX"
        ),
        KikaEffect(
            id = "eff_vintage_noir",
            name = "Noir 8mm",
            category = "Film",
            description = "Classic cinematic film tone with heavy vignette and analog texture",
            glowIntensity = 0f,
            hueShift = 30f,
            glitchAmount = 0.2f,
            sparkleCount = 0,
            scanlines = true,
            vignette = 0.8f,
            creatorName = "Cinema8"
        ),
        KikaEffect(
            id = "eff_speed_rave",
            name = "Speed Rave",
            category = "Motion",
            description = "High intensity neon flashes and dynamic speed distortion",
            glowIntensity = 0.9f,
            hueShift = 280f,
            glitchAmount = 0.5f,
            sparkleCount = 25,
            scanlines = true,
            vignette = 0.4f,
            speedMultiplier = 2.0f,
            creatorName = "RaveNation"
        )
    )

    val soundTracks = listOf(
        SoundTrack("snd_1", "Tokyo Neon Nights (Club Mix)", "Zack & The Beatmakers", 15, 128),
        SoundTrack("snd_2", "Hyperpop Glitch Anthem", "NovaKidd", 30, 140),
        SoundTrack("snd_3", "Golden Hour Skate Flow", "Sunset Wave", 15, 95),
        SoundTrack("snd_4", "Midnight Lofi Coffee Rain", "Chilled Sakura", 60, 85),
        SoundTrack("snd_5", "Phonk Drift 2026", "Kage Rider", 15, 150)
    )

    private val _effects = MutableStateFlow<List<KikaEffect>>(defaultEffects)
    val effects: StateFlow<List<KikaEffect>> = _effects.asStateFlow()

    private val initialVideos = listOf(
        KikaVideo(
            id = "vid_1",
            authorUsername = "@zack_vfx",
            authorName = "Zack Vance",
            description = "Late night Tokyo freestyle with the new Cyber Glow effect! The colors pop so crazy under neon lights 🔥 #kika #cyberglow #dance #tokyo",
            hashtags = listOf("#kika", "#cyberglow", "#dance", "#tokyo"),
            soundTrackTitle = "Tokyo Neon Nights (Club Mix)",
            soundArtist = "Zack & The Beatmakers",
            effectUsed = defaultEffects[0],
            imageRes = R.drawable.vid_cyber_dancer,
            likesCount = 24890,
            commentsCount = 482,
            sharesCount = 1205,
            bookmarksCount = 3840,
            isLiked = false,
            isBookmarked = false,
            isFollowing = false,
            createdAtFormatted = "1h ago"
        ),
        KikaVideo(
            id = "vid_2",
            authorUsername = "@glitch_queen",
            authorName = "Aria Hologram",
            description = "Made a custom holographic glitch effect in Kika Effects Lab! Check the chromatic aura when I move my hands ✨⚡ #makeeffects #vfx #glitchart",
            hashtags = listOf("#makeeffects", "#vfx", "#glitchart", "#kika"),
            soundTrackTitle = "Hyperpop Glitch Anthem",
            soundArtist = "NovaKidd",
            effectUsed = defaultEffects[1],
            imageRes = R.drawable.vid_glitch_vfx,
            likesCount = 58210,
            commentsCount = 934,
            sharesCount = 4120,
            bookmarksCount = 8900,
            isLiked = true,
            isBookmarked = false,
            isFollowing = true,
            createdAtFormatted = "3h ago"
        ),
        KikaVideo(
            id = "vid_3",
            authorUsername = "@kai_skates",
            authorName = "Kai Tanaka",
            description = "Sunset kickflips hitting different today 🛹 Starlight Magic effect makes the lens flare sparkle so hard! #skate #sunset #starlight #kikavids",
            hashtags = listOf("#skate", "#sunset", "#starlight", "#kikavids"),
            soundTrackTitle = "Golden Hour Skate Flow",
            soundArtist = "Sunset Wave",
            effectUsed = defaultEffects[2],
            imageRes = R.drawable.vid_skater_sunset,
            likesCount = 18450,
            commentsCount = 270,
            sharesCount = 560,
            bookmarksCount = 1920,
            isLiked = false,
            isBookmarked = true,
            isFollowing = false,
            createdAtFormatted = "5h ago"
        ),
        KikaVideo(
            id = "vid_4",
            authorUsername = "@sakura_lofi",
            authorName = "Mio Visuals",
            description = "Rainy window vibes & lofi beats. Added rainbow prism tint for that dreamy anime feel 🌧️🎧 #aesthetic #lofi #chillvibes #kika",
            hashtags = listOf("#aesthetic", "#lofi", "#chillvibes", "#kika"),
            soundTrackTitle = "Midnight Lofi Coffee Rain",
            soundArtist = "Chilled Sakura",
            effectUsed = defaultEffects[3],
            imageRes = R.drawable.vid_synthwave_art,
            likesCount = 42100,
            commentsCount = 612,
            sharesCount = 1890,
            bookmarksCount = 5400,
            isLiked = false,
            isBookmarked = false,
            isFollowing = false,
            createdAtFormatted = "8h ago"
        )
    )

    private val _videos = MutableStateFlow<List<KikaVideo>>(initialVideos)
    val videos: StateFlow<List<KikaVideo>> = _videos.asStateFlow()

    private val _commentsMap = MutableStateFlow<Map<String, List<Comment>>>(
        mapOf(
            "vid_1" to listOf(
                Comment("c1", "@neon_runner", "That spin with the cyber glow is unreal!! ⚡", "45m ago", 184, false),
                Comment("c2", "@chloe_creatives", "How did you get the perimeter glow to pulse with the kick?", "30m ago", 92, false),
                Comment("c3", "@dance_guru", "Pure fire choreography 🔥🙌", "12m ago", 45, false)
            ),
            "vid_2" to listOf(
                Comment("c4", "@cyberpunk_art", "This effect is crazy good! Can you share the preset?", "2h ago", 312, false),
                Comment("c5", "@vfx_noob", "Used your effect in my new vid, worked like a charm!", "1h ago", 140, false)
            ),
            "vid_3" to listOf(
                Comment("c6", "@ollie_king", "Clean catch on that kickflip bro 🛹", "4h ago", 88, false),
                Comment("c7", "@golden_hour", "The sparkles on the sunset are chef's kiss 👌", "3h ago", 54, false)
            ),
            "vid_4" to listOf(
                Comment("c8", "@chill_hop", "Instantly relaxed watching this 🍵", "7h ago", 240, false),
                Comment("c9", "@anime_fan", "The colors give such warm aesthetic vibes!", "5h ago", 110, false)
            )
        )
    )
    val commentsMap: StateFlow<Map<String, List<Comment>>> = _commentsMap.asStateFlow()

    fun toggleLike(videoId: String) {
        _videos.value = _videos.value.map { vid ->
            if (vid.id == videoId) {
                val newLiked = !vid.isLiked
                val newCount = if (newLiked) vid.likesCount + 1 else (vid.likesCount - 1).coerceAtLeast(0)
                vid.copy(isLiked = newLiked, likesCount = newCount)
            } else vid
        }
    }

    fun toggleBookmark(videoId: String) {
        _videos.value = _videos.value.map { vid ->
            if (vid.id == videoId) {
                val newBm = !vid.isBookmarked
                val newCount = if (newBm) vid.bookmarksCount + 1 else (vid.bookmarksCount - 1).coerceAtLeast(0)
                vid.copy(isBookmarked = newBm, bookmarksCount = newCount)
            } else vid
        }
    }

    fun toggleFollow(authorUsername: String) {
        _videos.value = _videos.value.map { vid ->
            if (vid.authorUsername == authorUsername) {
                vid.copy(isFollowing = !vid.isFollowing)
            } else vid
        }
    }

    fun addComment(videoId: String, text: String, author: String = "@you") {
        if (text.isBlank()) return
        val currentList = _commentsMap.value[videoId] ?: emptyList()
        val newComment = Comment(
            id = UUID.randomUUID().toString(),
            authorUsername = author,
            text = text.trim(),
            timeAgo = "just now",
            likesCount = 0,
            isLiked = false
        )
        _commentsMap.value = _commentsMap.value + (videoId to (listOf(newComment) + currentList))
        
        // Update comments count in video
        _videos.value = _videos.value.map { vid ->
            if (vid.id == videoId) {
                vid.copy(commentsCount = vid.commentsCount + 1)
            } else vid
        }
    }

    fun saveCustomEffect(effect: KikaEffect) {
        val updated = listOf(effect) + _effects.value
        _effects.value = updated
    }

    fun publishNewVideo(
        description: String,
        hashtags: List<String>,
        effect: KikaEffect,
        soundTrack: SoundTrack,
        imageRes: Int? = null
    ): KikaVideo {
        val newVideo = KikaVideo(
            id = "vid_user_${System.currentTimeMillis()}",
            authorUsername = "@you",
            authorName = "You (Kika Creator)",
            description = description.ifBlank { "New video made with ${effect.name} ✨ #kika #vids #effects" },
            hashtags = if (hashtags.isEmpty()) listOf("#kika", "#myvid", "#${effect.name.lowercase().replace(" ", "")}") else hashtags,
            soundTrackTitle = soundTrack.title,
            soundArtist = soundTrack.artist,
            effectUsed = effect,
            imageRes = imageRes ?: R.drawable.vid_cyber_dancer,
            likesCount = 1,
            commentsCount = 0,
            sharesCount = 0,
            bookmarksCount = 0,
            isLiked = true,
            isBookmarked = false,
            isFollowing = false,
            createdAtFormatted = "Just now",
            isUserUploaded = true
        )
        // Insert at the very top of the feed so user immediately scrolls to it!
        _videos.value = listOf(newVideo) + _videos.value
        return newVideo
    }
}
