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
        val CLIP_HISTORY_JSON = stringPreferencesKey("clipHistory")
        val SNIPPETS_JSON = stringPreferencesKey("snippets")
        val CAPSULES_JSON = stringPreferencesKey("capsules")
        val NOTIFICATION_RULES_JSON = stringPreferencesKey("notificationRules")
        val GAME_WATCH_INTERVAL = intPreferencesKey("gameWatchInterval")
        val WANT_SHELF_JSON = stringPreferencesKey("wantShelf")
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
    val clipHistoryJson: Flow<String?> = context.dataStore.data.map { it[CLIP_HISTORY_JSON] }
    val snippetsJson: Flow<String?> = context.dataStore.data.map { it[SNIPPETS_JSON] }
    val capsulesJson: Flow<String?> = context.dataStore.data.map { it[CAPSULES_JSON] }
    val notificationRulesJson: Flow<String?> = context.dataStore.data.map { it[NOTIFICATION_RULES_JSON] }
    val gameWatchInterval: Flow<Int> = context.dataStore.data.map { it[GAME_WATCH_INTERVAL] ?: 60 }
    val wantShelfJson: Flow<String?> = context.dataStore.data.map { it[WANT_SHELF_JSON] }
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
    suspend fun addClipEntry(entry: com.example.tavi.clipboard.ClipEntry) = context.dataStore.edit { prefs ->
        val existing = runCatching {
            org.json.JSONArray(prefs[CLIP_HISTORY_JSON] ?: "[]")
        }.getOrDefault(org.json.JSONArray())
        val obj = org.json.JSONObject().apply {
            put("content", entry.content)
            put("type", entry.type.name)
            put("ts", entry.timestamp)
        }
        val updated = org.json.JSONArray()
        updated.put(obj)
        for (i in 0 until minOf(existing.length(), 9)) updated.put(existing.getJSONObject(i))
        prefs[CLIP_HISTORY_JSON] = updated.toString()
    }
    suspend fun clearClipHistory() = context.dataStore.edit { it.remove(CLIP_HISTORY_JSON) }

    suspend fun addSnippet(entry: com.example.tavi.snippet.SnippetEntry) = context.dataStore.edit { prefs ->
        val existing = runCatching { org.json.JSONArray(prefs[SNIPPETS_JSON] ?: "[]") }.getOrDefault(org.json.JSONArray())
        val obj = org.json.JSONObject().apply {
            put("id", entry.id)
            put("title", entry.title)
            put("content", entry.content)
            put("tags", org.json.JSONArray(entry.tags))
            put("fav", entry.isFavorite)
            put("ts", entry.timestamp)
        }
        val updated = org.json.JSONArray()
        updated.put(obj)
        for (i in 0 until minOf(existing.length(), 99)) updated.put(existing.getJSONObject(i))
        prefs[SNIPPETS_JSON] = updated.toString()
    }

    suspend fun deleteSnippet(id: String) = context.dataStore.edit { prefs ->
        val existing = runCatching { org.json.JSONArray(prefs[SNIPPETS_JSON] ?: "[]") }.getOrDefault(org.json.JSONArray())
        val updated = org.json.JSONArray()
        for (i in 0 until existing.length()) {
            val obj = existing.getJSONObject(i)
            if (obj.optString("id") != id) updated.put(obj)
        }
        prefs[SNIPPETS_JSON] = updated.toString()
    }

    suspend fun toggleSnippetFavorite(id: String) = context.dataStore.edit { prefs ->
        val existing = runCatching { org.json.JSONArray(prefs[SNIPPETS_JSON] ?: "[]") }.getOrDefault(org.json.JSONArray())
        val updated = org.json.JSONArray()
        for (i in 0 until existing.length()) {
            val obj = existing.getJSONObject(i)
            if (obj.optString("id") == id) obj.put("fav", !obj.optBoolean("fav", false))
            updated.put(obj)
        }
        prefs[SNIPPETS_JSON] = updated.toString()
    }

    suspend fun addCapsule(capsule: com.example.tavi.capsule.WorkCapsule) = context.dataStore.edit { prefs ->
        val existing = runCatching { org.json.JSONArray(prefs[CAPSULES_JSON] ?: "[]") }.getOrDefault(org.json.JSONArray())
        val obj = org.json.JSONObject().apply {
            put("id", capsule.id)
            put("title", capsule.title)
            put("content", capsule.content)
            put("src", capsule.source.name)
            put("ts", capsule.timestamp)
        }
        val updated = org.json.JSONArray()
        updated.put(obj)
        for (i in 0 until minOf(existing.length(), 49)) updated.put(existing.getJSONObject(i))
        prefs[CAPSULES_JSON] = updated.toString()
    }

    suspend fun deleteCapsule(id: String) = context.dataStore.edit { prefs ->
        val existing = runCatching { org.json.JSONArray(prefs[CAPSULES_JSON] ?: "[]") }.getOrDefault(org.json.JSONArray())
        val updated = org.json.JSONArray()
        for (i in 0 until existing.length()) {
            val obj = existing.getJSONObject(i)
            if (obj.optString("id") != id) updated.put(obj)
        }
        prefs[CAPSULES_JSON] = updated.toString()
    }

    suspend fun clearCapsules() = context.dataStore.edit { it.remove(CAPSULES_JSON) }

    suspend fun toggleNotificationRule(id: String) = context.dataStore.edit { prefs ->
        val existing = runCatching { JSONArray(prefs[NOTIFICATION_RULES_JSON] ?: "[]") }.getOrDefault(JSONArray())
        val updated = JSONArray()
        var found = false
        for (i in 0 until existing.length()) {
            val obj = existing.getJSONObject(i)
            if (obj.optString("id") == id) {
                obj.put("active", !obj.optBoolean("active", false))
                found = true
            }
            updated.put(obj)
        }
        // If id not found (e.g. first run with defaults), write defaults then toggle
        if (!found) {
            val defaults = com.example.tavi.notification.NotificationRuleRepository.defaultRules()
            val defaultsArr = JSONArray()
            defaults.forEach { rule ->
                defaultsArr.put(org.json.JSONObject().apply {
                    put("id", rule.id)
                    put("name", rule.name)
                    put("window", rule.timeWindow)
                    put("active", if (rule.id == id) !rule.isActive else rule.isActive)
                    put("apps", JSONArray(rule.allowedApps))
                })
            }
            prefs[NOTIFICATION_RULES_JSON] = defaultsArr.toString()
        } else {
            prefs[NOTIFICATION_RULES_JSON] = updated.toString()
        }
    }

    suspend fun setGameWatchInterval(seconds: Int) = context.dataStore.edit { it[GAME_WATCH_INTERVAL] = seconds }

    suspend fun addWantItem(item: com.example.tavi.desire.WantItem) = context.dataStore.edit { prefs ->
        val existing = runCatching { org.json.JSONArray(prefs[WANT_SHELF_JSON] ?: "[]") }.getOrDefault(org.json.JSONArray())
        val obj = org.json.JSONObject().apply {
            put("id", item.id)
            put("title", item.title)
            put("content", item.content)
            put("cost", item.subscriptionCost ?: "")
            put("hints", org.json.JSONArray(item.manipulationHints))
            put("ts", item.timestamp)
        }
        val updated = org.json.JSONArray()
        updated.put(obj)
        for (i in 0 until minOf(existing.length(), 29)) updated.put(existing.getJSONObject(i))
        prefs[WANT_SHELF_JSON] = updated.toString()
    }

    suspend fun deleteWantItem(id: String) = context.dataStore.edit { prefs ->
        val existing = runCatching { org.json.JSONArray(prefs[WANT_SHELF_JSON] ?: "[]") }.getOrDefault(org.json.JSONArray())
        val updated = org.json.JSONArray()
        for (i in 0 until existing.length()) {
            val obj = existing.getJSONObject(i)
            if (obj.optString("id") != id) updated.put(obj)
        }
        prefs[WANT_SHELF_JSON] = updated.toString()
    }

    suspend fun clearWantShelf() = context.dataStore.edit { it.remove(WANT_SHELF_JSON) }

    suspend fun addRecentScope(scope: String) = context.dataStore.edit { prefs ->
        val current = runCatching {
            val arr = JSONArray(prefs[RECENT_SCOPES_JSON] ?: "[]")
            List(arr.length()) { arr.getString(it) }
        }.getOrDefault(emptyList())
        val updated = (listOf(scope) + current.filter { it != scope }).take(5)
        prefs[RECENT_SCOPES_JSON] = JSONArray(updated).toString()
    }
}
