# TAVI Gates

| Gate | Description | Status |
|---|---|---|
| TV-001 | Concept Contract Lock | Complete |
| TV-002 | Stack Decision | Complete — Kotlin + Compose + KSP + Gradle 8.x (see STACK.md) |
| TV-003 | Opus Product Planning Map | Complete — CLUSTER_MAP.md merged, 19 clusters defined |
| TV-004 | First Prototype Cluster | Complete — branch `claude/intent-zen-integration-wL7tV` |
| TV-005 | QoL Refinery + Clusters 15 / 1 / 5 | Complete — same branch, 4 commits |

## TV-004 Delivery Summary

Branch: `claude/intent-zen-integration-wL7tV`

Cherry-pick fusion of Zen (spatial model, MediaPipe Gemma, accelerometer parallax) and INTENT (Google AI Studio prototype: Bot Workspaces, Tinder cleanup deck, Shizuku power adapter). 46 Kotlin source files.

### Clusters implemented

| Cluster | Key Files | Status |
|---|---|---|
| 11 — App Fossil Finder | `fossil/FossilDeckScreen.kt`, `garden/GardenEngine.kt` | Complete |
| 12 — Zen Shell / Launcher Rooms | `shell/GardenCanvas.kt`, `shell/FocusZone.kt`, `shell/SpatialLauncherScreen.kt` | Complete |
| 13 — Gesture Edge | `gesture/TaviGestureRouter.kt`, `gesture/SwipeEngine.kt` | Complete |
| 14 — State Grammar / One Anchor | `state/TaviState.kt`, `state/TaviStateReducer.kt`, `ui/components/StateAnchor.kt` | Complete |
| 17 — AI / Tool Handoff | `ai/LocalAIEngine.kt`, `ai/TaviAIEngine.kt`, `ai/IntentRouter.kt` | Complete |
| 19 — Privacy / Control / Warden | `warden/TaviWarden.kt`, `warden/WardenScreen.kt` | Complete |

### Hard constraints satisfied

- No AccessibilityService — gestures work at Compose layer only (HOME launcher full window ownership)
- No AutoInput, no silent background behavior
- Shizuku gated behind TaviWarden.isShizukuEnabled (optional power adapter, not core)
- Forbidden terms absent: Dashboard, Productivity, Wellbeing, Smart, Helper, App Blocker
- FLUBBER design language: Lilita One / Barlow / JetBrains Mono via Google Fonts downloadable API
- Spatial model (3D voxel canvas + accelerometer parallax) preserved from Zen
- State Grammar: exactly 9 states, pure reducer, One Anchor per Moment

### Audit fixes applied

- C1: SwipeEngine.kt — fixed `coroutineScope.kotlinx.coroutines.launch` invalid syntax
- C2: AndroidManifest — fixed receiver class name `.receiver.BootReceiver` → `.receiver.TaviBootReceiver`
- C3: Launcher icon — created `res/drawable/ic_launcher.xml` adaptive vector
- M1: LocalAIEngine.kt — fixed AI streaming (was passing null callback; now uses Proxy pattern)
- M2: Fossil removal — added `viewModel.onFossilRemove()` before system uninstall intent
- M3: FossilDeckScreen — rewritten with Card + graphicsLayer + AsyncImage (INTENT pattern)
- M4: TaviAIEngine — fixed imports, non-nullable geminiService, geminiApiKey parameter
- m1: WardenScreen — implemented + wired into SpatialLauncherScreen via long-press
- m2: Google Fonts — Type.kt uses downloadable API (Lilita One, Barlow, JetBrains Mono)
- m3: GeminiApiService — model ID fixed to `gemini-1.5-flash`
- m4: WorkspaceRepository — implemented with Moshi JSON persistence
- m5: Documentation — README.md updated, GATES.md updated, ARCHITECTURE.md created

---

## TV-005 Delivery Summary

Branch: `claude/intent-zen-integration-wL7tV` (4 commits on top of TV-004)

