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
            val json = JSONObject(text.substringAfter("{").substringBeforeLast("}").let { "{$it}" })
            json.getString("command")
        }
    }
}
