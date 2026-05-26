package com.example.tavi.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray

private val Context.dataStore by preferencesDataStore(name = "tavi_prefs")

class TaviPreferences(private val context: Context) {

    companion object {
        val MAX_FOCUS_ITEMS = intPreferencesKey("maxFocusItems")
        val REDUCE_MOTION = booleanPreferencesKey("reduceMotion")
        val CURRENT_SCOPE_TAG = stringPreferencesKey("currentScopeTag")
        val AI_MODEL_PATH = stringPreferencesKey("aiModelPath")
        val WARDEN_EMERGENCY_OFF = booleanPreferencesKey("wardenEmergencyOff")
        val PRIVATE_MODE_ENABLED = booleanPreferencesKey("privateModeEnabled")
        val SHIZUKU_ENABLED = booleanPreferencesKey("shizukuEnabled")
        val BOT_WORKSPACES_ENABLED = booleanPreferencesKey("botWorkspacesEnabled")
        val CLOUD_AI_ENABLED = booleanPreferencesKey("cloudAiEnabled")
        val TAVI_WORKSPACES_JSON = stringPreferencesKey("taviWorkspaces")
        val SESSION_ONLY_MODE = booleanPreferencesKey("sessionOnlyMode")
        val RECENT_SCOPES_JSON = stringPreferencesKey("recentScopes")
    }

    val maxFocusItems: Flow<Int> = context.dataStore.data.map { it[MAX_FOCUS_ITEMS] ?: 5 }
    val reduceMotion: Flow<Boolean> = context.dataStore.data.map { it[REDUCE_MOTION] ?: false }
    val currentScopeTag: Flow<String?> = context.dataStore.data.map { it[CURRENT_SCOPE_TAG] }
    val aiModelPath: Flow<String?> = context.dataStore.data.map { it[AI_MODEL_PATH] }
    val wardenEmergencyOff: Flow<Boolean> = context.dataStore.data.map { it[WARDEN_EMERGENCY_OFF] ?: false }
    val privateModeEnabled: Flow<Boolean> = context.dataStore.data.map { it[PRIVATE_MODE_ENABLED] ?: false }
    val shizukuEnabled: Flow<Boolean> = context.dataStore.data.map { it[SHIZUKU_ENABLED] ?: false }
    val botWorkspacesEnabled: Flow<Boolean> = context.dataStore.data.map { it[BOT_WORKSPACES_ENABLED] ?: true }
    val cloudAiEnabled: Flow<Boolean> = context.dataStore.data.map { it[CLOUD_AI_ENABLED] ?: false }
    val taviWorkspacesJson: Flow<String?> = context.dataStore.data.map { it[TAVI_WORKSPACES_JSON] }
    val sessionOnlyMode: Flow<Boolean> = context.dataStore.data.map { it[SESSION_ONLY_MODE] ?: false }
    val recentScopes: Flow<List<String>> = context.dataStore.data.map { prefs ->
        val json = prefs[RECENT_SCOPES_JSON] ?: return@map emptyList()
        runCatching {
            val arr = JSONArray(json)
            List(arr.length()) { arr.getString(it) }
        }.getOrDefault(emptyList())
    }

    suspend fun setMaxFocusItems(value: Int) = context.dataStore.edit { it[MAX_FOCUS_ITEMS] = value }
    suspend fun setReduceMotion(value: Boolean) = context.dataStore.edit { it[REDUCE_MOTION] = value }
    suspend fun setScopeTag(tag: String?) = context.dataStore.edit {
        if (tag != null) it[CURRENT_SCOPE_TAG] = tag else it.remove(CURRENT_SCOPE_TAG)
    }
    suspend fun setAiModelPath(path: String) = context.dataStore.edit { it[AI_MODEL_PATH] = path }
    suspend fun setShizukuEnabled(value: Boolean) = context.dataStore.edit { it[SHIZUKU_ENABLED] = value }
    suspend fun setPrivateModeEnabled(value: Boolean) = context.dataStore.edit { it[PRIVATE_MODE_ENABLED] = value }
    suspend fun setCloudAiEnabled(value: Boolean) = context.dataStore.edit { it[CLOUD_AI_ENABLED] = value }
    suspend fun setBotWorkspacesEnabled(value: Boolean) = context.dataStore.edit { it[BOT_WORKSPACES_ENABLED] = value }
    suspend fun setEmergencyOff(value: Boolean) = context.dataStore.edit { it[WARDEN_EMERGENCY_OFF] = value }
    suspend fun setTaviWorkspacesJson(json: String) = context.dataStore.edit { it[TAVI_WORKSPACES_JSON] = json }
    suspend fun setSessionOnlyMode(value: Boolean) = context.dataStore.edit { it[SESSION_ONLY_MODE] = value }
    suspend fun addRecentScope(scope: String) = context.dataStore.edit { prefs ->
        val current = runCatching {
            val arr = JSONArray(prefs[RECENT_SCOPES_JSON] ?: "[]")
            List(arr.length()) { arr.getString(it) }
        }.getOrDefault(emptyList())
        val updated = (listOf(scope) + current.filter { it != scope }).take(5)
        prefs[RECENT_SCOPES_JSON] = JSONArray(updated).toString()
    }
}
