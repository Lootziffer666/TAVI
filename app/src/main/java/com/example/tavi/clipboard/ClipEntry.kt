package com.example.tavi.clipboard

data class ClipEntry(
    val content: String,
    val type: ClipType,
    val timestamp: Long = System.currentTimeMillis()
)

enum class ClipType { TEXT, URL, PHONE, CODE, OTHER }
