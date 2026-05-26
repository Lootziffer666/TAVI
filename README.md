<h1 align="center">TAVI</h1>

<p align="center">
  <strong>Tiny Action · Visual Intent</strong><br/>
  <em>Android-nahe Schwellen-, Intent- und Reibungsarchitektur</em>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android%2024%2B-green?style=flat-square" />
  <img src="https://img.shields.io/badge/Stack-Kotlin%20%2B%20Compose-blueviolet?style=flat-square" />
  <img src="https://img.shields.io/badge/Status-Implemented%20%E2%80%94%20branch%20wL7tV-brightgreen?style=flat-square" />
  <img src="https://img.shields.io/badge/AI-Gemma%20%2B%20Gemini%20fallback-orange?style=flat-square" />
</p>

---

## Was TAVI ist

Drei Kernbegriffe:

- **Schwelle** — der Moment zwischen Impuls und Aktion
- **Intent** — was der User eigentlich erreichen will
- **Reibung** — was zwischen User und Intent steht, was sie abbaut, was sie aufbaut

TAVI operiert auf der **Android-User-Seite**: vor der App, neben der App, statt der App. Ein räumlicher Android-Launcher mit lokalem AI-Kern, der die 19 TAVI-Cluster schrittweise materialisiert — ohne Accessibility, ohne Zwang, ohne Wellbeing-Dashboard.

## Was TAVI nicht ist

- Kein Wellbeing-Dashboard
- Kein App-Blocker
- Keine heimliche Automation
- Kein AccessibilityService-Pfad
- Kein AutoInput
- Kein Shizuku-Zwang (optional power adapter, nicht Kern)

Vollständige Liste: [`NOT_IMPLEMENTED.md`](NOT_IMPLEMENTED.md)

---

## Architektur

TAVI ist eine Cherry-Pick-Fusion aus zwei Prototypen: **Zen** (räumliches Modell, MediaPipe Gemma, Sensor-Parallax) und **INTENT** (Google AI Studio Prototyp, Bot Workspaces, Tinder-Deck, Shizuku). Das Beste beider Welten landet im `claude/intent-zen-integration-wL7tV`-Branch.

Vollständige Architektur: [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md)

### Modul-Übersicht

