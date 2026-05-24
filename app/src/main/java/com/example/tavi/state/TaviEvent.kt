package com.example.tavi.state

sealed class TaviEvent {
    object OrbExpanded : TaviEvent()
    object OrbCollapsed : TaviEvent()
    data class TextChanged(val text: String) : TaviEvent()
    object PromptSubmitted : TaviEvent()
    object AIResponseReceived : TaviEvent()
    data class AIActionReceived(val action: String) : TaviEvent()
    data class RiskCommand(val command: String) : TaviEvent()
    object UserConfirmed : TaviEvent()
    object UserCancelled : TaviEvent()
    object ExecutionSuccess : TaviEvent()
    data class ExecutionFailed(val reason: String, val recovery: String) : TaviEvent()
    object WardenPrivateModeOn : TaviEvent()
    object WardenPrivateModeOff : TaviEvent()
    object AIEngineUnavailable : TaviEvent()
    object AIEngineRestored : TaviEvent()
    data class BlockedOccurred(val reason: String) : TaviEvent()
}
