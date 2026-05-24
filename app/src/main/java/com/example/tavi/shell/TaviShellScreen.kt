package com.example.tavi.shell

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.tavi.fossil.FossilDeckScreen
import com.example.tavi.garden.GardenEngine
import com.example.tavi.gesture.TaviGestureRouter
import com.example.tavi.gesture.GestureIntent
import com.example.tavi.state.TaviState
import com.example.tavi.viewmodel.TaviUiState
import com.example.tavi.viewmodel.TaviViewModel
import com.example.tavi.workspace.BotWorkspaceScreen
import kotlinx.coroutines.launch

@Composable
fun TaviShellScreen(
    uiState: TaviUiState,
    viewModel: TaviViewModel
) {
    val pageCount = 2 + uiState.bots.size
    val pagerState = rememberPagerState(initialPage = 1) { pageCount }
    val scope = rememberCoroutineScope()
    val gestureRouter = remember { TaviGestureRouter() }
    val config = LocalConfiguration.current
    val density = LocalDensity.current
    val screenW = with(density) { config.screenWidthDp.dp.toPx().toInt() }
    val screenH = with(density) { config.screenHeightDp.dp.toPx().toInt() }

    // Navigate to target page from ViewModel (e.g. bot navigation via IntentRouter)
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
                detectDragGestures(
                    onDragStart = { offset -> gestureRouter.onDragStart(offset) },
                    onDrag = { _, delta -> gestureRouter.onDrag(delta) },
                    onDragEnd = {
                        val intent = gestureRouter.onDragEnd(
                            IntSize(screenW, screenH),
                            pagerState.currentPage
                        )
                        scope.launch {
                            when (intent) {
                                is GestureIntent.ExpandOrb -> viewModel.onOrbToggled()
                                is GestureIntent.NavigatePage -> {
                                    val target = (pagerState.currentPage + intent.delta)
                                        .coerceIn(0, pageCount - 1)
                                    pagerState.animateScrollToPage(target)
                                }
                                is GestureIntent.OpenFossilDeck -> pagerState.animateScrollToPage(0)
                                is GestureIntent.OpenBotWorkspaces -> {
                                    if (uiState.bots.isNotEmpty()) pagerState.animateScrollToPage(2)
                                }
                                GestureIntent.CollapseOrb, GestureIntent.Passthrough -> Unit
                            }
                        }
                    }
                )
            }
    ) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            when {
                page == 0 -> FossilDeckScreen(
                    candidates = uiState.background + uiState.midground,
                    onKeep = { viewModel.onNodeTap(it) },
                    onRemove = { node ->
                        // gardenEngine marks fossil + system uninstall dialog shown by Android
                        val intent = android.content.Intent(
                            android.content.Intent.ACTION_DELETE,
                            android.net.Uri.parse("package:${node.packageName}")
                        ).apply { flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK }
                        // Note: startActivity called here via context in real impl
                    }
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
                    onNodeLongPress = viewModel::onNodeLongPress
                )
                else -> BotWorkspaceScreen(bot = uiState.bots[page - 2])
            }
        }
    }
}
