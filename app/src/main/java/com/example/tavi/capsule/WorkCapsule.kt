package com.example.tavi.capsule

import java.util.UUID

data class WorkCapsule(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val source: CapsuleSource = CapsuleSource.CLIPBOARD,
    val timestamp: Long = System.currentTimeMillis()
)

enum class CapsuleSource { CLIPBOARD, AI_RESPONSE, MANUAL }
