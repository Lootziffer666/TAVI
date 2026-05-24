package com.example.tavi.ai

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.tavi.garden.GardenEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class MobileActionsRouter(
    private val context: Context,
    private val gardenEngine: GardenEngine
) {

    suspend fun parseAndRoute(rawJson: String): AIResponse = withContext(Dispatchers.Default) {
        runCatching {
            val json = JSONObject(rawJson)
            AIResponse(
                action = json.optString("action", AIActions.NARRATE),
                target = json.optString("target").takeIf { it.isNotBlank() },
                message = json.optString("message").takeIf { it.isNotBlank() }
            )
        }.getOrElse {
            AIResponse(action = AIActions.NARRATE, message = "I couldn't process that. Try again.")
        }
    }

    suspend fun execute(response: AIResponse) {
        when (response.action) {
            AIActions.PROMOTE_APP -> response.target?.let { gardenEngine.recordLaunch(it) }
            AIActions.DEMOTE_APP -> response.target?.let { gardenEngine.markAsFossil(it) }
            AIActions.NARRATE -> Unit
            AIActions.SIMPLIFY_VIEW -> Unit
            AIActions.EXPAND_VIEW -> Unit
            AIActions.CREATE_SCOPE -> Unit
            AIActions.NAVIGATE_BOT -> Unit
            AIActions.EXECUTE_SHELL -> Unit
        }
    }
}
