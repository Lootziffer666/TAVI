package com.example.tavi.shell

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import com.example.tavi.fossil.FossilDeckScreen
import com.example.tavi.gesture.GestureIntent
import com.example.tavi.gesture.TaviGestureRouter
import com.example.tavi.viewmodel.TaviUiState
import com.example.tavi.viewmodel.TaviViewModel
import com.example.tavi.warden.TaviWarden
import com.example.tavi.warden.WardenScreen
import com.example.tavi.workspace.BotWorkspaceScreen
import kotlinx.coroutines.launch

@Composable
fun TaviShellScreen(
    uiState: TaviUiState,
    viewModel: TaviViewModel,
    warden: TaviWarden
) {
    val pageCount = if (uiState.botWorkspacesEnabled) 2 + uiState.bots.size else 2
    val pagerState = rememberPagerState(initialPage = 1) { pageCount }
    val scope = rememberCoroutineScope()
    val gestureRouter = remember { TaviGestureRouter() }
    val context = LocalContext.current

    LaunchedEffect(uiState.targetPage) {
        uiState.targetPage?.let { page ->
            pagerState.animateScrollToPage(page.coerceIn(0, pageCount - 1))
            viewModel.clearTargetPage()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                // Edge-zone gestures consume events (TAVI owns them).
                // Center gestures are tracked but NOT consumed so HorizontalPager
                // handles natural page-swipe from any horizontal center drag.
                val fraction = com.example.tavi.gesture.EdgeZoneConfig.EDGE_FRACTION
                awaitEachGesture {
                    val w = size.width.toFloat()
                    val h = size.height.toFloat()
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val startPos = down.position
                    val isEdge = startPos.x < w * fraction ||
                                 startPos.x > w * (1 - fraction) ||
                                 startPos.y < h * fraction ||
                                 startPos.y > h * (1 - fraction)
                    gestureRouter.onDragStart(startPos)
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                        val delta = Offset(
                            change.position.x - change.previousPosition.x,
                            change.position.y - change.previousPosition.y
                        )
                        gestureRouter.onDrag(delta)
                        if (isEdge) change.consume()
                        if (!change.pressed) break
                    }
                    val intent = gestureRouter.onDragEnd(size, pagerState.currentPage)
                    scope.launch {
                        when (intent) {
                            is GestureIntent.ExpandOrb -> viewModel.onOrbToggled()
                            is GestureIntent.NavigatePage -> {
                                val target = (pagerState.currentPage + intent.delta)
                                    .coerceIn(0, pageCount - 1)
                                pagerState.animateScrollToPage(target)
                            }
                            GestureIntent.OpenFossilDeck -> pagerState.animateScrollToPage(0)
                            GestureIntent.OpenBotWorkspaces -> {
                                if (uiState.botWorkspacesEnabled && uiState.bots.isNotEmpty())
                                    pagerState.animateScrollToPage(2)
                            }
                            GestureIntent.Passthrough -> Unit
                        }
                    }
                }
            }
    ) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            when {
                page == 0 -> FossilDeckScreen(
                    candidates = uiState.fossilCandidates.sortedBy { it.affinityScore },
                    onKeep = { node -> viewModel.onNodeTap(node) },
                    onRemove = { node ->
                        // Mark fossil in DB first so it doesn't reappear on next garden sync
                        viewModel.onFossilRemove(node)
                        val deleteIntent = Intent(
                            Intent.ACTION_DELETE,
                            Uri.parse("package:${node.packageName}")
                        ).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
                        context.startActivity(deleteIntent)
                    },
                    categoryCache = uiState.categoryCache
                )
                page == 1 -> SpatialLauncherScreen(
                    foreground = uiState.foreground,
                    midground = uiState.midground,
                    background = uiState.background,
                    tilt = uiState.tilt,
                    taviState = uiState.taviState,
                    isOrbExpanded = uiState.isOrbExpanded,
                    promptText = uiState.promptText,
                    isThinking = uiState.isThinking,
                    aiMessage = uiState.aiMessage,
                    onOrbToggle = viewModel::onOrbToggled,
                    onTextChanged = viewModel::onTextChanged,
                    onPromptSubmit = viewModel::onPromptSubmitted,
                    onNodeTap = viewModel::onNodeTap,
                    onNodeLongPress = viewModel::onNodeLongPress,
                    onWardenOpen = viewModel::onWardenToggle,
                    onSelfHeal = viewModel::onSelfHealRequested,
                    recentScopes = uiState.recentScopes,
                    onScopeSelected = viewModel::onScopeSelected,
                    currentScope = uiState.currentScope,
                    pendingAction = uiState.pendingAction,
                    onRiskConfirmed = viewModel::onRiskConfirmed,
                    onRiskCancelled = viewModel::onRiskCancelled,
                    clipHistory = uiState.clipHistory,
                    showClipPanel = uiState.showClipPanel,
                    bots = uiState.bots,
                    onClipSelected = viewModel::onClipSelected,
                    onClipHandoff = viewModel::onClipHandoff,
                    onQuickAction = viewModel::onQuickAction,
                    snippets = uiState.snippets,
                    showSnippetPanel = uiState.showSnippetPanel,
                    onSnippetCopy = viewModel::onSnippetCopy,
                    onSnippetDelete = viewModel::onSnippetDelete,
                    onSnippetFavorite = viewModel::onSnippetFavorite,
                    capsules = uiState.capsules,
                    showCapsulePanel = uiState.showCapsulePanel,
                    onCapsuleCopy = viewModel::onCapsuleCopy,
                    onCapsuleDelete = viewModel::onCapsuleDelete,
                    onSaveAiAsCapsule = viewModel::onSaveAiAsCapsule
                )
                else -> BotWorkspaceScreen(bot = uiState.bots[page - 2])
            }
        }

        // Warden overlay — fullscreen, slides over everything
        if (uiState.showWarden) {
            WardenScreen(
                warden = warden,
                onClose = viewModel::onWardenToggle,
                moduleHealth = uiState.moduleHealth,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
