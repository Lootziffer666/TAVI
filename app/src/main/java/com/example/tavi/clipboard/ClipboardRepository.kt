package com.example.tavi.clipboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.example.tavi.data.TaviPreferences
import kotlinx.coroutines.flow.*
import org.json.JSONArray
import org.json.JSONObject

private val URL_REGEX = Regex("^https?://.*", RegexOption.IGNORE_CASE)
private val PHONE_REGEX = Regex("^[+]?[(]?[0-9]{3}[)]?[-\\s.]?[0-9]{3}[-\\s.]?[0-9]{4,6}$")

class ClipboardRepository(
    private val context: Context,
    private val prefs: TaviPreferences
) {
    // In-memory list for session-only mode entries (not written to DataStore)
    private val _sessionHistory = MutableStateFlow<List<ClipEntry>>(emptyList())

    val history: Flow<List<ClipEntry>> = combine(
        prefs.clipHistoryJson.map { parseHistory(it) },
        _sessionHistory,
        prefs.privateModeEnabled
    ) { persisted, session, isPrivate ->
        val base = if (isPrivate) session else (session + persisted)
        base.distinctBy { it.content }.take(10)
    }

    fun read(): ClipEntry? {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val text = cm.primaryClip?.getItemAt(0)?.text?.toString()?.trim() ?: return null
        if (text.isBlank()) return null
        return ClipEntry(content = text, type = detectType(text))
    }

    suspend fun addToHistory(entry: ClipEntry, persist: Boolean) {
        if (persist) {
            prefs.addClipEntry(entry)
        } else {
            _sessionHistory.update { list ->
                (listOf(entry) + list.filter { it.content != entry.content }).take(10)
            }
        }
    }

    suspend fun clearHistory() {
        _sessionHistory.value = emptyList()
        prefs.clearClipHistory()
    }

    private fun detectType(text: String): ClipType = when {
        URL_REGEX.matches(text) -> ClipType.URL
        PHONE_REGEX.matches(text) -> ClipType.PHONE
        text.contains("{") || text.trimStart().startsWith("fun ") ||
            text.trimStart().startsWith("val ") || text.trimStart().startsWith("def ") ||
            text.trimStart().startsWith("class ") -> ClipType.CODE
        else -> ClipType.TEXT
    }

    private fun parseHistory(json: String?): List<ClipEntry> {
        json ?: return emptyList()
        return runCatching {
            val arr = JSONArray(json)
            List(arr.length()) { i ->
                val obj = arr.getJSONObject(i)
                ClipEntry(
                    content = obj.getString("content"),
                    type = ClipType.valueOf(obj.optString("type", "TEXT")),
                    timestamp = obj.optLong("ts", System.currentTimeMillis())
                )
            }
        }.getOrDefault(emptyList())
    }
}