Goal: eliminate "stated but never proven" — every declared feature now runs end-to-end. Plus three new clusters in dependency order (15 → 1 → 5).

### Commit 1 — Refinery: dead stubs wired

| Fix | Detail |
|---|---|
| GeminiShellExecutor | Now instantiated in TaviViewModel; NL→shell translation active when API key present |
| AppCategorizer | Now instantiated; lazily loads categories on first FossilDeck appearance; shown in card subtitle |
| BuildLayout | Routes through `handleAIQuery()` instead of echo-only |
| SIMPLIFY_VIEW / EXPAND_VIEW | Update `_maxFocusItems` StateFlow + persist to DataStore; `flatMapLatest` re-subscribes live |
| GardenTendWorker | Scheduled from `TaviViewModel.init` (not only on boot); LINEAR 15-min backoff in both ViewModel and BootReceiver |
| HandoffToBot | `IntentRouter` routes `>botname: content` prefix; copies content to system clipboard and navigates |
| recentScopes | `TaviPreferences`: `RECENT_SCOPES_JSON` key, `recentScopes` flow, `addRecentScope()` (max 5, deduplicated) |
| RECORD_AUDIO | Orphan permission removed from AndroidManifest |

### Commit 2 — QoL improvements

| Fix | Detail |
|---|---|
| AIResponseBanner | Tap-to-dismiss; content scrollable up to 200dp (shell output can be 20+ lines) |
| FossilDeckScreen | `LaunchedEffect(candidates.size)` prevents out-of-bounds crash when list shrinks during active deck session |
| Scope chip strip | `FilterChip` LazyRow between FocusZone and PromptOrb; visible when scopes exist and orb is collapsed; tap switches scope without opening orb |

### Commit 3 — Cluster 15: Safe Action Buffer

| Cluster | Key Files | Status |
|---|---|---|
| 15 — Safe Action Buffer | `state/PendingAction.kt`, `shell/ActionPreflightCard.kt` | Complete |

- `PendingAction` sealed class: `ShellCommand(display, translated, executable)`, `DemoteApp`, `PromoteApp`, `ScopeChange`
- `pendingShellCommand: String?` replaced with `pendingAction: PendingAction?` throughout
- AI `DEMOTE_APP` / `PROMOTE_APP` now route through preflight (not silently executed)
- `ActionPreflightCard`: type icon + translated/display command + reversibility hint + Cancel/Execute buttons
- `SpatialLauncherScreen`: preflight shown at zIndex 6 when `RiskDetected` + `pendingAction != null`

### Commit 4 — Clusters 1 + 5: Clipboard + Handoffs

| Cluster | Key Files | Status |
|---|---|---|
| 1 — Clipboard / Transfer | `clipboard/ClipEntry.kt`, `clipboard/ClipboardRepository.kt`, `clipboard/ClipPanel.kt` | Complete |
| 5 — Handoffs | `ai/IntentRouter.kt` (`>` prefix), `viewmodel/TaviViewModel.kt` `handleHandoff()` | Complete |

- `ClipboardRepository`: reads system clipboard, auto-detects type (URL/PHONE/CODE/TEXT), dual DataStore + session-memory history (max 10)
- `TaviPreferences`: `CLIP_HISTORY_JSON`, `addClipEntry()`, `clearClipHistory()`
- `IntentRouter`: `ShowClipboard` result + `clip:` routing
- `ClipPanel`: animated chip row above PromptOrb; URL clips show per-bot send icons for direct handoff
- `TaviWarden.triggerEmergencyOff()` clears clip history
- PromptOrb placeholder updated to `? ask  ! act  /build  >bot`

### Hard constraints re-verified

- No AccessibilityService — unchanged
- Preflight visible before every risk action — no silent execution
- Clipboard history: in-memory only in private/session-only mode; cleared on emergency off
- Warden gates respected for all new features (botWorkspacesEnabled, isSessionOnlyMode)
- State Grammar: `RiskDetected` drives `ActionPreflightCard` anchor; `Blocked` on empty clipboard / unknown bot
