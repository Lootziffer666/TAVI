package com.example.tavi.snippet

import com.example.tavi.data.TaviPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class SnippetRepository(private val prefs: TaviPreferences) {

    val snippets: Flow<List<SnippetEntry>> = prefs.snippetsJson.map { parseSnippets(it) }

    suspend fun add(entry: SnippetEntry) = prefs.addSnippet(entry)
    suspend fun delete(id: String) = prefs.deleteSnippet(id)
    suspend fun toggleFavorite(id: String) = prefs.toggleSnippetFavorite(id)

    private fun parseSnippets(json: String?): List<SnippetEntry> {
        json ?: return emptyList()
        return runCatching {
            val arr = JSONArray(json)
            List(arr.length()) { i ->
                val obj = arr.getJSONObject(i)
                val tagsArr = obj.optJSONArray("tags")
                SnippetEntry(
                    id = obj.optString("id").ifBlank { UUID.randomUUID().toString() },
                    title = obj.getString("title"),
                    content = obj.getString("content"),
                    tags = tagsArr?.let { t -> List(t.length()) { t.getString(it) } } ?: emptyList(),
                    isFavorite = obj.optBoolean("fav", false),
                    timestamp = obj.optLong("ts", System.currentTimeMillis())
                )
            }
        }.getOrDefault(emptyList())
    }
}
