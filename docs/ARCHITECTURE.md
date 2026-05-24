# TAVI Architecture

Branch: `claude/intent-zen-integration-wL7tV`
Cherry-pick source: Zen (Claude prototype) + INTENT (Google AI Studio prototype)

---

## Module Map

```
app/src/main/java/com/example/tavi/
│
├── MainActivity.kt
│     HOME-category launcher entry point. Full immersion (enableEdgeToEdge).
│     Back button swallowed (onBackPressedDispatcher no-op).
│     Hosts TaviShellScreen as sole content.
│
├── state/
│     TaviState.kt         — 9 sealed states (Idle/Ready/Capture/IntentUnclear/
│                             RiskDetected/ActNow/Blocked/Failed/Private/Fallback)
│     TaviEvent.kt         — Input events driving state transitions
│     TaviStateReducer.kt  — Pure function: reduce(TaviState, TaviEvent) → TaviState
│
├── viewmodel/
│     TaviViewModel.kt     — AndroidViewModel. Single source of truth (TaviUiState).
│                             Collects: sensor, garden layers, preferences, bots.
│                             Exposes: warden (public) for WardenScreen wiring.
│
├── garden/                  FROM ZEN — unchanged
│     GardenEngine.kt      — Affinity scoring: 0.4×log(freq) + 0.4×e^(-0.1×hours) + 0.2×user
│                             assignLayers(): foreground/midground/background by affinity quartiles
│                             markAsFossil(): sets affinity=0, depthLayer=-2, fossilStatus
│     GardenNode.kt        — Data: packageName, position, depthLayer (0/1/2/-2=fossil),
│                             colorHue, affinityScore, isSpatiallyAnchored, growthStage
│     GrowthStage.kt       — SEED(0,3)→SPROUT(5,5)→PLANT(20,6)→BLOOM(60,8) sides
│     AppScanner.kt        — PackageManager sync on boot and periodic WorkManager task
│     GardenRepository.kt  — Flow<List<GardenNode>> per depth layer via Room DAO
│
├── shell/                   UI composition layer
│     GardenCanvas.kt      — Compose Canvas: perspective voxel grid (vanishing point
│                             shifts with tilt), polygon nodes at 3 depth scales
│     FocusZone.kt         — Max 5 nodes, 8-sec breath ring animation, AsyncImage icons,
│                             long-press = spatial anchor toggle
│     PromptOrb.kt         — Pulsing FAB (2800ms scale), expandable TextField, isThinking spinner
│     AIResponseBanner.kt  — Ephemeral top banner, auto-dismisses after 4 seconds
│     StateAnchor.kt       — Single chip: maps TaviState → public-safe label + color.
│                             Long-press on chip → opens Warden
│     SpatialLauncherScreen.kt  — Z-stack: Canvas(0) + StateAnchor(3) + Banner(2)
│                                  + FocusZone(2) + PromptOrb(4).
│                                  graphicsLayer{ rotationX/Y = tilt * 6f, camera 12f }
│                                  combinedClickable(onLongClick = onWardenOpen) on root Box
│     TaviShellScreen.kt   — HorizontalPager(2+bots pages): FossilDeck|Spatial|BotWorkspaces.
│                             Root pointerInput → TaviGestureRouter.
│                             LaunchedEffect(targetPage) for bot navigation.
│                             WardenScreen overlay when uiState.showWarden = true.
│
├── gesture/                 Cluster 13 — no AccessibilityService
│     GestureIntent.kt     — Sealed: ExpandOrb/CollapseOrb/NavigatePage(delta)/
│                             OpenFossilDeck/OpenBotWorkspaces/Passthrough
│     EdgeZoneConfig.kt    — EDGE_FRACTION=0.12f, velocity threshold=300f, distance=80f
│     TaviGestureRouter.kt — onDragStart/onDrag/onDragEnd → GestureIntent.
│                             5 zones: bottom/top edge→Orb, left→prev page,
│                             right→next page, center-up→FossilDeck, center-down→Bots
│     SwipeEngine.kt       — Modifier.swipeToDecide() extension.
│                             Animatable rotation wired to offsetX/20f.
│                             coroutineScope.launch (fixed from invalid kotlinx.coroutines.launch)
│
├── sensor/
│     SpatialSensorManager.kt  — FROM ZEN. SensorManager.SENSOR_DELAY_GAME.
│                                  Low-pass α=0.08. Output: Flow<TiltState(x,y)> in [-1,1].
│
├── ai/
│     LocalAIEngine.kt     — FROM ZEN. MediaPipe LlmInference via reflection.
│                             initialize(): Class.forName LlmInference, getDeclaredMethod create.
│                             generate(): Proxy.newProxyInstance for LlmInferenceResultListener.
│                             onResult(token, done) → trySend(token) / close().
│                             ruleBasedResponse(): always-available keyword fallback.
│     TaviAIEngine.kt      — Orchestrator: LocalAIEngine → Gemini cloud → rule-based.
│                             Cloud only when isCloudAiEnabled + geminiApiKey non-blank.
│     ContextAnalyzer.kt   — FROM ZEN. Builds context string: time-of-day, weekday/weekend,
│                             top-5 apps with launch counts, scope cluster.
│     MobileActionsRouter.kt — FROM ZEN. Parses JSON response tokens.
│                               Routes: promote_app/demote_app/anchor/narrate → gardenEngine calls.
│     IntentRouter.kt      — Prefix routing:
│                             ?<query> → QueryAI or NavigateBot (if matches bot name)
│                             !<cmd>  → ShellCommand
│                             /build  → BuildLayout
│                             http(s) → OpenUrl
│                             settings→ OpenSettings
│                             else    → QueryAI
│     AIResponse.kt        — data class: action, target, message. AIActions constants.
│
├── cloud/
│     GeminiApiService.kt  — FROM INTENT. Retrofit @POST gemini-1.5-flash:generateContent.
│                             Moshi-annotated request/response models.
│     RetrofitClient.kt    — OkHttp + Moshi + Retrofit singleton.
│     AppCategorizer.kt    — FROM INTENT. Gemini-powered app category grouping.
│     GeminiShellExecutor.kt — NL → shell command via Gemini. Returns Result<String>.
│
├── fossil/                  Cluster 11 — FROM INTENT (rewritten)
│     FossilDeckScreen.kt  — Tinder-style card stack. Card + graphicsLayer(rotationZ, scaleX/Y).
│                             AsyncImage for app icons. stackOffset depth effect (3 cards).
│                             Swipe left → onRemove (markAsFossil + system uninstall dialog).
│                             Swipe right → onKeep (recordLaunch → affinity boost).
│
├── workspace/
│     BotInfo.kt           — data class(id, name, url) + BotRegistry.defaults (8 bots)
│     BotWorkspaceScreen.kt — AndroidView(WebView): JS + domStorage + custom UserAgent
│     WorkspaceRepository.kt — Moshi JSON ↔ DataStore. bots: Flow<List<BotInfo>>.
│                               saveBots/addBot/removeBot/reset.
│
├── warden/                  Cluster 19 — new for TAVI
│     TaviWarden.kt        — DataStore-backed flows: isEmergencyOff, isPrivateMode,
│                             isShizukuEnabled, isCloudAiEnabled, isBotWorkspacesEnabled,
│                             isFullyOperational. enable/disable methods. triggerEmergencyOff().
│     WardenScreen.kt      — Scrollable Column of SwitchListTile-style rows.
│                             Rows: Private room / Shizuku / Cloud AI / Bot workspaces.
│                             Emergency off button (RiskRed outlined).
│                             Access: long-press on canvas background or StateAnchor chip.
│
├── shizuku/
│     ShizukuManager.kt    — FROM INTENT. isReady(), checkPermission(),
│                             requestPermission(), executeCommand() via Shizuku.newProcess().
│
├── receiver/
│     TaviBootReceiver.kt  — RECEIVE_BOOT_COMPLETED → enqueue GardenTendWorker (periodic 24h)
│     GardenTendWorker.kt  — syncInstalledApps + recalculate + assignLayers + persistUpdates
│
├── data/
│     TaviDatabase.kt      — Room singleton. Migration 1→2: adds fossilStatus column.
│     AppNodeEntity.kt     — Room entity: packageName (PK), label, depthLayer,
│                             affinityScore, launchCount, lastLaunched, colorHue,
│                             isSpatiallyAnchored, scopeTag, fossilStatus, animationPhaseOffset
│     AppNodeDao.kt        — getAllNodes, getForeground/Mid/Background/FossilCandidates (Flows),
│                             recordLaunch, updateFossilStatus, upsert, delete
│     TaviPreferences.kt   — DataStore: maxFocusItems, reduceMotion, scopeTag, aiModelPath,
│                             wardenEmergencyOff, privateModeEnabled, shizukuEnabled,
│                             botWorkspacesEnabled, cloudAiEnabled, taviWorkspacesJson
│
└── ui/theme/                FLUBBER design language
      Color.kt             — SpaceBlack=#050810, SpaceNavy=#0D1117, TaviAccent=#00BCD4,
                              BreathBlue=#4FC3F7, BreathTeal=#26C6DA, RiskRed=#EF5350,
                              PrivatePurple=#7E57C2, GlowAmber=#FFB300, FallbackGrey=#78909C
      Type.kt              — Google Fonts downloadable: Lilita One (display) / Barlow (body) /
                              JetBrains Mono (labels). GoogleFont.Provider via GMS fonts.
      Theme.kt             — dark-first darkColorScheme
```

