# TAVI Stack Decision — Gate TV-002 ✓

**Status: Resolved** (2026-05-24, branch `claude/intent-zen-integration-wL7tV`)

## Decision

**Kotlin + Jetpack Compose + Gradle 8.x + KSP**

| Layer | Technology | Source |
|---|---|---|
| Language | Kotlin (Android-only, no KMP) | Both prototypes |
| UI | Jetpack Compose + Material 3 | Both prototypes |
| Build | Gradle 8.x + Version Catalog (libs.versions.toml) | Both prototypes |
| Annotation processing | KSP (Kotlin Symbol Processing) | Both prototypes |
| Persistence | Room 2.6.1 + DataStore Preferences 1.1.1 | Zen |
| Background | WorkManager 2.10.0 | Zen |
| On-device AI | MediaPipe Tasks GenAI 0.10.14 (Gemma) | Zen |
| Cloud AI | Retrofit 2.11.0 + Moshi + OkHttp (Gemini API) | INTENT |
| Image loading | Coil 2.7.0 | Both prototypes |
| Power adapter (optional) | Shizuku 13.1.5 | INTENT |
| Min SDK | 24 (Android 7.0) | Both prototypes |
| Compile/Target SDK | 36 (Android 16) | Both prototypes |

## Rationale

Both INTENT and Zen use identical stacks (same versions, same patterns). Kotlin Multiplatform adds zero benefit — TAVI is Android-only by contract. Compose is the only viable path because:

1. **Gesture layer** — `detectDragGestures` / `pointerInput` at the Compose layer intercepts all touch events without AccessibilityService, and without MIUI system gestures. This is the architectural constraint from Cluster 13.
2. **Spatial canvas** — `graphicsLayer { rotationX/Y/cameraDistance }` driven by accelerometer requires Compose Canvas for perspective rendering.
3. **State Grammar** — `sealed class TaviState` + `StateFlow` + `collectAsStateWithLifecycle()` is the idiomatic Compose state pattern already proven in both prototypes.

## Design Language

FLUBBER spec: Dark-first (SpaceBlack #050810 background), Lilita One for display headers, Barlow for body, JetBrains Mono for code/labels. 10 state-driven motion tokens (see `ui/theme/`).
