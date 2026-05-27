# TAVI — Roadmap

**Stand:** 2026-05-27 · Branch `claude/intent-zen-integration-wL7tV`

---

## Status-Übersicht

**18 von 19 Clustern implementiert.** Nur Cluster 10 (Supervised Game Drawer) verbleibt als eigenständiges Familien-Feature / potentielles Sub-Projekt.

---

## Was fertig ist

| Cluster | Name | Commit | Kernfunktion |
|---|---|---|---|
| 1 | Clipboard / Transfer Layer | TV-005 | ClipPanel, `clip:` Prefix, 10-Entry-History, Private Mode |
| 2 | Snippet Capsule | TV-006 | SnippetPanel, `snip:` Prefix, Favorites-Sort, DataStore |
| 3 | QuickActions | TV-006 | OPEN_URL, DIAL, SAVE_SNIPPET, SAVE_CAPSULE, PARK |
| 4 | Image-as-Intent | TV-010 | `img:` Prefix → System Picker → Gemini Vision → Banner |
| 5 | Handoffs | TV-005 | `>bot: content` → Clipboard + BotWorkspace-Navigation |
| 6 | Intent Controller | TV-007 | 30+ App-Regeln, IntentClarifierCard, Capture-State |
| 7 | Notification Sifter | TV-011 | 3 Default-Regeln, WardenScreen Toggle-Rows |
| 8 | Subscription Trap | TV-011 | 25 bekannte Apps, Warden-Subscription-Sektion |
| 9 | Psychotricks + Game Watch | TV-009 / TV-012 | 15 Muster, 25+ App-Familien, MediaProjection-Echtzeit |
| 11 | App Fossil Finder | TV-004 | Tinder-Deck, GardenEngine.markAsFossil(), Cloud-Kategorisierung |
| 12 | Zen Shell | TV-004 | GardenCanvas, FocusZone, SpatialLauncherScreen, Tilt-Parallax |
| 13 | Gesture Edge | TV-004 | TaviGestureRouter, 12%-Edge-Zonen, SwipeEngine |
| 14 | State Grammar | TV-004 | 9 States, Pure Reducer, StateAnchor, Public-safe Labels |
| 15 | Safe Action Buffer | TV-005 | PendingAction, ActionPreflightCard, Preflight für Shell/Demote/Promote/Scope |
| 16 | Desire Queue | TV-013 | WantPanel, `want:`, PARK, Abo/Manipulation-Enrichment |
| 17 | AI Tool Handoff | TV-004 | LocalAIEngine (Gemma), TaviAIEngine, GeminiShellExecutor |
| 18 | Work Capsule | TV-006 | CapsulePanel, `cap:` Prefix, AI-Response-Save, max 50 |
| 19 | Privacy / Warden | TV-004 | TaviWarden, WardenScreen, EmergencyOff, Private Mode |

---

## Was als nächstes kommen kann

### Cluster 10 — Supervised Game Drawer (Familien-Feature)
**Entscheidung ausstehend:** Wird Cluster 10 Teil von TAVI oder ein eigenes Sub-Projekt?

Wenn Teil von TAVI:
- Game-Profil + Session-Timer-Overlay (kein AccessibilityService — SYSTEM_ALERT_WINDOW-Foreground-Service)
- Preflight vor Spielstart (nutzt bereits vorhandene ActionPreflightCard als Pattern)
- Kinder-Profil (einfacher Scope mit eingeschränkter App-Sichtbarkeit)
- Weiches Spielende (Notification nach Zeit-Limit, kein harter Block)
- Debrief nach Session (nutzt GameSessionService.sessionDebrief)

Wenn Sub-Projekt: Repo `Lootziffer666/tavi-kids` mit geteiltem Vokabular (Warden, State Grammar), eigenem Code.

---

## Phase 2 — Feature-Erweiterungen bestehender Cluster

Diese Features sind in den Slot Contracts dokumentiert aber noch nicht implementiert:

### Cluster 1 (Clipboard)
- Datei- und Bild-Clipboard (Share Target)
- Pinboard (persistente Favoritenclips)

### Cluster 3 (QuickActions)
- Adresse → Maps/Navigation
- Datum/Uhrzeit → Kalender
- IBAN/Betrag → Merken
- Code-Fehler → Agent-Prompt

### Cluster 4 (Image-as-Intent)
- Rechnungsscreenshot → Betrag / IBAN / Fälligkeit (ML Kit OCR)
- Chat-Screenshot → Antwort / Termin / Aufgabe
- Bild schwärzen (vor Teilen)
- Share Target (direkter Capture aus anderen Apps)

### Cluster 6 (Intent Controller)
- Intent-Aufzeichnung (aus Entscheidungen lernen)
- Redirect: YouTube "Suchen" → PromptOrb vorausgefüllt
- KI-generierte Vorschläge für unbekannte Apps
- Share-Intent-Klärung

### Cluster 9 (Psychotricks)
- Reflexionsfrage pro Pattern (Tap-to-Expand)
- Kinderhinweis für PEGI-relevante Patterns
- Pattern-Statistik über Zeit

### Cluster 16 (Desire Queue)
- Purchase Preflight (Schwelle vor dem Öffnen eines geparkten Links)
- Decision Receipt (Protokoll getroffener Entscheidungen)
- Zeitbasierte Erinnerung nach X Tagen

### Cluster 17 (AI Tool Handoff)
- Modellwahl pro Aufgabe (in PromptOrb)
- Prompt-Budget-Anzeige / Credit-Bewusstsein
- Repo-Kontext anhängen (file content → capsule → AI context)

### Cluster 18 (Work Capsule)
- GitHub-Export (capsule → Gist / Repo)
- PDF/DOCX-Umwandlung
- Quelle-Anhang (welche Quellen flossen in die Antwort ein?)

---

## Release-Vorbereitung

Wenn MVP als stabil gilt (aktuell erfüllt für 18 Cluster):

1. **ProGuard-Regeln** finalisieren (Room, Moshi, Retrofit, Shizuku, MediaPipe)
2. **Datenschutzerklärung** verfassen (kein Server-Upload außer Gemini API wenn aktiviert; Private Mode als Hauptversprechen)
3. **APK-Signierung** einrichten
4. **Gerätetest** auf Android 7+ (SDK 24), Android 14+ (SDK 34 für MediaProjection-Consent-Dialog)
5. **Play Store Listing** ohne Dashboard/Wellbeing/Blocker-Framing — Positionierung als Intent-First-Launcher

---

## Architektur-Grenzen (permanent)

| Constraint | Status |
|---|---|
| Kein AccessibilityService | ✓ Eingehalten — alle Features über HOME-Launcher, MediaProjection, Clipboard API |
| Shizuku optional | ✓ Eingehalten — nur über Warden aktivierbar, nie Kernvoraussetzung |
| Kein AutoInput | ✓ Eingehalten |
| Anti-Dashboard | ✓ Eingehalten — ein dominantes UI-Element pro State |
| State Grammar (9 States) | ✓ Eingehalten — Pure Reducer, kein Drift |
| Keine heimliche Speicherung | ✓ Eingehalten — Private Mode + EmergencyOff als Garanten |
