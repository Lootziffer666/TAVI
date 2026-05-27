package com.example.tavi.shell

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.tavi.capsule.CapsulePanel
import com.example.tavi.capsule.WorkCapsule
import com.example.tavi.clipboard.ClipEntry
import com.example.tavi.clipboard.ClipPanel
import com.example.tavi.garden.GardenNode
import com.example.tavi.quickaction.QuickActionSuggester
import com.example.tavi.quickaction.QuickActionType
import com.example.tavi.snippet.SnippetEntry
import com.example.tavi.snippet.SnippetPanel
import com.example.tavi.sensor.TiltState
import com.example.tavi.state.PendingAction
import com.example.tavi.state.TaviState
import com.example.tavi.ui.components.StateAnchor
import com.example.tavi.workspace.BotInfo
import com.example.tavi.ui.theme.TaviAccent
import com.example.tavi.ui.theme.DepthMid

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SpatialLauncherScreen(
    foreground: List<GardenNode>,
    midground: List<GardenNode>,
    background: List<GardenNode>,
    tilt: TiltState,
    taviState: TaviState,
    isOrbExpanded: Boolean,
    promptText: String,
    isThinking: Boolean,
    aiMessage: String?,
    onOrbToggle: () -> Unit,
    onTextChanged: (String) -> Unit,
    onPromptSubmit: () -> Unit,
    onNodeTap: (GardenNode) -> Unit,
    onNodeLongPress: (GardenNode) -> Unit,
    onWardenOpen: () -> Unit,
    onSelfHeal: () -> Unit,
    recentScopes: List<String> = emptyList(),
    onScopeSelected: (String) -> Unit = {},
    currentScope: String? = null,
    pendingAction: PendingAction? = null,
    onRiskConfirmed: () -> Unit = {},
    onRiskCancelled: () -> Unit = {},
    clipHistory: List<ClipEntry> = emptyList(),
    showClipPanel: Boolean = false,
    bots: List<BotInfo> = emptyList(),
    onClipSelected: (ClipEntry) -> Unit = {},
    onClipHandoff: (botId: String, content: String) -> Unit = { _, _ -> },
    onQuickAction: (ClipEntry, QuickActionType) -> Unit = { _, _ -> },
    snippets: List<SnippetEntry> = emptyList(),
    showSnippetPanel: Boolean = false,
    onSnippetCopy: (SnippetEntry) -> Unit = {},
    onSnippetDelete: (SnippetEntry) -> Unit = {},
    onSnippetFavorite: (SnippetEntry) -> Unit = {},
    capsules: List<WorkCapsule> = emptyList(),
    showCapsulePanel: Boolean = false,
    onCapsuleCopy: (WorkCapsule) -> Unit = {},
    onCapsuleDelete: (WorkCapsule) -> Unit = {},
    onSaveAiAsCapsule: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            // Long-press on canvas background opens Warden — no persistent button needed
            .combinedClickable(onClick = {}, onLongClick = onWardenOpen)
            // Tilt applied here, inside the page — not on the pager item itself
            .graphicsLayer {
                rotationX = tilt.y * 6f
                rotationY = -tilt.x * 6f
                cameraDistance = 12f * density
            }
    ) {
        // Layer 0: Garden canvas (bg + mid)
        GardenCanvas(
            foreground = foreground,
            midground = midground,
            background = background,
            tilt = tilt,
            modifier = Modifier.fillMaxSize().zIndex(0f)
        )

        // Layer 1: State anchor chip (long-press opens Warden when chip is visible)
        StateAnchor(
            state = taviState,
            onLongPress = onWardenOpen,
            onSelfHeal = onSelfHeal,
            modifier = Modifier.align(Alignment.TopCenter).zIndex(3f)
        )

        // Layer 2: AI response banner (long-press save to capsule)
        AIResponseBanner(
            message = aiMessage,
            onSaveAsCapsule = if (aiMessage != null) onSaveAiAsCapsule else null,
            modifier = Modifier.align(Alignment.TopCenter).zIndex(2f).padding(top = 48.dp)
        )

        // Layer 3: Focus zone (center)
        FocusZone(
            nodes = foreground,
            onNodeTap = onNodeTap,
            onNodeLongPress = onNodeLongPress,
            modifier = Modifier.align(Alignment.Center).zIndex(2f)
        )

        // Layer 4a: Clipboard panel — slides up above orb when showClipPanel
        ClipPanel(
            clips = clipHistory,
            bots = bots,
            visible = showClipPanel,
            onClipSelected = onClipSelected,
            onHandoff = onClipHandoff,
            onQuickAction = onQuickAction,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(4f)
                .padding(bottom = 88.dp)
        )

        // Layer 4c: Snippet panel
        SnippetPanel(
            snippets = snippets,
            visible = showSnippetPanel,
            onSnippetCopy = onSnippetCopy,
            onSnippetDelete = onSnippetDelete,
            onSnippetFavorite = onSnippetFavorite,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(4f)
                .padding(bottom = 88.dp)
        )

        // Layer 4d: Capsule panel
        CapsulePanel(
            capsules = capsules,
            visible = showCapsulePanel,
            onCapsuleCopy = onCapsuleCopy,
            onCapsuleDelete = onCapsuleDelete,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(4f)
                .padding(bottom = 88.dp)
        )

        // Layer 4b: Scope chip strip — visible when scopes exist and orb is collapsed
        if (recentScopes.isNotEmpty() && !isOrbExpanded) {
            LazyRow(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .zIndex(4f)
                    .padding(bottom = 96.dp, start = 8.dp, end = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(recentScopes) { scope ->
                    FilterChip(
                        selected = scope == currentScope,
                        onClick = { onScopeSelected(scope) },
                        label = { Text(scope) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TaviAccent.copy(alpha = 0.2f),
                            containerColor = DepthMid
                        )
                    )
                }
            }
        }

        // Layer 5: Risk preflight — shown over everything when a dangerous action is pending
        if (pendingAction != null && taviState is TaviState.RiskDetected) {
            ActionPreflightCard(
                action = pendingAction,
                onConfirm = onRiskConfirmed,
                onCancel = onRiskCancelled,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .zIndex(6f)
                    .padding(bottom = 8.dp)
            )
        }

        // Layer 6: Prompt orb (bottom center)
        PromptOrb(
            isExpanded = isOrbExpanded,
            promptText = promptText,
            isThinking = isThinking,
            onToggle = onOrbToggle,
            onTextChanged = onTextChanged,
            onSubmit = onPromptSubmit,
            modifier = Modifier.align(Alignment.BottomCenter).zIndex(7f)
        )
    }
}
