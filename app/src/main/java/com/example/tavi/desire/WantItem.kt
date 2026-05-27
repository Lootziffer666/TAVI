package com.example.tavi.desire

import java.util.UUID

data class WantItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val subscriptionCost: String? = null,
    val manipulationHints: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)
