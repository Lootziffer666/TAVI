package com.example.tavi.manipulation

data class ManipulationPattern(
    val id: String,
    val name: String,
    val category: PatternCategory,
    val explanation: String,
    val reflectionQuestion: String,
    val childRelevant: Boolean = false
)

enum class PatternCategory {
    ENGAGEMENT,  // Streak, Daily Reward, Variable Reward
    COMMERCE,    // Loot Box, Gacha, Battle Pass, Pay-to-Win, Energy Gate, Subscription Trap
    URGENCY,     // FOMO Countdown, Comeback Reward
    ATTENTION,   // Endless Scroll, Autoplay, Push Flood
    SOCIAL       // Social Pressure
}
