package com.example.tavi.desire

import com.example.tavi.data.TaviPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import java.util.UUID

class WantShelfRepository(private val prefs: TaviPreferences) {

    val items: Flow<List<WantItem>> = prefs.wantShelfJson.map { parseItems(it) }

    suspend fun add(item: WantItem) = prefs.addWantItem(item)
    suspend fun delete(id: String) = prefs.deleteWantItem(id)
    suspend fun clear() = prefs.clearWantShelf()

    private fun parseItems(json: String?): List<WantItem> {
        if (json == null) return emptyList()
        val arr = runCatching { JSONArray(json) }.getOrNull() ?: return emptyList()
        return buildList {
            for (i in 0 until arr.length()) {
                runCatching {
                    val obj = arr.getJSONObject(i)
                    val hintsArr = obj.optJSONArray("hints")
                    WantItem(
                        id = obj.optString("id").ifBlank { UUID.randomUUID().toString() },
                        title = obj.optString("title").ifBlank { "Untitled" },
                        content = obj.optString("content"),
                        subscriptionCost = obj.optString("cost").ifBlank { null },
                        manipulationHints = hintsArr?.let { a -> List(a.length()) { a.getString(it) } } ?: emptyList(),
                        timestamp = obj.optLong("ts", System.currentTimeMillis())
                    )
                }.onSuccess { add(it) }
            }
        }
    }
}
