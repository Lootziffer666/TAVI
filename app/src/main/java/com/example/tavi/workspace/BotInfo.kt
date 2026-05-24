package com.example.tavi.workspace

data class BotInfo(val id: String, val name: String, val url: String)

object BotRegistry {
    val defaults = listOf(
        BotInfo("chatgpt", "ChatGPT", "https://chat.openai.com"),
        BotInfo("claude", "Claude", "https://claude.ai"),
        BotInfo("gemini", "Gemini", "https://gemini.google.com"),
        BotInfo("perplexity", "Perplexity", "https://perplexity.ai"),
        BotInfo("notebooklm", "NotebookLM", "https://notebooklm.google.com"),
        BotInfo("github", "GitHub", "https://github.com"),
        BotInfo("gdrive", "Drive", "https://drive.google.com"),
        BotInfo("keep", "Keep", "https://keep.google.com")
    )

    val names: Set<String> get() = defaults.map { it.id }.toSet()
}
