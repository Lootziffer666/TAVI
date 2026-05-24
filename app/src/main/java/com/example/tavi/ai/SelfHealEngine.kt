package com.example.tavi.ai

import com.example.tavi.state.TaviState

class SelfHealEngine {

    fun buildPrompt(state: TaviState, health: ModuleHealth): String {
        val issues = buildList {
            if (health.sensor == ModuleStatus.FAILED) add("accelerometer sensor unavailable")
            if (health.sensor == ModuleStatus.DEGRADED) add("accelerometer reading unreliable")
            if (health.localAI == ModuleStatus.FAILED) add("local Gemma model not loaded — no model file path set")
            if (health.localAI == ModuleStatus.DEGRADED) add("local Gemma initializing slowly")
            if (health.cloudAI == ModuleStatus.FAILED) add("Gemini API unreachable — check network or API key")
            if (health.garden == ModuleStatus.FAILED) add("app garden sync failed — database may be locked")
            if (health.garden == ModuleStatus.DEGRADED) add("app garden running with stale data")
        }
        val stateContext = when (state) {
            is TaviState.Blocked -> "blocked: ${state.reason}"
            is TaviState.Failed -> "failed: ${state.reason}. Suggested recovery: ${state.recoveryAction}"
            TaviState.Fallback -> "running on rule-based fallback — no AI model available"
            else -> "current state: ${state.javaClass.simpleName.lowercase()}"
        }
        return buildString {
            append("TAVI self-diagnostic. $stateContext.")
            if (issues.isNotEmpty()) {
                append(" Detected: ${issues.joinToString("; ")}.")
            }
            append(" Provide a short, user-friendly diagnosis and one concrete step to fix the issue.")
            append(" Use action=narrate.")
        }
    }
}
