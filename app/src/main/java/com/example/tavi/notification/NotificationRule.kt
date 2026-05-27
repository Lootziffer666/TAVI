package com.example.tavi.notification

import java.util.UUID

data class NotificationRule(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val timeWindow: String,
    val isActive: Boolean = false,
    val allowedApps: List<String> = emptyList()
)
