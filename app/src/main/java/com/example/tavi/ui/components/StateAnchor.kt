package com.example.tavi.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tavi.state.TaviState
import com.example.tavi.ui.theme.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StateAnchor(
    state: TaviState,
    modifier: Modifier = Modifier,
    onLongPress: (() -> Unit)? = null
) {
    val label = state.publicLabel()
    val color = state.anchorColor()
    // Always show an invisible hit target for long-press even when label is null
    val longPressModifier = if (onLongPress != null) {
        Modifier.combinedClickable(onClick = {}, onLongClick = onLongPress)
    } else Modifier

    AnimatedVisibility(
        visible = label != null,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier.padding(top = 8.dp)
    ) {
        if (label != null) {
            SuggestionChip(
                onClick = {},
                label = { Text(label, color = SpaceBlack) },
                colors = SuggestionChipDefaults.suggestionChipColors(containerColor = color),
                modifier = longPressModifier
            )
        }
    }
}

private fun TaviState.publicLabel(): String? = when (this) {
    TaviState.Idle -> null
    TaviState.Ready -> null
    TaviState.Capture -> null
    TaviState.IntentUnclear -> "Need more detail"
    TaviState.RiskDetected -> "Review action"
    TaviState.ActNow -> "Executing…"
    is TaviState.Blocked -> "Not available: ${this.reason}"
    is TaviState.Failed -> "Could not complete: ${this.reason}"
    TaviState.Private -> "Private room"
    TaviState.Fallback -> "Running on local rules"
}

private fun TaviState.anchorColor(): Color = when (this) {
    TaviState.RiskDetected, TaviState.ActNow -> GlowAmber
    is TaviState.Blocked, is TaviState.Failed -> RiskRed
    TaviState.Private -> PrivatePurple
    TaviState.Fallback -> FallbackGrey
    else -> TaviAccent
}
