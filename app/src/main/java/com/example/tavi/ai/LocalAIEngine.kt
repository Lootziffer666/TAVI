package com.example.tavi.ai

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext

class LocalAIEngine(private val context: Context) {

    private var llmInference: Any? = null
    private var ready = false

    private val systemPrompt = """
        You are TAVI, an Android launcher assistant. Reply ONLY with a single JSON object.
        Valid actions:
        - promote_app: {"action":"promote_app","target":"com.package","message":"..."}
        - demote_app:  {"action":"demote_app","target":"com.package","message":"..."}
        - create_scope: {"action":"create_scope","target":"work","message":"..."}
        - narrate:     {"action":"narrate","message":"..."}
        - simplify_view: {"action":"simplify_view","message":"..."}
        - expand_view: {"action":"expand_view","message":"..."}
        - navigate_bot: {"action":"navigate_bot","target":"chatgpt","message":"..."}
        - execute_shell: {"action":"execute_shell","target":"<cmd>","message":"<plain explanation>"}
        Keep message short. If unclear, use narrate and ask a follow-up question.
    """.trimIndent()

    suspend fun initialize(modelPath: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val optionsClass = Class.forName(
                "com.google.mediapipe.tasks.genai.llminference.LlmInference\$LlmInferenceOptions"
            )
            val builderClass = Class.forName(
                "com.google.mediapipe.tasks.genai.llminference.LlmInference\$LlmInferenceOptions\$Builder"
            )
            val builder = optionsClass.getDeclaredMethod("builder").invoke(null)
            builderClass.getDeclaredMethod("setModelPath", String::class.java)
                .invoke(builder, modelPath)
            builderClass.getDeclaredMethod("setMaxTokens", Int::class.java)
                .invoke(builder, 512)
            val options = builderClass.getDeclaredMethod("build").invoke(builder)

            val llmClass = Class.forName(
                "com.google.mediapipe.tasks.genai.llminference.LlmInference"
            )
            llmInference = llmClass
                .getDeclaredMethod("createFromOptions", Context::class.java, optionsClass)
                .invoke(null, context, options)
            ready = true
        }
    }

    fun isReady(): Boolean = ready && llmInference != null

    fun generate(userMessage: String, contextSummary: String = ""): Flow<String> {
        if (!isReady()) return flowOf(ruleBasedResponse(userMessage))

        val fullPrompt = buildString {
            appendLine(systemPrompt)
            if (contextSummary.isNotBlank()) appendLine("\nContext: $contextSummary")
            appendLine("\nUser: $userMessage")
            appendLine("Response:")
        }

        return callbackFlow {
            runCatching {
                val llm = llmInference!!
                val llmClass = llm.javaClass

                // Dynamically implement LlmInferenceResultListener via Proxy
                // This is the correct pattern — null callback would produce no tokens
                val resultListenerClass = Class.forName(
                    "com.google.mediapipe.tasks.genai.llminference.LlmInference\$LlmInferenceResultListener"
                )
                val proxy = java.lang.reflect.Proxy.newProxyInstance(
                    resultListenerClass.classLoader,
                    arrayOf(resultListenerClass)
                ) { _, method, args ->
                    when (method.name) {
                        "onResult" -> {
                            val token = args?.getOrNull(0) as? String ?: ""
                            val isDone = args?.getOrNull(1) as? Boolean ?: false
                            if (token.isNotEmpty()) trySend(token)
                            if (isDone) close()
                        }
                        "onError" -> {
                            val err = args?.getOrNull(0) as? Exception
                            close(err ?: Exception("LlmInference error"))
                        }
                    }
                    null
                }

                llmClass.getDeclaredMethod("generateAsync", String::class.java, resultListenerClass)
                    .invoke(llm, fullPrompt, proxy)
            }.onFailure { e ->
                trySend(ruleBasedResponse(userMessage))
                close(e)
            }
            awaitClose {}
        }
    }

    fun ruleBasedResponse(query: String): String {
        val lower = query.lowercase()
        return when {
            "focus" in lower || "work" in lower ->
                """{"action":"create_scope","target":"work","message":"Focusing on work. Your workspace is ready."}"""
            "music" in lower || "audio" in lower ->
                """{"action":"create_scope","target":"music","message":"Music apps coming to the front."}"""
            "simple" in lower || "less" in lower || "calm" in lower ->
                """{"action":"simplify_view","message":"Simplifying your space. Only essentials now."}"""
            "show all" in lower || "expand" in lower || "everything" in lower ->
                """{"action":"expand_view","message":"Expanding the full view."}"""
            "social" in lower || "message" in lower || "chat" in lower ->
                """{"action":"create_scope","target":"social","message":"Social apps in focus."}"""
            "clean" in lower || "fossil" in lower || "unused" in lower ->
                """{"action":"narrate","message":"Swipe left on the Fossil Deck to clear unused apps."}"""
            else ->
                """{"action":"narrate","message":"I'm running on local rules. Load a Gemma model for smarter responses."}"""
        }
    }

    fun shutdown() {
        runCatching {
            llmInference?.javaClass?.getDeclaredMethod("close")?.invoke(llmInference)
        }
        llmInference = null
        ready = false
    }
}
