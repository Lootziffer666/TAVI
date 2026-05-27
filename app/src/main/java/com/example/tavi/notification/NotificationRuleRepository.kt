package com.example.tavi.notification

import com.example.tavi.data.TaviPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import java.util.UUID

class NotificationRuleRepository(private val prefs: TaviPreferences) {

    val rules: Flow<List<NotificationRule>> = prefs.notificationRulesJson.map { parseRules(it) }

    suspend fun toggleRule(id: String) = prefs.toggleNotificationRule(id)

    private fun parseRules(json: String?): List<NotificationRule> {
        if (json == null) return defaultRules()
        val arr = runCatching { JSONArray(json) }.getOrNull() ?: return defaultRules()
        val result = buildList {
            for (i in 0 until arr.length()) {
                runCatching {
                    val obj = arr.getJSONObject(i)
                    val appsArr = obj.optJSONArray("apps")
                    NotificationRule(
                        id = obj.optString("id").ifBlank { UUID.randomUUID().toString() },
                        name = obj.optString("name").ifBlank { "Rule" },
                        timeWindow = obj.optString("window"),
                        isActive = obj.optBoolean("active", false),
                        allowedApps = appsArr?.let { a -> List(a.length()) { a.getString(it) } } ?: emptyList()
                    )
                }.onSuccess { add(it) }
            }
        }
        return result.ifEmpty { defaultRules() }
    }

    companion object {
        fun defaultRules() = listOf(
            NotificationRule("deep_focus", "Deep focus", "09:00 – 12:00", false, listOf("Calendar", "Phone")),
            NotificationRule("sleep", "Sleep", "23:00 – 07:00", false, listOf("Phone", "Alarms")),
            NotificationRule("open_hours", "Open hours", "12:00 – 18:00", true, emptyList())
        )
    }
}
