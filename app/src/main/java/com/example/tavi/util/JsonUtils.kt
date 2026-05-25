package com.example.tavi.util

// Extracts the first top-level JSON object from arbitrary text by counting braces.
// Handles nested objects, preamble text, and markdown code fences.
fun extractFirstJsonObject(text: String): String? {
    var depth = 0
    var start = -1
    for (i in text.indices) {
        when (text[i]) {
            '{' -> { if (depth == 0) start = i; depth++ }
            '}' -> { depth--; if (depth == 0 && start >= 0) return text.substring(start, i + 1) }
        }
    }
    return null
}
