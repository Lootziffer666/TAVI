package com.example.tavi.manipulation

data class SessionDebrief(
    val packageName: String,
    val appLabel: String,
    val detectedPatterns: List<String>,
    val durationMinutes: Int
)
