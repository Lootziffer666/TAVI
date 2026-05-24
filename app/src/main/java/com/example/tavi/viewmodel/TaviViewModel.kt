package com.example.tavi.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tavi.ai.*
import com.example.tavi.cloud.RetrofitClient
import com.example.tavi.data.TaviDatabase
import com.example.tavi.data.TaviPreferences
import com.example.tavi.garden.*
import com.example.tavi.sensor.SpatialSensorManager
import com.example.tavi.sensor.TiltState
import com.example.tavi.shizuku.ShizukuManager
import com.example.tavi.state.TaviEvent
import com.example.tavi.state.TaviState
import com.example.tavi.state.TaviStateReducer
import com.example.tavi.warden.TaviWarden
import com.example.tavi.workspace.BotInfo
import com.example.tavi.workspace.BotRegistry
import com.example.tavi.BuildConfig
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class TaviUiState(
    val taviState: TaviState = TaviState.Idle,
    val foreground: List<GardenNode> = emptyList(),
    val midground: List<GardenNode> = emptyList(),
    val background: List<GardenNode> = emptyList(),
    val tilt: TiltState = TiltState(0f, 0f),
    val isOrbExpanded: Boolean = false,
    val promptText: String = "",
    val isThinking: Boolean = false,
    val aiMessage: String? = null,
    val currentScope: String? = null,
    val bots: List<BotInfo> = BotRegistry.defaults,
    val targetPage: Int? = null,
    val pendingShellCommand: String? = null,
    val showWarden: Boolean = false
)

class TaviViewModel(app: Application) : AndroidViewModel(app) {

    private val db = TaviDatabase.getInstance(app)
    private val dao = db.appNodeDao()
    private val prefs = TaviPreferences(app)
    val warden = TaviWarden(prefs)
    private val gardenRepo = GardenRepository(dao, app)
    private val gardenEngine = GardenEngine(dao)
    private val appScanner = AppScanner(app, dao)
    private val sensorManager = SpatialSensorManager(app)
    private val localAI = LocalAIEngine(app)
    private val contextAnalyzer = ContextAnalyzer()
    private val workspaceRepo = com.example.tavi.workspace.WorkspaceRepository(prefs)
    private val actionsRouter = MobileActionsRouter(app, gardenEngine)
    private val intentRouter = IntentRouter(BotRegistry.names)
    private val taviAI = TaviAIEngine(
        context = app,
        localEngine = localAI,
        geminiService = RetrofitClient.geminiService,
        warden = warden,
        geminiApiKey = BuildConfig.GEMINI_API_KEY.takeIf { it.isNotBlank() && it != "placeholder" } ?: ""
    )

    private val _state = MutableStateFlow(TaviUiState())
    val state: StateFlow<TaviUiState> = _state.asStateFlow()

    private var stateGrammar: TaviState = TaviState.Idle

    init {
        viewModelScope.launch { appScanner.syncInstalledApps() }
        collectSensorData()
        collectGardenData()
        collectPreferences()
        collectBots()
        initAIEngine()
    }

    private fun collectSensorData() = viewModelScope.launch {
        sensorManager.tiltFlow.collect { tilt ->
            _state.update { it.copy(tilt = tilt) }
        }
    }

    private fun collectGardenData() {
        viewModelScope.launch {
            combine(
                gardenRepo.foregroundNodes(),
                gardenRepo.midgroundNodes(),
                gardenRepo.backgroundNodes()
            ) { fg, mid, bg -> Triple(fg, mid, bg) }
                .collect { (fg, mid, bg) ->
                    _state.update { it.copy(foreground = fg, midground = mid, background = bg) }
                }
        }
    }

    private fun collectPreferences() = viewModelScope.launch {
        prefs.currentScopeTag.collect { scope ->
            _state.update { it.copy(currentScope = scope) }
        }
    }

    private fun collectBots() = viewModelScope.launch {
        workspaceRepo.bots.collect { bots ->
            _state.update { it.copy(bots = bots) }
        }
    }

    private fun initAIEngine() = viewModelScope.launch {
        prefs.aiModelPath.firstOrNull()?.let { path ->
            if (path.isNotBlank()) {
                localAI.initialize(path)
                if (!localAI.isReady()) emitEvent(TaviEvent.AIEngineUnavailable)
            } else {
                emitEvent(TaviEvent.AIEngineUnavailable)
            }
        } ?: emitEvent(TaviEvent.AIEngineUnavailable)
    }

    fun onOrbToggled() {
        val expanded = !_state.value.isOrbExpanded
        _state.update { it.copy(isOrbExpanded = expanded) }
        emitEvent(if (expanded) TaviEvent.OrbExpanded else TaviEvent.OrbCollapsed)
    }

    fun onTextChanged(text: String) {
        _state.update { it.copy(promptText = text) }
        emitEvent(TaviEvent.TextChanged(text))
    }

