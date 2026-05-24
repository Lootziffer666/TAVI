package com.example.tavi.state

sealed class TaviState {
    object Idle : TaviState()
    object Ready : TaviState()
    object Capture : TaviState()
    object IntentUnclear : TaviState()
    object RiskDetected : TaviState()
    object ActNow : TaviState()
    data class Blocked(val reason: String) : TaviState()
    data class Failed(val reason: String, val recoveryAction: String) : TaviState()
    object Private : TaviState()
    object Fallback : TaviState()
}
