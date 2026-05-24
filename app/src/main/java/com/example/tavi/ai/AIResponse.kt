package com.example.tavi.ai

data class AIResponse(
    val action: String,
    val target: String? = null,
    val message: String? = null,
    val parameters: Map<String, String> = emptyMap()
)

object AIActions {
    const val PROMOTE_APP = "promote_app"
    const val DEMOTE_APP = "demote_app"
    const val CREATE_SCOPE = "create_scope"
    const val NARRATE = "narrate"
    const val SIMPLIFY_VIEW = "simplify_view"
    const val EXPAND_VIEW = "expand_view"
    const val NAVIGATE_BOT = "navigate_bot"
    const val EXECUTE_SHELL = "execute_shell"
}
