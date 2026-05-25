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
import com.example.tavi.receiver.GardenTendWorker
import androidx.work.*
import java.util.concurrent.TimeUnit
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
    val fossilCandidates: List<GardenNode> = emptyList(),
    val targetPage: Int? = null,
    val pendingShellCommand: String? = null,
    val showWarden: Boolean = false,
    val botWorkspacesEnabled: Boolean = true,
    val moduleHealth: ModuleHealth = ModuleHealth(),
    val isSessionOnlyMode: Boolean = false
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
    private val selfHealEngine = SelfHealEngine()
    private val taviAI = TaviAIEngine(
        context = app,
        localEngine = localAI,
        geminiService = RetrofitClient.geminiService,
        warden = warden,
        geminiApiKey = BuildConfig.GEMINI_API_KEY.takeIf { it.isNotBlank() && it != "placeholder" } ?: ""
    )

    private val _state = MutableStateFlow(TaviUiState())
    val state: StateFlow<TaviUiState> = _state.asStateFlow()

    // Session-scope anchor overrides — not persisted to DB when sessionOnlyMode is on
    private val _sessionAnchorOverrides = MutableStateFlow<Map<String, Boolean>>(emptyMap())

    private var stateGrammar: TaviState = TaviState.Idle

    init {
        viewModelScope.launch { appScanner.syncInstalledApps() }
        scheduleGardenTending(app)
        collectSensorData()
        collectGardenData()
        collectPreferences()
        collectBots()
        collectBotWorkspacesEnabled()
        collectWardenPrivateMode()
        collectSessionOnlyMode()
        initAIEngine()
    }

    private fun scheduleGardenTending(app: Application) {
        val work = PeriodicWorkRequestBuilder<GardenTendWorker>(24, TimeUnit.HOURS)
            .setConstraints(Constraints.Builder().setRequiresBatteryNotLow(true).build())
            .build()
        WorkManager.getInstance(app)
            .enqueueUniquePeriodicWork("gardenTend", ExistingPeriodicWorkPolicy.KEEP, work)
    }

    private fun collectSensorData() = viewModelScope.launch {
        runCatching {
            sensorManager.tiltFlow.collect { tilt ->
                _state.update { it.copy(tilt = tilt) }
            }
        }.onFailure {
            _state.update { it.copy(moduleHealth = it.moduleHealth.copy(sensor = ModuleStatus.FAILED)) }
        }
    }

    private fun collectGardenData() {
        viewModelScope.launch {
            runCatching {
                combine(
                    combine(
                        gardenRepo.foregroundNodes(),
                        gardenRepo.midgroundNodes(),
                        gardenRepo.backgroundNodes(),
                        gardenRepo.fossilCandidates()
                    ) { fg, mid, bg, fossils -> listOf(fg, mid, bg, fossils) },
                    _sessionAnchorOverrides
                ) { layers, overrides ->
                    fun List<GardenNode>.applyOverrides() = if (overrides.isEmpty()) this
                    else map { node -> overrides[node.packageName]?.let { node.copy(isSpatiallyAnchored = it) } ?: node }
                    listOf(
                        layers[0].applyOverrides(),
                        layers[1].applyOverrides(),
                        layers[2].applyOverrides(),
                        layers[3]  // fossils don't need anchor override
                    )
                }.collect { layers ->
                    _state.update {
                        it.copy(
                            foreground = layers[0],
                            midground = layers[1],
                            background = layers[2],
                            fossilCandidates = layers[3],
                            moduleHealth = it.moduleHealth.copy(garden = ModuleStatus.OK)
                        )
                    }
                }
            }.onFailure {
                _state.update { it.copy(moduleHealth = it.moduleHealth.copy(garden = ModuleStatus.FAILED)) }
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

    private fun collectBotWorkspacesEnabled() = viewModelScope.launch {
        warden.isBotWorkspacesEnabled.collect { enabled ->
            _state.update { it.copy(botWorkspacesEnabled = enabled) }
        }
    }

    private fun collectWardenPrivateMode() = viewModelScope.launch {
        warden.isPrivateMode.collect { isPrivate ->
            emitEvent(if (isPrivate) TaviEvent.WardenPrivateModeOn else TaviEvent.WardenPrivateModeOff)
        }
    }

    private fun collectSessionOnlyMode() = viewModelScope.launch {
        warden.isSessionOnlyMode.collect { sessionOnly ->
            _state.update { it.copy(isSessionOnlyMode = sessionOnly) }
            // Clear session overrides when mode is turned off (return to DB-persisted state)
            if (!sessionOnly) _sessionAnchorOverrides.value = emptyMap()
        }
    }

    private fun initAIEngine() = viewModelScope.launch {
        prefs.aiModelPath.firstOrNull()?.let { path ->
            if (path.isNotBlank()) {
                val result = localAI.initialize(path)
                if (result.isSuccess && localAI.isReady()) {
                    _state.update { it.copy(moduleHealth = it.moduleHealth.copy(localAI = ModuleStatus.OK)) }
                } else {
                    _state.update { it.copy(moduleHealth = it.moduleHealth.copy(localAI = ModuleStatus.FAILED)) }
                    emitEvent(TaviEvent.AIEngineUnavailable)
                }
            } else {
                _state.update { it.copy(moduleHealth = it.moduleHealth.copy(localAI = ModuleStatus.DEGRADED)) }
                emitEvent(TaviEvent.AIEngineUnavailable)
            }
        } ?: run {
            _state.update { it.copy(moduleHealth = it.moduleHealth.copy(localAI = ModuleStatus.DEGRADED)) }
            emitEvent(TaviEvent.AIEngineUnavailable)
        }
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
                    val botIdx = _state.value.bots.indexOfFirst { it.id == result.botName }
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
                    _state.update {
                        it.copy(
                            isThinking = false, isOrbExpanded = false, promptText = "",
                            aiMessage = "Layout update: ${result.prompt}"
                        )
                    }
                    emitEvent(TaviEvent.AIActionReceived("build_layout"))
                }
            }
        }
    }

    private suspend fun handleAIQuery(query: String) {
        try {
            val ctx = contextAnalyzer.buildContextString(
                _state.value.foreground, _state.value.midground, _state.value.currentScope
            )
            val buffer = StringBuilder()
            taviAI.generate(query, ctx).collect { token -> buffer.append(token) }
            val response = actionsRouter.parseAndRoute(buffer.toString())
            if (!_state.value.isSessionOnlyMode) actionsRouter.execute(response)

            // Persist scope tag when AI creates one (respects session-only mode)
            if (response.action == AIActions.CREATE_SCOPE && !response.target.isNullOrBlank()) {
                if (!_state.value.isSessionOnlyMode) prefs.setScopeTag(response.target)
                _state.update { it.copy(currentScope = response.target) }
            }

            // AI-generated shell commands go through the same risk gate as manual ! commands
            if (response.action == AIActions.EXECUTE_SHELL && !response.target.isNullOrBlank()) {
                handleShellCommand(response.target)
                return
            }

            _state.update {
                it.copy(
                    isThinking = false, isOrbExpanded = false, promptText = "",
                    aiMessage = response.message
                )
            }
            if (response.action == AIActions.NARRATE) emitEvent(TaviEvent.AIResponseReceived)
            else emitEvent(TaviEvent.AIActionReceived(response.action))
        } catch (e: Exception) {
            _state.update {
                it.copy(
                    isThinking = false, isOrbExpanded = false, promptText = "",
                    moduleHealth = it.moduleHealth.copy(cloudAI = ModuleStatus.DEGRADED)
                )
            }
            emitEvent(TaviEvent.BlockedOccurred("AI request failed"))
        }
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
                onSuccess = { output ->
                    _state.update { it.copy(aiMessage = output.ifBlank { "Done." }) }
                    emitEvent(TaviEvent.ExecutionSuccess)
                },
                onFailure = {
                    emitEvent(TaviEvent.ExecutionFailed(it.message ?: "Unknown", "Try again or check Shizuku"))
                }
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
        val newAnchorState = !node.isSpatiallyAnchored
        if (_state.value.isSessionOnlyMode) {
            // Session-only: update in-memory overrides without touching the DB
            _sessionAnchorOverrides.update { it + (node.packageName to newAnchorState) }
        } else {
            gardenEngine.toggleAnchor(node.packageName, newAnchorState)
        }
    }

    fun onFossilRemove(node: GardenNode) = viewModelScope.launch {
        gardenEngine.markAsFossil(node.packageName)
    }

    fun clearTargetPage() = _state.update { it.copy(targetPage = null) }

    fun onWardenToggle() = _state.update { it.copy(showWarden = !it.showWarden) }

    fun onSelfHealRequested() {
        val currentState = _state.value
        if (currentState.isThinking) return
        val prompt = selfHealEngine.buildPrompt(stateGrammar, currentState.moduleHealth)
        _state.update { it.copy(isThinking = true) }
        viewModelScope.launch {
            try {
                val ctx = contextAnalyzer.buildContextString(
                    currentState.foreground, currentState.midground, currentState.currentScope
                )
                val buffer = StringBuilder()
                taviAI.generate(prompt, ctx).collect { token -> buffer.append(token) }
                val response = actionsRouter.parseAndRoute(buffer.toString())
                _state.update {
                    it.copy(isThinking = false, aiMessage = response.message)
                }
                emitEvent(TaviEvent.AIResponseReceived)
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isThinking = false,
                        aiMessage = "Self-heal unavailable. Check: ${currentState.moduleHealth.degradedSummary.ifBlank { "all modules appear OK" }}"
                    )
                }
            }
        }
    }

    private fun emitEvent(event: TaviEvent) {
        stateGrammar = TaviStateReducer.reduce(stateGrammar, event)
        _state.update { it.copy(taviState = stateGrammar) }
    }

    override fun onCleared() {
        localAI.shutdown()
        super.onCleared()
    }
}
