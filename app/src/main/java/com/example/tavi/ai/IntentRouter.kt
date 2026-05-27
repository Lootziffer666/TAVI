package com.example.tavi.ai

sealed class IntentRouterResult {
    data class QueryAI(val query: String) : IntentRouterResult()
    data class NavigateBot(val botName: String) : IntentRouterResult()
    data class ShellCommand(val command: String) : IntentRouterResult()
    data class BuildLayout(val prompt: String) : IntentRouterResult()
    data class OpenUrl(val url: String) : IntentRouterResult()
    data class HandoffToBot(val botId: String, val content: String) : IntentRouterResult()
    object ShowClipboard : IntentRouterResult()
    object ShowSnippets : IntentRouterResult()
    data class SaveSnippet(val title: String) : IntentRouterResult()
    object ShowCapsules : IntentRouterResult()
    data class SaveCapsule(val title: String) : IntentRouterResult()
    object OpenSettings : IntentRouterResult()
    data class CaptureImage(val prompt: String) : IntentRouterResult()
    object ShowWantShelf : IntentRouterResult()
    data class SaveWantItem(val title: String) : IntentRouterResult()
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
            trimmed.equals("snips", ignoreCase = true) || trimmed.startsWith("snip:") -> {
                val rest = trimmed.removePrefix("snip:").trim()
                if (rest.startsWith("save ")) IntentRouterResult.SaveSnippet(rest.removePrefix("save ").trim())
                else IntentRouterResult.ShowSnippets
            }
            trimmed.equals("caps", ignoreCase = true) || trimmed.startsWith("cap:") -> {
                val rest = trimmed.removePrefix("cap:").trim()
                if (rest.startsWith("save ")) IntentRouterResult.SaveCapsule(rest.removePrefix("save ").trim())
                else IntentRouterResult.ShowCapsules
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
            trimmed.equals("want:", ignoreCase = true) || trimmed.startsWith("want:") -> {
                val rest = trimmed.removePrefix("want:").trim()
                if (rest.startsWith("save ")) IntentRouterResult.SaveWantItem(rest.removePrefix("save ").trim())
                else IntentRouterResult.ShowWantShelf
            }
            trimmed.equals("img:", ignoreCase = true) || trimmed.startsWith("img: ") ||
            (trimmed.startsWith("img:") && trimmed.length > 4) -> {
                val prompt = trimmed.removePrefix("img:").trim()
                    .ifBlank { "Extract the intent and actionable information from this image." }
                IntentRouterResult.CaptureImage(prompt)
            }
            trimmed.startsWith("http://") || trimmed.startsWith("https://") -> {
                IntentRouterResult.OpenUrl(trimmed)
            }
            trimmed.equals("settings", ignoreCase = true) -> IntentRouterResult.OpenSettings
            else -> IntentRouterResult.QueryAI(trimmed)
        }
    }
}
