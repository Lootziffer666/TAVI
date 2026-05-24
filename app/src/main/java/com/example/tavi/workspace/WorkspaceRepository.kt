package com.example.tavi.workspace

import com.example.tavi.data.TaviPreferences
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class WorkspaceRepository(private val prefs: TaviPreferences) {

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val listType = Types.newParameterizedType(List::class.java, BotInfo::class.java)
    private val adapter = moshi.adapter<List<BotInfo>>(listType)

    val bots: Flow<List<BotInfo>> = prefs.taviWorkspacesJson.map { json ->
        if (json.isNullOrBlank()) BotRegistry.defaults
        else runCatching { adapter.fromJson(json) ?: BotRegistry.defaults }.getOrElse { BotRegistry.defaults }
    }

    suspend fun saveBots(bots: List<BotInfo>) {
        val json = runCatching { adapter.toJson(bots) }.getOrNull() ?: return
        prefs.setTaviWorkspacesJson(json)
    }

    private suspend fun currentBots(): List<BotInfo> {
        val json = prefs.taviWorkspacesJson.firstOrNull()
        return if (json.isNullOrBlank()) BotRegistry.defaults
        else runCatching { adapter.fromJson(json) ?: BotRegistry.defaults }.getOrElse { BotRegistry.defaults }
    }

    suspend fun addBot(bot: BotInfo) = saveBots(currentBots() + bot)

    suspend fun removeBot(botId: String) = saveBots(currentBots().filter { it.id != botId })

    suspend fun reset() = saveBots(BotRegistry.defaults)
}
