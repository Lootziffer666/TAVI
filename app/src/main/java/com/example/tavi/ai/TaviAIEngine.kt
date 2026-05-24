package com.example.tavi.ai

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.tavi.cloud.GeminiApiService
import com.example.tavi.cloud.GeminiContent
import com.example.tavi.cloud.GeminiPart
import com.example.tavi.cloud.GeminiRequest
import com.example.tavi.warden.TaviWarden
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class TaviAIEngine(
    private val context: Context,
    private val localEngine: LocalAIEngine,
    private val geminiService: GeminiApiService,
    private val warden: TaviWarden,
    private val geminiApiKey: String = ""
) {
    private val cloudSystemPrompt = """
        You are TAVI, an Android launcher assistant. Respond ONLY with valid JSON.
        Available actions: promote_app, demote_app, create_scope, narrate, simplify_view, expand_view, navigate_bot, execute_shell.
        Format: {"action": "<action>", "target": "<optional>", "message": "<optional user message>"}
        If unclear, use action=narrate with a clarifying question.
    """.trimIndent()

    fun generate(prompt: String, contextSummary: String): Flow<String> = when {
        localEngine.isReady() ->
            localEngine.generate(prompt, contextSummary)
                .catch { emit(localEngine.ruleBasedResponse(prompt)) }
        geminiApiKey.isNotBlank() && hasNetwork() ->
            cloudFallback(prompt, contextSummary)
        else ->
            flow { emit(localEngine.ruleBasedResponse(prompt)) }
    }

    private fun cloudFallback(prompt: String, contextSummary: String): Flow<String> = flow {
        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(
                        GeminiPart("$cloudSystemPrompt\n\nContext: $contextSummary\n\nUser: $prompt")
                    )
                )
            )
        )
        runCatching {
            val response = geminiService.generateContent(geminiApiKey, request)
            val text = response.candidates.firstOrNull()
                ?.content?.parts?.firstOrNull()?.text
                ?: localEngine.ruleBasedResponse(prompt)
            emit(text)
        }.onFailure {
            emit(localEngine.ruleBasedResponse(prompt))
        }
    }

    private fun hasNetwork(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val caps = cm.getNetworkCapabilities(cm.activeNetwork ?: return false) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
