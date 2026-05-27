package com.example.tavi.capsule

import com.example.tavi.data.TaviPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class CapsuleRepository(private val prefs: TaviPreferences) {

    val capsules: Flow<List<WorkCapsule>> = prefs.capsulesJson.map { parseCapsules(it) }

    suspend fun add(capsule: WorkCapsule) = prefs.addCapsule(capsule)
    suspend fun delete(id: String) = prefs.deleteCapsule(id)

    private fun parseCapsules(json: String?): List<WorkCapsule> {
        json ?: return emptyList()
        val arr = runCatching { JSONArray(json) }.getOrNull() ?: return emptyList()
        return buildList {
            for (i in 0 until arr.length()) {
                runCatching {
                    val obj = arr.getJSONObject(i)
                    WorkCapsule(
                        id = obj.optString("id").ifBlank { UUID.randomUUID().toString() },
                        title = obj.optString("title").ifBlank { "Untitled" },
                        content = obj.optString("content"),
                        source = runCatching { CapsuleSource.valueOf(obj.optString("src", "CLIPBOARD")) }
                            .getOrDefault(CapsuleSource.CLIPBOARD),
                        timestamp = obj.optLong("ts", System.currentTimeMillis())
                    )
                }.onSuccess { add(it) }
            }
        }
    }
}
