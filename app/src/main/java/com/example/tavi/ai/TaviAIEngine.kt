package com.example.tavi.ai

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.tavi.cloud.GeminiApiService
import com.example.tavi.warden.TaviWarden
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEmpty

class TaviAIEngine(
    private val context: Context,
    private val localEngine: LocalAIEngine,
    private val geminiService: GeminiApiService?,
    private val warden: TaviWarden
) {
    private val systemPromptForCloud = """
        You are TAVI, an Android launcher assistant. Respond ONLY with valid JSON.
        Available actions: promote_app, demote_app, create_scope, narrate, simplify_view, expand_view, navigate_bot, execute_shell.
        Format: {"action": "<action>", "target": "<optional>", "message": "<optional user message>"}
        If unclear, use action=narrate with a clarifying question.
    """.trimIndent()

    fun generate(prompt: String, contextSummary: String): Flow<String> {
        return when {
            localEngine.isReady() -> localEngine.generate(prompt, contextSummary)
                .catch { emit(localEngine.ruleBasedResponse(prompt)) }
            hasNetwork() -> cloudFallback(prompt, contextSummary)
            else -> flow { emit(localEngine.ruleBasedResponse(prompt)) }
        }
    }

    private fun cloudFallback(prompt: String, contextSummary: String): Flow<String> = flow {
        val service = geminiService ?: run {
            emit(localEngine.ruleBasedResponse(prompt))
            return@flow
        }
        val request = com.example.tavi.cloud.GeminiRequest(
            contents = listOf(
                com.example.tavi.cloud.GeminiContent(
                    parts = listOf(com.example.tavi.cloud.GeminiPart("$systemPromptForCloud\n\nContext: $contextSummary\n\nUser: $prompt"))
                )
            )
        )
        runCatching {
            val response = service.generateContent("", request)
            val text = response.candidates.firstOrNull()
                ?.content?.parts?.firstOrNull()?.text
                ?: localEngine.ruleBasedResponse(prompt)
            emit(text)
        }.onFailure { emit(localEngine.ruleBasedResponse(prompt)) }
    }

    private fun hasNetwork(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
