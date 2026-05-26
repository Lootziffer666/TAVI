package com.example.tavi.ai

sealed class IntentRouterResult {
    data class QueryAI(val query: String) : IntentRouterResult()
    data class NavigateBot(val botName: String) : IntentRouterResult()
    data class ShellCommand(val command: String) : IntentRouterResult()
    data class BuildLayout(val prompt: String) : IntentRouterResult()
    data class OpenUrl(val url: String) : IntentRouterResult()
    data class HandoffToBot(val botId: String, val content: String) : IntentRouterResult()
    object ShowClipboard : IntentRouterResult()
    object OpenSettings : IntentRouterResult()
}

class IntentRouter(private val knownBotNames: Set<String>) {

    fun route(input: String): IntentRouterResult {
        val trimmed = input.trim()
        return when {
            trimmed.startsWith("?") -> {
                val query = trimmed.removePrefix("?").trim()
                if (query.lowercase() in knownBotNames) IntentRouterResult.NavigateBot(query.lowercase())
                else IntentRouterResult.QueryAI(query)
            }
            trimmed.startsWith("!") -> {
                IntentRouterResult.ShellCommand(trimmed.removePrefix("!").trim())
            }
            trimmed.startsWith("/build") -> {
                IntentRouterResult.BuildLayout(trimmed.removePrefix("/build").trim())
            }
            trimmed.equals("clip:", ignoreCase = true) || trimmed.startsWith("clip: ") -> {
                IntentRouterResult.ShowClipboard
            }
            trimmed.startsWith(">") -> {
                val rest = trimmed.removePrefix(">")
                val (botId, content) = if (": " in rest) {
                    val parts = rest.split(": ", limit = 2)
                    parts[0].trim().lowercase() to parts[1].trim()
                } else {
                    rest.trim().lowercase() to ""
                }
                IntentRouterResult.HandoffToBot(botId, content)
            }
            trimmed.startsWith("http://") || trimmed.startsWith("https://") -> {
                IntentRouterResult.OpenUrl(trimmed)
            }
            trimmed.equals("settings", ignoreCase = true) -> IntentRouterResult.OpenSettings
            else -> IntentRouterResult.QueryAI(trimmed)
        }
    }
}
