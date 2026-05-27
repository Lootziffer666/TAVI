package com.example.tavi.manipulation

data class ManipulationPattern(
    val id: String,
    val name: String,
    val category: PatternCategory
)

enum class PatternCategory {
    ENGAGEMENT,  // Streak, Daily Reward, Variable Reward
    COMMERCE,    // Loot Box, Gacha, Battle Pass, Pay-to-Win, Energy Gate, Subscription Trap
    URGENCY,     // FOMO Countdown, Comeback Reward
    ATTENTION,   // Endless Scroll, Autoplay, Notification Flood
    SOCIAL       // Social Pressure
}
