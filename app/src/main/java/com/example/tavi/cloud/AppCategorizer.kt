package com.example.tavi.cloud

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AppCategorizer(private val service: GeminiApiService, private val apiKey: String) {

    suspend fun categorize(packageNames: List<String>): Map<String, String> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank() || apiKey == "placeholder") return@withContext emptyMap()
        val prompt = """
            Categorize these Android app package names into one of: Social, Productivity, Games, Tools,
            Streaming, Finance, Health, Education, Utilities, Other.
            Respond with valid JSON only: {"packageName": "Category"}.
            Apps: ${packageNames.joinToString(", ")}
        """.trimIndent()
        runCatching {
            val response = service.generateContent(apiKey, GeminiRequest(
                contents = listOf(GeminiContent(parts = listOf(GeminiPart(prompt))))
            ))
            val text = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
            val jsonStr = text.substringAfter("{").substringBeforeLast("}").let { "{$it}" }
            val json = JSONObject(jsonStr)
            packageNames.associateWith { pkg -> json.optString(pkg, "Other") }
        }.getOrElse { emptyMap() }
    }
}
