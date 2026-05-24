package com.example.tavi.cloud

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class GeminiShellExecutor(private val service: GeminiApiService, private val apiKey: String) {

    suspend fun buildCommand(naturalLanguageTask: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val prompt = """
                Convert this task to a safe Android shell command.
                Return JSON only: {"command": "<shell command>", "explanation": "<what it does in plain language>"}
                Never include destructive commands (rm -rf, format, wipe).
                Task: $naturalLanguageTask
            """.trimIndent()
            val response = service.generateContent(apiKey, GeminiRequest(
                contents = listOf(GeminiContent(parts = listOf(GeminiPart(prompt))))
            ))
            val text = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: error("No response from Gemini")
            val jsonString = extractFirstJsonObject(text)
                ?: error("No JSON object found in response")
            JSONObject(jsonString).getString("command")
        }
    }

    // Extracts the first top-level JSON object from arbitrary text by counting braces.
    // Handles nested objects, multiple JSON-like structures, and markdown code fences.
    private fun extractFirstJsonObject(text: String): String? {
        var depth = 0
        var start = -1
        for (i in text.indices) {
            when (text[i]) {
                '{' -> {
                    if (depth == 0) start = i
                    depth++
                }
                '}' -> {
                    depth--
                    if (depth == 0 && start >= 0) return text.substring(start, i + 1)
                }
            }
        }
        return null
    }
}
