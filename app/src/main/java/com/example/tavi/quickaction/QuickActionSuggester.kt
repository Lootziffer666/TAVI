package com.example.tavi.quickaction

import com.example.tavi.clipboard.ClipEntry
import com.example.tavi.clipboard.ClipType

enum class QuickActionType { OPEN_URL, DIAL, SAVE_SNIPPET, SAVE_CAPSULE, PARK }

data class QuickAction(val type: QuickActionType, val label: String)

object QuickActionSuggester {
    fun suggest(entry: ClipEntry): List<QuickAction> = buildList {
        when (entry.type) {
            ClipType.URL -> {
                add(QuickAction(QuickActionType.OPEN_URL, "Open"))
                add(QuickAction(QuickActionType.PARK, "Park"))
            }
            ClipType.PHONE -> add(QuickAction(QuickActionType.DIAL, "Call"))
            ClipType.CODE -> Unit
            ClipType.TEXT, ClipType.OTHER -> Unit
        }
        add(QuickAction(QuickActionType.SAVE_SNIPPET, "→ Snippet"))
        add(QuickAction(QuickActionType.SAVE_CAPSULE, "→ Capsule"))
    }
}
