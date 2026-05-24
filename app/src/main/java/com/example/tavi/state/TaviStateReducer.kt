package com.example.tavi.state

object TaviStateReducer {
    fun reduce(state: TaviState, event: TaviEvent): TaviState = when (event) {
        is TaviEvent.OrbExpanded -> if (state is TaviState.Idle || state is TaviState.Fallback) TaviState.Ready else state
        is TaviEvent.OrbCollapsed -> TaviState.Idle
        is TaviEvent.TextChanged -> when {
            event.text.isNotEmpty() && state is TaviState.Ready -> TaviState.Capture
            event.text.isEmpty() && state is TaviState.Capture -> TaviState.Ready
            else -> state
        }
        is TaviEvent.PromptSubmitted -> state
        is TaviEvent.AIResponseReceived -> TaviState.Idle
        is TaviEvent.AIActionReceived -> TaviState.Idle
        is TaviEvent.RiskCommand -> TaviState.RiskDetected
        is TaviEvent.UserConfirmed -> TaviState.ActNow
        is TaviEvent.UserCancelled -> TaviState.Idle
        is TaviEvent.ExecutionSuccess -> TaviState.Idle
        is TaviEvent.ExecutionFailed -> TaviState.Failed(event.reason, event.recovery)
        is TaviEvent.WardenPrivateModeOn -> TaviState.Private
        is TaviEvent.WardenPrivateModeOff -> TaviState.Idle
        is TaviEvent.AIEngineUnavailable -> TaviState.Fallback
        is TaviEvent.AIEngineRestored -> TaviState.Idle
        is TaviEvent.BlockedOccurred -> TaviState.Blocked(event.reason)
    }
}