```
app/src/main/java/com/example/tavi/
├── MainActivity.kt              Entry: full immersion, HOME launcher, no back
├── state/                       State Grammar (9 Zustände, pure reducer)
│   ├── TaviState.kt             9 sealed states
│   ├── TaviStateReducer.kt      Pure reducer
│   └── PendingAction.kt         Cluster 15 — ShellCommand / DemoteApp / PromoteApp / ScopeChange
├── garden/                      Räumliches Modell (von Zen)
│   ├── GardenEngine.kt          Affinity-Scoring: 40% Frequenz + 40% Aktualität + 20% User
│   ├── GardenNode.kt            App-Repräsentation mit Tiefenlage + GrowthStage
│   └── GardenRepository.kt      Flow-basierte DAO-Brücke; foregroundNodes(limit) für reaktive Anpassung
├── shell/                       UI-Schicht
│   ├── GardenCanvas.kt          3D-Voxel-Grid + Polygon-Knoten (Compose Canvas)
│   ├── FocusZone.kt             Max 5 Nodes + Atemring (8-Sek-Animation)
│   ├── PromptOrb.kt             Pulsierender AI-Eingabe-Button; Placeholder: ? ask ! act /build >bot
│   ├── AIResponseBanner.kt      Ephemeral Banner; tap-to-dismiss; scrollbar für Shell-Output
│   ├── ActionPreflightCard.kt   Cluster 15 — Preflight-Card im RiskDetected-Zustand
│   ├── SpatialLauncherScreen.kt Z-Stack: Canvas(0) + StateAnchor(3) + ClipPanel(4) + ScopeChips(4)
│   │                             + PreflightCard(6) + PromptOrb(7)
│   └── TaviShellScreen.kt       HorizontalPager + Gesture-Routing + Warden-Overlay
├── clipboard/                   Cluster 1 — Clipboard / Transfer Layer
│   ├── ClipEntry.kt             data class + ClipType (TEXT/URL/PHONE/CODE/OTHER)
│   ├── ClipboardRepository.kt   DataStore + session-memory; auto-detects type; max 10 Einträge
│   └── ClipPanel.kt             AnimatedVisibility LazyRow; URL-Clips mit Bot-Send-Icons
├── gesture/                     Cluster 13 — ohne AccessibilityService
│   ├── TaviGestureRouter.kt     Edge-Zonen (12% Ränder) → GestureIntent
│   └── SwipeEngine.kt           Tinder-Karten-Physik (Animatable + detectDragGestures)
├── sensor/                      Accelerometer → TiltState (Low-Pass α=0.08)
├── ai/                          AI-Schicht
│   ├── LocalAIEngine.kt         MediaPipe Gemma (primär, streaming via Proxy)
│   ├── TaviAIEngine.kt          Orchestrator: Local → Cloud Fallback → Rule-Based
│   ├── IntentRouter.kt          Prefix-Routing: ? / ! / /build / >bot / clip: / http / settings
│   └── MobileActionsRouter.kt   JSON → promote_app / demote_app / narrate (DEMOTE/PROMOTE → Preflight)
├── cloud/                       Gemini API (Retrofit + Moshi, cloud fallback)
│   ├── GeminiShellExecutor.kt   NL → Shell-Kommando-Übersetzung (aktiv wenn API-Key vorhanden)
│   └── AppCategorizer.kt        App-Kategorisierung für FossilDeck (aktiv wenn API-Key vorhanden)
├── fossil/                      Cluster 11 — FossilDeckScreen (Tinder-Karten) + AppCategorizer
├── workspace/                   Bot Workspaces (ChatGPT, Claude, Gemini, ...)
├── warden/                      Cluster 19 — Privacy & Control
│   ├── TaviWarden.kt            Logic: private mode, Shizuku, cloud AI, emergency off + Clip-History-Clear
│   └── WardenScreen.kt          UI: Toggle-Rows + Emergency-Off-Button
├── shizuku/                     Optional power adapter für !-Kommandos
├── data/                        Room DB + DataStore Preferences
├── util/
│   └── JsonUtils.kt             extractFirstJsonObject() — geteilt von AppCategorizer, GeminiShellExecutor, MobileActionsRouter
└── ui/theme/                    FLUBBER Design Language
    ├── Color.kt                 SpaceBlack / TaviAccent / BreathBlue / RiskRed / ...
    └── Type.kt                  Lilita One / Barlow / JetBrains Mono (Google Fonts)
```

### Gesture-Architektur (kein MIUI, kein AccessibilityService)

TAVI registriert sich als HOME-Category-Launcher. Das gibt der App **vollständiges Fenster-Ownership** — alle Touch-Events erreichen Compose ohne Systemgesten-Interferenz.

```
Layer 1: HorizontalPager    (Seiten-Swipes — Compose nativ)
Layer 2: TaviGestureRouter  (Edge-Zonen — pointerInput auf Root-Box)
Layer 3: SwipeEngine        (Karten-Physik — detectDragGestures auf Karte)
Layer 4: SpatialSensorManager (Accelerometer → Parallax, kein Touch nötig)
```

Linke Kante → FossilDeck | Rechte Kante → BotWorkspaces | Unten/Oben → PromptOrb

### AI-Schicht