---

## Integration Seams

### Seam 1: Zen Spatial Canvas ↔ INTENT Page Navigation

`TaviShellScreen` uses `HorizontalPager` (from INTENT's backbone) for pages. `SpatialLauncherScreen` + `GardenCanvas` + `FocusZone` live on page 1. Bot pages are pages 2+. Tilt parallax operates within the page via `graphicsLayer` — not on the pager item, which avoids transformation matrix conflicts.

### Seam 2: GardenEngine Affinity ↔ FossilDeck Cleanup

`GardenEngine.assignLayers()` drives `GrowthStage`. Apps at low affinity are surfaced in `FossilDeckScreen`. Swipe-left triggers `gardenEngine.markAsFossil()` (sets affinity=0, depthLayer=-2, fossilStatus="removed_candidate") **before** the system uninstall `Intent.ACTION_DELETE`. Without the markAsFossil call, the node would reappear on next `AppScanner.syncInstalledApps()`.

### Seam 3: LocalAIEngine (primary) ↔ GeminiAPI (fallback)

`TaviAIEngine` chains: LocalAIEngine (streaming tokens via MediaPipe Proxy) → Gemini cloud (when `isCloudAiEnabled` + `geminiApiKey` non-blank) → `ruleBasedResponse()` (always available). `IntentRouter` routes prefixes before AI dispatch. `MobileActionsRouter` parses the response and routes `promote_app`/`demote_app` back into `GardenEngine`.

### Seam 4: Warden ↔ All gated features

`TaviWarden` wraps every risky capability:
- `isShizukuEnabled` → gates `!cmd` shell dispatch in `TaviViewModel.handleShellCommand()`
- `isCloudAiEnabled` → gates Gemini cloud calls in `TaviAIEngine`
- `isPrivateMode` → blocks DataStore writes and cloud calls
- `isEmergencyOff` → triggered by Emergency button, disables all power adapters at once

---

## State Grammar Transitions

```
Idle ──(OrbExpanded)──→ Capture ──(PromptSubmitted)──→ ActNow
                                                           │
                         ←──(AIResponseReceived)──────────┘
                                                           │
                         ←──(AIActionReceived)────────────┘

Idle ──(TextChanged)──→ Ready

Capture ──(BlockedOccurred)──→ Blocked
Capture ──(RiskCommand)──────→ RiskDetected
RiskDetected ──(UserConfirmed)──→ ActNow
RiskDetected ──(UserCancelled)──→ Idle
ActNow ──(ExecutionFailed)──→ Failed
ActNow ──(ExecutionSuccess)──→ Idle
Any ──(AIEngineUnavailable)──→ Fallback
```

---

## Build Constraints

- Min SDK 24, Compile/Target SDK 36
- KSP for Room annotation processing
- `buildConfig = true` in app/build.gradle.kts — exposes `BuildConfig.GEMINI_API_KEY`
- `GEMINI_API_KEY` read from `local.properties`, defaults to empty string if absent
- MediaPipe LlmInference loaded via reflection to avoid compile-time dependency on specific API version
- Google Fonts downloadable API requires GMS (`com.google.android.gms`) installed on device
