# TAVI Gates

| Gate | Description | Status |
|---|---|---|
| TV-001 | Concept Contract Lock | Complete |
| TV-002 | Stack Decision | Complete — Kotlin + Compose + KSP + Gradle 8.x (see STACK.md) |
| TV-003 | Opus Product Planning Map | Complete — CLUSTER_MAP.md merged, 19 clusters defined |
| TV-004 | First Prototype Cluster | Complete — branch `claude/intent-zen-integration-wL7tV` |

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
