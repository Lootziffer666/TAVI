package com.example.tavi.snippet

import java.util.UUID

data class SnippetEntry(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val tags: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