    fun onPromptSubmitted() {
        val input = _state.value.promptText.trim()
        if (input.isBlank()) return
        _state.update { it.copy(isThinking = true) }
        emitEvent(TaviEvent.PromptSubmitted)

        viewModelScope.launch {
            when (val result = intentRouter.route(input)) {
                is IntentRouterResult.QueryAI -> handleAIQuery(result.query)
                is IntentRouterResult.NavigateBot -> {
                    val botIdx = BotRegistry.defaults.indexOfFirst { it.id == result.botName }
                    if (botIdx >= 0) _state.update { it.copy(targetPage = 2 + botIdx) }
                    _state.update { it.copy(isThinking = false, isOrbExpanded = false, promptText = "") }
                    emitEvent(TaviEvent.AIActionReceived("navigate_bot"))
                }
                is IntentRouterResult.ShellCommand -> handleShellCommand(result.command)
                is IntentRouterResult.OpenUrl -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(result.url)).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    getApplication<Application>().startActivity(intent)
                    _state.update { it.copy(isThinking = false, isOrbExpanded = false, promptText = "") }
                    emitEvent(TaviEvent.AIActionReceived("open_url"))
                }
                is IntentRouterResult.OpenSettings -> {
                    val intent = Intent(android.provider.Settings.ACTION_SETTINGS).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    getApplication<Application>().startActivity(intent)
                    _state.update { it.copy(isThinking = false, isOrbExpanded = false, promptText = "") }
                    emitEvent(TaviEvent.AIActionReceived("settings"))
                }
                is IntentRouterResult.BuildLayout -> {
                    _state.update { it.copy(isThinking = false, isOrbExpanded = false, promptText = "",
                        aiMessage = "Layout update: ${result.prompt}") }
                    emitEvent(TaviEvent.AIActionReceived("build_layout"))
                }
            }
        }
    }

    private suspend fun handleAIQuery(query: String) {
        val ctx = contextAnalyzer.buildContextString(
            _state.value.foreground, _state.value.midground, _state.value.currentScope
        )
        val buffer = StringBuilder()
        taviAI.generate(query, ctx).collect { token -> buffer.append(token) }
        val response = actionsRouter.parseAndRoute(buffer.toString())
        actionsRouter.execute(response)
        val message = response.message
        _state.update { it.copy(
            isThinking = false, isOrbExpanded = false, promptText = "",
            aiMessage = message
        )}
        if (response.action == AIActions.NARRATE) emitEvent(TaviEvent.AIResponseReceived)
        else emitEvent(TaviEvent.AIActionReceived(response.action))
    }

    private suspend fun handleShellCommand(command: String) {
        val shizukuEnabled = warden.isShizukuEnabled.firstOrNull() ?: false
        if (!shizukuEnabled) {
            _state.update { it.copy(isThinking = false) }
            emitEvent(TaviEvent.BlockedOccurred("Power adapter not connected"))
            return
        }
        _state.update { it.copy(isThinking = false, pendingShellCommand = command) }
        emitEvent(TaviEvent.RiskCommand(command))
    }

    fun onRiskConfirmed() {
        val cmd = _state.value.pendingShellCommand ?: return
        emitEvent(TaviEvent.UserConfirmed)
        viewModelScope.launch {
            ShizukuManager.executeCommand(cmd).fold(
                onSuccess = { emitEvent(TaviEvent.ExecutionSuccess) },
                onFailure = { emitEvent(TaviEvent.ExecutionFailed(it.message ?: "Unknown", "Try again or check Shizuku")) }
            )
            _state.update { it.copy(pendingShellCommand = null) }
        }
    }

    fun onRiskCancelled() {
        _state.update { it.copy(pendingShellCommand = null) }
        emitEvent(TaviEvent.UserCancelled)
    }

    fun onNodeTap(node: GardenNode) = viewModelScope.launch {
        gardenEngine.recordLaunch(node.packageName)
        val pm = getApplication<Application>().packageManager
        val intent = pm.getLaunchIntentForPackage(node.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        intent?.let { getApplication<Application>().startActivity(it) }
        emitEvent(TaviEvent.ExecutionSuccess)
    }

    fun onNodeLongPress(node: GardenNode) = viewModelScope.launch {
        gardenEngine.toggleAnchor(node.packageName, !node.isSpatiallyAnchored)
    }

    // Called from FossilDeck before the system uninstall dialog is shown.
    // Marks the node as fossil in DB so it doesn't reappear on next garden sync.
    fun onFossilRemove(node: GardenNode) = viewModelScope.launch {
        gardenEngine.markAsFossil(node.packageName)
    }

    fun clearTargetPage() = _state.update { it.copy(targetPage = null) }

    fun onWardenToggle() = _state.update { it.copy(showWarden = !it.showWarden) }

    private fun emitEvent(event: TaviEvent) {
        stateGrammar = TaviStateReducer.reduce(stateGrammar, event)
        _state.update { it.copy(taviState = stateGrammar) }
    }

    override fun onCleared() {
        localAI.shutdown()
        super.onCleared()
    }
}
