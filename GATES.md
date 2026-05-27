# GATES — TAVI

**Stand:** 2026-05-27

---

## Abgeschlossene Gates

### Gate TV-001: Concept Contract Lock ✓
- **Ergebnis:** `CONCEPT_CONTRACT.md` und `NOT_IMPLEMENTED.md` als verbindlich etabliert. Kanonische Begriffe, State Grammar, Slot Contract, Forbidden Terms definiert.
- **Branch:** Früher Commit auf `claude/intent-zen-integration-wL7tV`

### Gate TV-002: Stack-Entscheidung ✓
- **Ergebnis:** Kotlin + Jetpack Compose + Gradle 8.x + KSP + Room + DataStore + WorkManager + MediaPipe (Gemma) + Retrofit/Moshi (Gemini). Dokumentiert in `docs/STACK.md`.
- **Kein KMP** — TAVI ist Android-only per Contract.

### Gate TV-003: Produkt-Planungs-Map ✓
- **Ergebnis:** Alle 19 Cluster mit Slot Contracts dokumentiert in `docs/CLUSTER_MAP.md`. Roadmap aufgeteilt in "Prototype now" / "Roadmap".

### Gate TV-004: Prototyp-Kernel ✓
- **Ergebnis:** Cluster 11 (App Fossil Finder), 12 (Zen Shell), 13 (Gesture Edge), 14 (State Grammar), 17 (AI Tool Handoff), 19 (Privacy/Warden) implementiert.
- **APK:** build-bar, App starts als HOME-Launcher.

### Gate TV-005: Foundation-Cluster ✓
- **Ergebnis:** Cluster 1 (Clipboard), 5 (Handoffs), 15 (Safe Action Buffer) + QoL-Refinery (GeminiShellExecutor wiring, AppCategorizer, /build command, SIMPLIFY/EXPAND_VIEW, Scope chips).

### Gate TV-006: Content-Cluster ✓
- **Ergebnis:** Cluster 2 (Snippet Capsule), 18 (Work Capsule), 3 (QuickActions).

### Gate TV-007: Intent-Cluster ✓
- **Ergebnis:** Cluster 6 (Intent Controller) — IntentClarifierEngine (25+ Regeln), IntentClarifierCard, TaviState.Capture.

### Gate TV-008: Erster Audit-Fix-Commit ✓
- **Ergebnis:** 10 Findings aus code-review behoben (SwipeEngine Coroutine-Fix, FossilDeck OOB-Guard, AIResponseBanner scroll, diverse Null-Checks und Wiring-Lücken).

### Gate TV-009: Manipulation-Cluster ✓
- **Ergebnis:** Cluster 9 MVP — ManipulationEngine (15 Patterns, 25+ App-Familien), IntentClarifierCard Pattern-Row (RiskRed).

### Gate TV-010: Image-Cluster ✓
- **Ergebnis:** Cluster 4 MVP — GeminiImageAnalyzer, img: Prefix, Image Picker in TaviShellScreen, AIResponseBanner Output.

### Gate TV-011: Sifter-Cluster ✓
- **Ergebnis:** Cluster 7 MVP (Notification Sifter: NotificationRule + NotificationRuleRepository + WardenScreen Toggle-Rows), Cluster 8 MVP (SubscriptionScanner 25 Apps + WardenScreen Subscription-Sektion).

### Gate TV-012: Game-Watch-Extension ✓
- **Ergebnis:** Cluster 9 Phase 2 — GameSessionService (MediaProjection Foreground-Service, VirtualDisplay + ImageReader, konfigurierbare Intervalle 30s/1min/2min, Live-Notifications, Session-Debrief). IntentClarifierCard Watch-Toggle. GeminiImageAnalyzer Bitmap-Overload.

### Gate TV-013: Desire-Queue ✓
- **Ergebnis:** Cluster 16 MVP — WantItem, WantShelfRepository, WantPanel, want:/PARK-Routing, EmergencyOff-Clear, Cluster-8/9-Enrichment.

### Gate TV-014: Zweiter Audit-Fix-Commit ✓
- **Ergebnis:** 4 confirmed bugs behoben — GeminiImageAnalyzer Null-Bitmap, GameSessionService Null-MediaProjection, WantPanel `formatAge()` eingefroren, TaviViewModel.onParkClip() URL-Extraktion.

---

## Laufende Audit-Fixes (TV-015 — in Arbeit)

Folgende Findings aus dem vollständigen Repo-Audit (post TV-014) wurden identifiziert und behoben:

| # | Datei | Art | Beschreibung |
|---|---|---|---|
| 1 | `ClipboardRepository.kt` | BUG | `history` Flow zeigte persistierte Clips trotz aktivem Private Mode |
| 2 | `TaviViewModel.kt` | BUG | `handleHandoff()` prüfte nur `sessionOnlyMode`, nicht `isPrivate` |
| 3 | `IntentClarifierEngine.kt` | BUG | `.x.` Match trifft nie `com.twitter.android`; fehlende Regeln für 30+ App-Familien |
| 4 | `ManipulationEngine.kt` | BUG | Gleiches `.x.`-Problem |
| 5 | `BotWorkspaceScreen.kt` | MISSING_WIRING | Kein `update`-Lambda → URL-Änderungen navigieren WebView nicht |

---

## Offene Roadmap-Gates

### Gate TV-016: Cluster 10 — Supervised Game Drawer (optional)
- **Scope:** Eigenständiges Familien-Feature / potentielles Sub-Projekt
- **Voraussetzungen:** Grundlagenentscheidung — Teil von TAVI oder eigenes Repo?
- **Kill:** Wenn als separates Projekt abgetrennt, hier schließen ohne Implementation

### Gate TV-099: Play Store / Side-load Release
- **Scope:** APK signieren, ProGuard-Regeln finalisieren, Datenschutzerklärung
- **Voraussetzungen:** Alle MVP-Cluster stabil (✓ erfüllt für Cluster 1-9, 11-19)

---

## Gate-Regel

Gate-Reihenfolge wird nicht nachträglich geändert. Neue Gates haben höhere Nummern.
