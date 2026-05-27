package com.example.tavi.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tavi.ai.*
import com.example.tavi.capsule.CapsuleRepository
import com.example.tavi.capsule.CapsuleSource
import com.example.tavi.capsule.WorkCapsule
import com.example.tavi.clipboard.ClipEntry
import com.example.tavi.clipboard.ClipboardRepository
import com.example.tavi.cloud.AppCategorizer
import com.example.tavi.cloud.GeminiShellExecutor
import com.example.tavi.cloud.RetrofitClient
import com.example.tavi.data.TaviDatabase
import com.example.tavi.data.TaviPreferences
import com.example.tavi.garden.*
import com.example.tavi.sensor.SpatialSensorManager
import com.example.tavi.sensor.TiltState
import com.example.tavi.shizuku.ShizukuManager
import com.example.tavi.state.PendingAction
import com.example.tavi.state.TaviEvent
import com.example.tavi.state.TaviState
import com.example.tavi.state.TaviStateReducer
import com.example.tavi.intent.IntentClarifierEngine
import com.example.tavi.intent.IntentSuggestion
import com.example.tavi.quickaction.QuickActionSuggester
import com.example.tavi.quickaction.QuickActionType
import com.example.tavi.snippet.SnippetEntry
import com.example.tavi.snippet.SnippetRepository
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
    val pendingAction: PendingAction? = null,
    val showWarden: Boolean = false,
    val botWorkspacesEnabled: Boolean = true,
    val moduleHealth: ModuleHealth = ModuleHealth(),
    val isSessionOnlyMode: Boolean = false,
    val categoryCache: Map<String, String> = emptyMap(),
    val recentScopes: List<String> = emptyList(),
    val clipHistory: List<ClipEntry> = emptyList(),
    val showClipPanel: Boolean = false,
    val snippets: List<SnippetEntry> = emptyList(),
    val showSnippetPanel: Boolean = false,
    val capsules: List<WorkCapsule> = emptyList(),
    val showCapsulePanel: Boolean = false,
    val pendingLaunchNode: com.example.tavi.garden.GardenNode? = null,
    val intentSuggestions: List<com.example.tavi.intent.IntentSuggestion> = emptyList(),
    val showIntentClarifier: Boolean = false
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
    private val clipboardRepo = ClipboardRepository(app, prefs)
    private val snippetRepo = SnippetRepository(prefs)
    private val capsuleRepo = CapsuleRepository(prefs)

    private val geminiApiKey = BuildConfig.GEMINI_API_KEY
        .takeIf { it.isNotBlank() && it != "placeholder" } ?: ""

    private val taviAI = TaviAIEngine(
        context = app,
        localEngine = localAI,
        geminiService = RetrofitClient.geminiService,
        warden = warden,
        geminiApiKey = geminiApiKey
    )

    // Optional cloud modules — only active when an API key is configured
    private val shellExecutor: GeminiShellExecutor? =
        if (geminiApiKey.isNotBlank()) GeminiShellExecutor(RetrofitClient.geminiService, geminiApiKey) else null
    private val appCategorizer: AppCategorizer? =
        if (geminiApiKey.isNotBlank()) AppCategorizer(RetrofitClient.geminiService, geminiApiKey) else null

    private val _state = MutableStateFlow(TaviUiState())
    val state: StateFlow<TaviUiState> = _state.asStateFlow()

    // Session-scope anchor overrides — not persisted to DB when sessionOnlyMode is on
    private val _sessionAnchorOverrides = MutableStateFlow<Map<String, Boolean>>(emptyMap())

    // Reactive focus limit — drives foreground query, updated by prefs and SIMPLIFY/EXPAND AI actions
    private val _maxFocusItems = MutableStateFlow(5)

    private var stateGrammar: TaviState = TaviState.Idle
    private var categoriesLoaded = false

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
        collectClipHistory()
        collectSnippets()
        collectCapsules()
        initAIEngine()
    }

    private fun scheduleGardenTending(app: Application) {
        val work = PeriodicWorkRequestBuilder<GardenTendWorker>(24, TimeUnit.HOURS)
            .setConstraints(Constraints.Builder().setRequiresBatteryNotLow(true).build())
            .setBackoffCriteria(BackoffPolicy.LINEAR, 15, TimeUnit.MINUTES)
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
                        // flatMapLatest re-subscribes to foregroundNodes whenever _maxFocusItems changes
                        _maxFocusItems.flatMapLatest { limit -> gardenRepo.foregroundNodes(limit) },
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
                        layers[3]
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
                    // Lazy one-time category load when fossil candidates first appear
                    if (!categoriesLoaded && layers[3].isNotEmpty()) {
                        categoriesLoaded = true
                        viewModelScope.launch { loadCategories(layers[3]) }
                    }
                }
            }.onFailure {
                _state.update { it.copy(moduleHealth = it.moduleHealth.copy(garden = ModuleStatus.FAILED)) }
            }
        }
    }

    private suspend fun loadCategories(nodes: List<GardenNode>) {
        appCategorizer ?: return
        val cache = appCategorizer.categorize(nodes.map { it.packageName })
        if (cache.isNotEmpty()) _state.update { it.copy(categoryCache = cache) }
    }

    private fun collectPreferences() = viewModelScope.launch {
        launch {
            prefs.currentScopeTag.collect { scope ->
                _state.update { it.copy(currentScope = scope) }
            }
        }
        launch {
            // Keep _maxFocusItems in sync with persisted pref on startup and after restarts
            prefs.maxFocusItems.collect { limit -> _maxFocusItems.value = limit }
        }
        launch {
            prefs.recentScopes.collect { scopes ->
                _state.update { it.copy(recentScopes = scopes) }
            }
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
            if (!sessionOnly) _sessionAnchorOverrides.value = emptyMap()
        }
    }

    private fun collectClipHistory() = viewModelScope.launch {
        clipboardRepo.history.collect { clips ->
            _state.update { it.copy(clipHistory = clips) }
        }
    }

    private fun collectSnippets() = viewModelScope.launch {
        snippetRepo.snippets.collect { list ->
            _state.update { it.copy(snippets = list) }
        }
    }

    private fun collectCapsules() = viewModelScope.launch {
        capsuleRepo.capsules.collect { list ->
            _state.update { it.copy(capsules = list) }
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
                    // Route through AI with layout-adaptation framing so the response is actionable
                    handleAIQuery("Adjust the launcher layout: ${result.prompt}")
                }
                is IntentRouterResult.HandoffToBot -> handleHandoff(result.botId, result.content)
                IntentRouterResult.ShowClipboard -> {
                    _state.update { it.copy(isThinking = false, isOrbExpanded = false, promptText = "",
                        showClipPanel = true, showSnippetPanel = false, showCapsulePanel = false) }
                }
                IntentRouterResult.ShowSnippets -> {
                    _state.update { it.copy(isThinking = false, isOrbExpanded = false, promptText = "",
                        showSnippetPanel = true, showClipPanel = false, showCapsulePanel = false) }
                }
                is IntentRouterResult.SaveSnippet -> handleSaveSnippet(result.title)
                IntentRouterResult.ShowCapsules -> {
                    _state.update { it.copy(isThinking = false, isOrbExpanded = false, promptText = "",
                        showCapsulePanel = true, showClipPanel = false, showSnippetPanel = false) }
                }
                is IntentRouterResult.SaveCapsule -> handleSaveCapsule(result.title, CapsuleSource.CLIPBOARD)
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

            // DEMOTE_APP and PROMOTE_APP go through the risk preflight — skip actionsRouter.execute() for them
            val routeThroughPreflight = response.action == AIActions.DEMOTE_APP || response.action == AIActions.PROMOTE_APP
            if (!_state.value.isSessionOnlyMode && !routeThroughPreflight) actionsRouter.execute(response)

            if (routeThroughPreflight && !response.target.isNullOrBlank()) {
                val packageName = response.target
                val allNodes = _state.value.foreground + _state.value.midground + _state.value.background
                val label = allNodes.firstOrNull { it.packageName == packageName }?.label ?: packageName
                val pending = if (response.action == AIActions.DEMOTE_APP)
                    PendingAction.DemoteApp(packageName, label)
                else
                    PendingAction.PromoteApp(packageName, label)
                _state.update { it.copy(isThinking = false, isOrbExpanded = false, promptText = "", pendingAction = pending) }
                emitEvent(TaviEvent.RiskCommand("${response.action}: $label"))
                return
            }

            // Persist scope tag when AI creates one (respects session-only mode)
            if (response.action == AIActions.CREATE_SCOPE && !response.target.isNullOrBlank()) {
                if (!_state.value.isSessionOnlyMode) {
                    prefs.setScopeTag(response.target)
                    prefs.addRecentScope(response.target)
                }
                _state.update { it.copy(currentScope = response.target) }
            }

            // Live focus limit adaptation (respects session-only mode)
            when (response.action) {
                AIActions.SIMPLIFY_VIEW -> {
                    if (!_state.value.isSessionOnlyMode) prefs.setMaxFocusItems(3)
                    _maxFocusItems.value = 3
                }
                AIActions.EXPAND_VIEW -> {
                    if (!_state.value.isSessionOnlyMode) prefs.setMaxFocusItems(5)
                    _maxFocusItems.value = 5
                }
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
        val cloudEnabled = warden.isCloudAiEnabled.firstOrNull() ?: false
        val translated: String?
        val executable: String
        if (cloudEnabled && shellExecutor != null && looksLikeNaturalLanguage(command)) {
            val result = shellExecutor.buildCommand(command)
            translated = result.getOrNull()
            executable = translated ?: command
        } else {
            translated = null
            executable = command
        }
        val pendingAction = PendingAction.ShellCommand(display = command, translated = translated, executable = executable)
        _state.update { it.copy(isThinking = false, pendingAction = pendingAction) }
        emitEvent(TaviEvent.RiskCommand(executable))
    }

    private suspend fun handleHandoff(botId: String, content: String) {
        if (!_state.value.botWorkspacesEnabled) {
            emitEvent(TaviEvent.BlockedOccurred("Bot workspaces are off"))
            _state.update { it.copy(isThinking = false) }
            return
        }
        val botIdx = _state.value.bots.indexOfFirst { it.id == botId }
        if (botIdx < 0) {
            emitEvent(TaviEvent.BlockedOccurred("Unknown bot: $botId"))
            _state.update { it.copy(isThinking = false) }
            return
        }
        if (content.isNotBlank()) {
            val cm = getApplication<Application>()
                .getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            cm.setPrimaryClip(android.content.ClipData.newPlainText("tavi-handoff", content))
            val entry = ClipEntry(content, com.example.tavi.clipboard.ClipType.TEXT)
            clipboardRepo.addToHistory(entry, persist = !_state.value.isSessionOnlyMode)
        }
        _state.update {
            it.copy(targetPage = 2 + botIdx, isThinking = false, isOrbExpanded = false, promptText = "")
        }
        emitEvent(TaviEvent.AIActionReceived("handoff_bot"))
    }

    fun onRiskConfirmed() {
        val action = _state.value.pendingAction ?: return
        emitEvent(TaviEvent.UserConfirmed)
        viewModelScope.launch {
            when (action) {
                is PendingAction.ShellCommand -> {
                    ShizukuManager.executeCommand(action.executable).fold(
                        onSuccess = { output ->
                            _state.update { it.copy(aiMessage = output.ifBlank { "Done." }) }
                            emitEvent(TaviEvent.ExecutionSuccess)
                        },
                        onFailure = {
                            emitEvent(TaviEvent.ExecutionFailed(it.message ?: "Unknown", "Try again or check Shizuku"))
                        }
                    )
                }
                is PendingAction.DemoteApp -> {
                    gardenEngine.markAsFossil(action.packageName)
                    emitEvent(TaviEvent.ExecutionSuccess)
                }
                is PendingAction.PromoteApp -> {
                    gardenEngine.recordLaunch(action.packageName)
                    emitEvent(TaviEvent.ExecutionSuccess)
                }
                is PendingAction.ScopeChange -> {
                    onScopeSelected(action.to)
                    emitEvent(TaviEvent.ExecutionSuccess)
                }
            }
            _state.update { it.copy(pendingAction = null) }
        }
    }

    fun onRiskCancelled() {
        _state.update { it.copy(pendingAction = null) }
        emitEvent(TaviEvent.UserCancelled)
    }

    fun onNodeTap(node: GardenNode) = viewModelScope.launch {
        val suggestions = IntentClarifierEngine.suggest(node.packageName)
        if (suggestions.isEmpty()) {
            launchNode(node)
        } else {
            _state.update {
                it.copy(
                    pendingLaunchNode = node,
                    intentSuggestions = suggestions,
                    showIntentClarifier = true,
                    taviState = TaviState.Capture
                )
            }
        }
    }

    fun onIntentSelected(suggestion: IntentSuggestion) = viewModelScope.launch {
        val node = _state.value.pendingLaunchNode ?: return@launch
        _state.update {
            it.copy(
                showIntentClarifier = false,
                pendingLaunchNode = null,
                intentSuggestions = emptyList()
            )
        }
        launchNode(node)
    }

    fun onIntentClarifierDismiss() = viewModelScope.launch {
        val node = _state.value.pendingLaunchNode ?: return@launch
        _state.update {
            it.copy(
                showIntentClarifier = false,
                pendingLaunchNode = null,
                intentSuggestions = emptyList()
            )
        }
        // Failure behavior: in doubt, launch directly — never block the user
        launchNode(node)
    }

    private suspend fun launchNode(node: GardenNode) {
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
            _sessionAnchorOverrides.update { it + (node.packageName to newAnchorState) }
        } else {
            gardenEngine.toggleAnchor(node.packageName, newAnchorState)
        }
    }

    fun onFossilRemove(node: GardenNode) = viewModelScope.launch {
        gardenEngine.markAsFossil(node.packageName)
    }

    fun onClipSelected(entry: ClipEntry) {
        val cm = getApplication<Application>()
            .getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        cm.setPrimaryClip(android.content.ClipData.newPlainText("tavi-clip", entry.content))
        _state.update { it.copy(promptText = entry.content, showClipPanel = false, isOrbExpanded = true) }
    }

    fun onClipDismiss() = _state.update { it.copy(showClipPanel = false) }

    fun onSnippetDismiss() = _state.update { it.copy(showSnippetPanel = false) }

    fun onSnippetCopy(entry: SnippetEntry) {
        val cm = getApplication<Application>()
            .getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        cm.setPrimaryClip(android.content.ClipData.newPlainText("tavi-snippet", entry.content))
        _state.update { it.copy(showSnippetPanel = false) }
    }

    fun onSnippetDelete(entry: SnippetEntry) = viewModelScope.launch {
        snippetRepo.delete(entry.id)
    }

    fun onSnippetFavorite(entry: SnippetEntry) = viewModelScope.launch {
        snippetRepo.toggleFavorite(entry.id)
    }

    private suspend fun handleSaveSnippet(title: String) {
        if (title.isBlank()) {
            emitEvent(TaviEvent.BlockedOccurred("Snippet title is empty"))
            _state.update { it.copy(isThinking = false) }
            return
        }
        val cm = getApplication<Application>()
            .getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val content = cm.primaryClip?.getItemAt(0)?.text?.toString()?.trim() ?: ""
        if (content.isBlank()) {
            emitEvent(TaviEvent.BlockedOccurred("Clipboard is empty"))
            _state.update { it.copy(isThinking = false) }
            return
        }
        snippetRepo.add(SnippetEntry(title = title, content = content))
        _state.update { it.copy(isThinking = false, isOrbExpanded = false, promptText = "",
            aiMessage = "Snippet \"$title\" saved.") }
        emitEvent(TaviEvent.AIActionReceived("save_snippet"))
    }

    fun onCapsuleDismiss() = _state.update { it.copy(showCapsulePanel = false) }

    fun onCapsuleCopy(capsule: WorkCapsule) {
        val cm = getApplication<Application>()
            .getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        cm.setPrimaryClip(android.content.ClipData.newPlainText("tavi-capsule", capsule.content))
        _state.update { it.copy(showCapsulePanel = false) }
    }

    fun onCapsuleDelete(capsule: WorkCapsule) = viewModelScope.launch {
        capsuleRepo.delete(capsule.id)
    }

    fun onSaveAiAsCapsule(title: String = "AI response") = viewModelScope.launch {
        val content = _state.value.aiMessage ?: return@launch
        capsuleRepo.add(WorkCapsule(title = title, content = content, source = CapsuleSource.AI_RESPONSE))
        _state.update { it.copy(aiMessage = "Saved as capsule: \"$title\"") }
    }

    private suspend fun handleSaveCapsule(title: String, source: CapsuleSource) {
        if (title.isBlank()) {
            emitEvent(TaviEvent.BlockedOccurred("Capsule title is empty"))
            _state.update { it.copy(isThinking = false) }
            return
        }
        val cm = getApplication<Application>()
            .getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val content = cm.primaryClip?.getItemAt(0)?.text?.toString()?.trim() ?: ""
        if (content.isBlank()) {
            emitEvent(TaviEvent.BlockedOccurred("Clipboard is empty"))
            _state.update { it.copy(isThinking = false) }
            return
        }
        capsuleRepo.add(WorkCapsule(title = title, content = content, source = source))
        _state.update { it.copy(isThinking = false, isOrbExpanded = false, promptText = "",
            aiMessage = "Capsule \"$title\" saved.") }
        emitEvent(TaviEvent.AIActionReceived("save_capsule"))
    }

    fun onQuickAction(entry: ClipEntry, actionType: QuickActionType) = viewModelScope.launch {
        when (actionType) {
            QuickActionType.OPEN_URL -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(entry.content)).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                runCatching { getApplication<Application>().startActivity(intent) }
                _state.update { it.copy(showClipPanel = false) }
            }
            QuickActionType.DIAL -> {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${entry.content}")).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                runCatching { getApplication<Application>().startActivity(intent) }
                _state.update { it.copy(showClipPanel = false) }
            }
            QuickActionType.SAVE_SNIPPET -> {
                val title = entry.content.take(30).replace('\n', ' ')
                snippetRepo.add(SnippetEntry(title = title, content = entry.content))
                _state.update { it.copy(showClipPanel = false, aiMessage = "Saved as snippet.") }
                emitEvent(TaviEvent.AIActionReceived("save_snippet"))
            }
            QuickActionType.SAVE_CAPSULE -> {
                val title = entry.content.take(30).replace('\n', ' ')
                capsuleRepo.add(WorkCapsule(title = title, content = entry.content, source = CapsuleSource.CLIPBOARD))
                _state.update { it.copy(showClipPanel = false, aiMessage = "Saved as capsule.") }
                emitEvent(TaviEvent.AIActionReceived("save_capsule"))
            }
        }
    }

    fun onClipHandoff(botId: String, content: String) = viewModelScope.launch {
        _state.update { it.copy(showClipPanel = false) }
        handleHandoff(botId, content)
    }

    fun onScopeSelected(scope: String) = viewModelScope.launch {
        if (!_state.value.isSessionOnlyMode) {
            prefs.setScopeTag(scope)
            prefs.addRecentScope(scope)
        }
        _state.update { it.copy(currentScope = scope) }
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

    companion object {
        private val SHELL_NATIVE_PREFIXES = listOf(
            "/", "svc ", "pm ", "am ", "settings ", "dumpsys ", "cmd ", "sh ", "su "
        )

        fun looksLikeNaturalLanguage(command: String): Boolean {
            if (!command.contains(" ")) return false
            return SHELL_NATIVE_PREFIXES.none { command.startsWith(it) }
        }
    }
}
