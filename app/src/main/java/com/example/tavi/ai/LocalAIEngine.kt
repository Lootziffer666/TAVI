package com.example.tavi.ai

import android.content.Context
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow

class LocalAIEngine(private val context: Context) {

    private var inference: Any? = null
    private var isInitialized = false

    private val systemPrompt = """
        You are TAVI, an Android launcher assistant. Respond ONLY with valid JSON.
        Available actions: promote_app, demote_app, create_scope, narrate, simplify_view, expand_view, navigate_bot, execute_shell.
        Format: {"action": "<action>", "target": "<packageName or botName>", "message": "<optional user-facing message>"}
        For execute_shell: only suggest if user explicitly uses ! prefix. Always include message explaining what the command does.
        If unclear, use action=narrate with a helpful message asking for clarification.
    """.trimIndent()

    fun initialize(modelPath: String) {
        runCatching {
            val llmClass = Class.forName("com.google.mediapipe.tasks.genai.llminference.LlmInference")
            val optionsClass = Class.forName("com.google.mediapipe.tasks.genai.llminference.LlmInference\$LlmInferenceOptions")
            val builder = optionsClass.getMethod("builder").invoke(null)
            optionsClass.getMethod("setModelPath", String::class.java).invoke(builder, modelPath)
            optionsClass.getMethod("setMaxTokens", Int::class.java).invoke(builder, 512)
            val options = optionsClass.getMethod("build").invoke(builder)
            inference = llmClass.getMethod("createFromOptions", Context::class.java, optionsClass)
                .invoke(null, context, options)
            isInitialized = true
        }
    }

    fun isReady(): Boolean = isInitialized && inference != null

    fun generate(prompt: String, context: String): Flow<String> = if (isReady()) {
        callbackFlow {
            val fullPrompt = "$systemPrompt\n\nContext: $context\n\nUser: $prompt"
            runCatching {
                val generateMethod = inference!!.javaClass.getMethod(
                    "generateResponseAsync", String::class.java, Any::class.java
                )
                // Streaming via result listener
                generateMethod.invoke(inference, fullPrompt, null)
            }.onFailure { close(it) }
            awaitClose()
        }
    } else {
        flow { emit(ruleBasedResponse(prompt)) }
    }

    fun ruleBasedResponse(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("focus") -> """{"action":"simplify_view","message":"Switching to focus mode"}"""
            lower.contains("expand") || lower.contains("all") -> """{"action":"expand_view","message":"Showing all apps"}"""
            lower.contains("scope") || lower.contains("work") -> """{"action":"create_scope","target":"work","message":"Work scope activated"}"""
            lower.contains("clean") || lower.contains("fossil") -> """{"action":"narrate","message":"Swipe to the Fossil Deck on the left to clean up unused apps"}"""
            else -> """{"action":"narrate","message":"I\'m running on local rules. Load a Gemma model for smarter responses."}"""
        }
    }

    fun shutdown() {
        runCatching { inference?.javaClass?.getMethod("close")?.invoke(inference) }
        inference = null
        isInitialized = false
    }
}