```
User-Eingabe
    ↓
IntentRouter (Prefix-Routing)
    ├── ?query     → TaviAIEngine
    │                   ├── LocalAIEngine (Gemma via MediaPipe, streaming Tokens)
    │                   ├── Cloud fallback (Gemini 1.5 Flash, wenn Gemma unavailable)
    │                   └── Rule-based fallback (immer verfügbar, kein Netz nötig)
    ├── !cmd       → GeminiShellExecutor (NL→Shell wenn Cloud AI on + natürliche Sprache erkannt)
    │                   → ShizukuManager via ActionPreflightCard (RiskDetected gate)
    ├── /build     → handleAIQuery("Adjust the launcher layout: …") → TaviAIEngine
    ├── >bot: txt  → handleHandoff() → Clipboard + Bot-Navigation (Cluster 5)
    ├── clip:      → ClipPanel öffnen (Cluster 1)
    ├── http…      → Intent.ACTION_VIEW
    └── settings   → Settings.ACTION_SETTINGS
```

### State Grammar

9 Zustände, kein Drift: `Idle → Ready → Capture → ActNow → Idle` (happy path). Jeder Zustand treibt ein einziges dominantes UI-Element (One Anchor per Moment).

---

## Setup

### Voraussetzungen

- Android Studio Meerkat oder neuer
- Min SDK 24, Target SDK 36
- Gemma-Modell-Datei (optional, lokal) für on-device AI
- Gemini API Key (optional) für Cloud-Fallback

### Build

```bash
# Gemini API Key setzen (optional)
echo "GEMINI_API_KEY=your_key_here" >> local.properties

./gradlew assembleDebug
```

### Gemma-Modell laden

Modell-Pfad in Warden-Einstellungen (`ai_model_path` DataStore-Key) eintragen. Das Modell wird über MediaPipe LlmInference geladen. Ohne Modell läuft TAVI im Rule-Based-Fallback.

---

## Branch

Alle Implementierungen: `claude/intent-zen-integration-wL7tV`

---

## 19 Cluster — Implementierungsstatus

| # | Cluster | Status |
|---|---|---|
| 1 | Clipboard / Transfer Layer | Implemented — ClipboardRepository + ClipPanel + `clip:` routing |
| 5 | Handoffs | Implemented — `>bot: content` IntentRouter + handleHandoff() + ClipPanel bot icons |
| 11 | App Fossil Finder | Implemented — FossilDeckScreen + GardenEngine.markAsFossil() + AppCategorizer |
| 12 | Zen Shell / Launcher Rooms | Implemented — GardenCanvas + FocusZone + SpatialLauncherScreen |
| 13 | Overlay / Handles / Gesture Edge | Implemented — TaviGestureRouter + SwipeEngine |
| 14 | State Grammar / One Anchor | Implemented — TaviState + TaviStateReducer + StateAnchor |
| 15 | Safe Action Buffer | Implemented — PendingAction + ActionPreflightCard; all risk actions through preflight |
| 17 | AI / Tool Handoff | Implemented — LocalAIEngine + TaviAIEngine + IntentRouter + GeminiShellExecutor wired |
| 19 | Privacy / Control / Warden | Implemented — TaviWarden + WardenScreen |
| 2–4, 6–10, 16, 18 | Weitere Cluster | Roadmap |

---

## Verwandt

| Projekt | Beziehung |
|---|---|
| [ANVIL](https://github.com/Lootziffer666/ANVIL) | Geteiltes Vokabular (Warden, State Grammar). Parallele Domäne (Coder-IDE). |
| [BORDERLINE](https://github.com/Lootziffer666/BORDERLINE) | Donor für Snippet/Clipper-Patterns. Trennung: Borderline mit Accessibility, TAVI ohne. |
| [INTENT](https://github.com/Lootziffer666/intent) | Google AI Studio Prototyp. Cherry-Pick-Quelle für Bot Workspaces, Tinder-Deck, Shizuku. |
| [ZEN](https://github.com/Lootziffer666/zen) | Claude Prototyp. Cherry-Pick-Quelle für räumliches Modell, Gemma, Sensor-Parallax. |
| [FLUBBER](https://github.com/Lootziffer666/FLUBBER) | Design-Sprache (als Skill installiert). |

---

<p align="center"><em>Schwelle. Intent. Reibung. Räume statt App-Salat.</em></p>
