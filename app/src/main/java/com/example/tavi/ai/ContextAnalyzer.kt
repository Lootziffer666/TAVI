package com.example.tavi.ai

import com.example.tavi.garden.GardenNode
import java.util.Calendar

class ContextAnalyzer {

    fun buildContextString(
        foreground: List<GardenNode>,
        midground: List<GardenNode>,
        currentScope: String?
    ): String {
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val isWeekend = cal.get(Calendar.DAY_OF_WEEK).let { it == Calendar.SATURDAY || it == Calendar.SUNDAY }

        val timeOfDay = when (hour) {
            in 5..11 -> "morning"
            in 12..16 -> "afternoon"
            in 17..20 -> "evening"
            else -> "night"
        }

        val topApps = foreground.take(5).joinToString(", ") {
            "${it.label}(${it.launchCount} launches, ${it.growthStage.name})"
        }

        val scopeLine = if (currentScope != null) "Current scope: $currentScope" else "No active scope"
        val dayType = if (isWeekend) "weekend" else "weekday"

        val scopeClusters = (foreground + midground)
            .filter { it.scopeTag != null }
            .groupBy { it.scopeTag }
            .entries.joinToString("; ") { (tag, apps) -> "$tag: ${apps.size} apps" }

        return buildString {
            appendLine("Time: $timeOfDay on a $dayType")
            appendLine(scopeLine)
            appendLine("Focus zone: $topApps")
            if (scopeClusters.isNotEmpty()) appendLine("Scope clusters: $scopeClusters")
        }.trim()
    }
}
