# TAVI Cluster Map — 19 Cluster mit Slot Contracts

**Stand:** 2026-05-26 (aktualisiert nach TV-005 Delivery)
**Status:** Cluster 1, 5, 11, 12, 13, 14, 15, 17, 19 implementiert auf `claude/intent-zen-integration-wL7tV`.
**Schema:** Jeder Cluster folgt dem Slot Contract aus `CONCEPT_CONTRACT.md` (Name, Purpose, User State, Input, Output, Visible Surface, Failure Behavior) plus Roadmap-Status, Dependencies und MVP-Cut-Empfehlung.

---

## Implementierungsstatus

| Cluster | Name | Status |
|---|---|---|
| 1 | Clipboard / Transfer Layer | **Implemented** — ClipboardRepository + ClipPanel + TaviPreferences keys |
| 5 | Handoffs | **Implemented** — IntentRouter `>` prefix + handleHandoff() + ClipPanel bot icons |
| 11 | App Fossil Finder | **Implemented** — FossilDeckScreen + GardenEngine.markAsFossil() + AppCategorizer |
| 12 | Zen Shell / Launcher Rooms | **Implemented** — GardenCanvas + FocusZone + SpatialLauncherScreen |
| 13 | Overlay / Handles / Gesture Edge | **Implemented** — TaviGestureRouter + SwipeEngine |
| 14 | State Grammar / One Anchor | **Implemented** — TaviState + TaviStateReducer + StateAnchor |
| 15 | Safe Action Buffer | **Implemented** — PendingAction + ActionPreflightCard + preflight routing |
| 17 | AI / Tool Handoff | **Implemented** — LocalAIEngine + TaviAIEngine + IntentRouter + GeminiShellExecutor |
| 19 | Privacy / Control / Warden | **Implemented** — TaviWarden + WardenScreen |
| 2–4, 6–10, 16, 18 | Weitere Cluster | Roadmap |

---

## Roadmap-Übersicht

**Prototype now (12):** Cluster 1, 2, 3, 4, 5, 8, 9, 11, 14, 15, 18, 19
**Roadmap (7):** Cluster 6, 7, 10, 12, 13, 16, 17
**Implementiert (TV-004):** Cluster 11, 12, 13, 14, 17, 19
**Implementiert (TV-005):** Cluster 1, 5, 15 — plus QoL-Verbesserungen, Refinery

**Empfohlener MVP-Schnitt (5 Cluster):** 1 Clipboard, 2 Snippets, 5 Handoffs, 14 State Grammar, 19 Privacy/Warden. Alles andere ist Phase 2+.

---

## Cluster 1 — Clipboard / Transfer Layer ✓ Implemented

| Feld | Inhalt |
|---|---|
| **Name** | Clipboard / Transfer Layer |
| **Purpose** | Inhalte zwischen Apps, Aktionen und Zuständen verlustfrei bewegen |
| **User State** | „Ich habe etwas und will es weiterverwenden" |
| **Input** | Text, Link, Datei, Bild, Screenshot, Markdown |
| **Output** | Clipboard-Inhalt, Datei, Verweis, Status |
| **Visible Surface** | ClipPanel (AnimatedVisibility LazyRow über PromptOrb) |
| **Failure Behavior** | `TaviState.Blocked("Clipboard empty")` bei leerem Clipboard; Private Mode: kein DataStore-Write |
| **Roadmap** | **Implemented (TV-005)** |
| **Dependencies** | keine (Foundation) |
| **MVP-Cut** | enthalten — Foundation für Cluster 2, 3, 4, 5 |

**Implemented:** `ClipEntry` + `ClipType` (TEXT/URL/PHONE/CODE/OTHER), `ClipboardRepository` (DataStore + session-memory dual history, max 10, auto-detect type), `ClipPanel` composable (chips + URL-Bot-Handoff-Icons), `TaviPreferences.CLIP_HISTORY_JSON`, `IntentRouter.ShowClipboard` + `clip:` prefix, `TaviWarden.triggerEmergencyOff()` löscht History.

**Noch nicht implementiert (Phase 2):** Datei- und Bild-Clipboard, Share Target, Pinboard.

---

## Cluster 2 — Snippet Capsule

| Feld | Inhalt |
|---|---|
| **Name** | Snippet Capsule |
| **Purpose** | Wiederkehrende Texte und Arbeitsbausteine griffbereit machen |
| **User State** | „Ich brauche denselben Baustein wieder" |
| **Input** | Text, Auswahl, Chat-Output, Datei |
| **Output** | kopierter / speicherbarer Baustein |
| **Visible Surface** | Snippet Panel, Suchfläche, Favoriten |
| **Failure Behavior** | Baustein bleibt als Datei / Eintrag verfügbar |
| **Roadmap** | Prototype now |
| **Dependencies** | Cluster 1 (Clipboard) |
| **MVP-Cut** | enthalten — Christian nutzt das in Borderline schon |

**Features:** Prompt-Snippets, Projekt-Snippets, Antwort-Snippets, Markdown-Snippets, Recovery-Snippets, Agenten-Aufträge, Kategorien, Favoriten, Suche, Varianten, Snippet aus Auswahl/Chat erzeugen.

---

## Cluster 3 — QuickActions / Kontextaktionen

| Feld | Inhalt |
|---|---|
| **Name** | QuickActions |
| **Purpose** | aus erkanntem Kontext kurze sinnvolle Aktionen machen |
| **User State** | „Hier steht etwas, was eigentlich eine Handlung ist" |
| **Input** | Text, Clipboard, Share, später Screenshot |
| **Output** | kleine Aktionsauswahl |
| **Visible Surface** | Mini-Panel, Kontextmenü |
| **Failure Behavior** | keine sichere Aktion erkannt → manuelle Auswahl |
| **Roadmap** | Prototype now |
| **Dependencies** | Cluster 1 (Clipboard), Cluster 5 (Handoffs) |
| **MVP-Cut** | nach MVP — braucht stabile Pattern-Erkennung |

**Features:** Adresse → Maps/CatchIt/kopieren, Telefonnummer → anrufen/speichern, Datum/Uhrzeit → Kalender/Erinnerung, Betrag/IBAN → kopieren/prüfen/merken, Link → öffnen/speichern/zusammenfassen, Rezept → Einkaufsliste/Notiz, Rechnung → Betrag/Fälligkeit/PDF, Code/Fehler → erklären/suchen/Agentenprompt.

---

## Cluster 4 — Image-as-Intent / Capture

| Feld | Inhalt |
|---|---|
| **Name** | Image-as-Intent / Capture Layer |
| **Purpose** | Bilder nicht sammeln, sondern in nächste Aktionen verwandeln |
| **User State** | „Ich habe ein Bild, aber brauche den nächsten Schritt" |
| **Input** | Screenshot, Foto, Bilddatei |
| **Output** | Text, PDF, Aktionsvorschläge, Schwärzung |
| **Visible Surface** | Capture Panel, Share Target |
| **Failure Behavior** | Bild bleibt unverändert; Ergebnis als unsicher markieren |
| **Roadmap** | Prototype now / Early Roadmap |
| **Dependencies** | Cluster 1, OCR-Backend (ML Kit), Cluster 5 (Handoffs) |
| **MVP-Cut** | nach MVP — OCR-Integration ist eigenes Gate |

**Features:** Screenshot als Handlung, Dokumentfoto → OCR/PDF/Zusammenfassung, Rechnungsscreenshot → Betrag/IBAN/Fälligkeit, Chat-Screenshot → Antwort/Termin/Aufgabe, Produktlabel → Seriennummer/Notiz, Paywall-Screenshot → Abo-Risiko, Fehlerseite → Diagnoseprompt, Bild schwärzen, Bild zu Text, Bild zu PDF.

---

## Cluster 5 — Handoffs ✓ Implemented

| Feld | Inhalt |
|---|---|
| **Name** | Handoff Layer |
| **Purpose** | Zustand an das richtige Werkzeug übergeben |
| **User State** | „Ich will nicht App suchen, sondern Ziel erreichen" |
| **Input** | Text, Link, Prompt — via `>botname: content` Syntax im PromptOrb |
| **Output** | Inhalt in System-Clipboard kopiert + Navigation zur Bot-Workspace-Seite |
| **Visible Surface** | PromptOrb hint `>bot`; ClipPanel zeigt Send-Icons für URL-Clips |
| **Failure Behavior** | `TaviState.Blocked("Bot workspaces are off")` / `Blocked("Unknown bot: X")`; Inhalt bleibt in Clipboard |
| **Roadmap** | **Implemented (TV-005)** |
| **Dependencies** | Cluster 1 (Clipboard) |
| **MVP-Cut** | enthalten — zentrale Reibungsreduktion |

**Implemented:** `IntentRouter.HandoffToBot(botId, content)` + `>` prefix-Routing, `TaviViewModel.handleHandoff()` (Clipboard-Copy + Bot-Navigation + History-Eintrag), ClipPanel URL-Clips mit per-Bot-Send-Icons, `onClipHandoff()` Funktion, PromptOrb Placeholder-Update.

**Noch nicht implementiert (Phase 2):** Datei-Handoff, Share Target, Adresse → Maps, Code → Agent.

---

## Cluster 6 — Intent Controller

| Feld | Inhalt |
|---|---|
| **Name** | Intent Controller |
| **Purpose** | zwischen Impuls und Aktion eine klärende Schwelle setzen |
| **User State** | „Ich öffne etwas, aber vielleicht ohne klares Ziel" |
| **Input** | App-Start, Share, Shortcut, Kontext |
| **Output** | weiterlassen, fragen, parken, umlenken |
| **Visible Surface** | Preflight, Launcher-Schicht |
| **Failure Behavior** | im Zweifel weiterlassen, nicht blockieren |
| **Roadmap** | Roadmap |
| **Dependencies** | Cluster 12 (Zen Shell), Cluster 13 (Gesture Edge), Cluster 14 (State Grammar) |
| **MVP-Cut** | nicht im MVP — braucht Cluster 12 + 13 als Hosts |

**Features:** App-Start mit Zielklärung, Impulsbremse, Doomscroll-Warnung, Moduswahl vor App, Share-Intent-Klärung, Bild-/Text-Intent-Klärung, Entscheidung merken, Absicht statt App.

---

## Cluster 7 — Notification Sifter

| Feld | Inhalt |
|---|---|
| **Name** | Notification Sifter |
| **Purpose** | Benachrichtigungen nach Bedeutung statt Lautstärke sortieren |
| **User State** | „Nicht alles, was piept, verdient mich" |
| **Input** | Notification, App, Zeit, Kontext |
| **Output** | zeigen, bündeln, später, ignorieren, erklären |
| **Visible Surface** | Digest, kurzer Status, Launcher-Hinweis |
| **Failure Behavior** | wichtige Dinge nicht verschlucken |
| **Roadmap** | Roadmap |
| **Dependencies** | Notification-Access-Permission (Android-spezifisch) |
| **MVP-Cut** | nicht im MVP — Permission-heavy, Risiko-Cluster |

**Features:** kritisch sofort, menschliche Nachrichten, Werbung bündeln, Systemnoise sammeln, Abo-/Zahlungshinweise hervorheben, Game-Lockreiz dämpfen, Streak-Druck erkennen, Digest, Herkunft erklären, ruhige Fenster.

---

## Cluster 8 — Abo-Alarm / Subscription Trap Detector

| Feld | Inhalt |
|---|---|
| **Name** | Abo-Alarm |
| **Purpose** | Abo-, Billing- und Trial-Fallen sichtbar machen |
| **User State** | „Hier will mich etwas binden oder kostenpflichtig machen" |
| **Input** | Screenshot, Text, Link, Store-Hinweis |
| **Output** | Warnung, Risiko, Merker, Reminder |
| **Visible Surface** | Paywall-Prüfer, Capture Panel |
| **Failure Behavior** | „unklar" statt falsches Urteil |
| **Roadmap** | Prototype now / Roadmap |
| **Dependencies** | Cluster 4 (Image-as-Intent) für Screenshot-Analyse |
| **MVP-Cut** | nach MVP — braucht Cluster 4 vollständig |

**Features:** Off-Play-Billing erkennen, Probeabo prüfen, Preisintervall sichtbar machen, Kündigungsweg prüfen, Paywall-Screenshot analysieren, Dark-Pattern-Hinweise, „Nur heute"-Druck, Kleingedrucktes extrahieren, Abo-Merker, Kündigungs-Erinnerung, Risk Label.

---

## Cluster 9 — Psychotricks / Manipulationsmuster

| Feld | Inhalt |
|---|---|
| **Name** | Psychotricks |
| **Purpose** | manipulative Mechaniken benennen, ohne moralisch zu blockieren |
| **User State** | „Diese App zieht an mir" |
| **Input** | Screenshot, App, Notification, Beschreibung |
| **Output** | Mustername, Erklärung, nächste Option |
| **Visible Surface** | Prüfer, Lexikon, Game-Preflight |
| **Failure Behavior** | als unklar markieren |
| **Roadmap** | Prototype now / Roadmap |
| **Dependencies** | Cluster 4 (Image-as-Intent) |
| **MVP-Cut** | nach MVP — Lexikon kann eigenständig starten |

**Features:** Daily Rewards, Streaks, Lootboxen, Gacha, Battle Pass, Energy-Systeme, FOMO, Comeback-Belohnung, aggressive Pushs, Kaufdruck, Manipulationslexikon, Kinderhinweis, Reflexionsfrage.

---

## Cluster 10 — Game / Kids / Supervision

| Feld | Inhalt |
|---|---|
| **Name** | Supervised Game Drawer |
| **Purpose** | Spiele bewusst starten und sauber verlassen |
| **User State** | „Ich / mein Kind will spielen, aber nicht versinken" |
| **Input** | Spiel, Profil, Zeit, Freigabe, Risiko |
| **Output** | Start, Timer, Hinweis, Debrief |
| **Visible Surface** | Game Room, Kinderfläche |
| **Failure Behavior** | nicht hart abstürzen; normale App bleibt erreichbar |
| **Roadmap** | Roadmap |
| **Dependencies** | Cluster 12 (Zen Shell), Cluster 9 (Psychotricks) |
| **MVP-Cut** | nicht im MVP — separates Familien-Feature |

**Features:** Supervised Game Drawer, Spiel-Preflight, Session-Timer, Kinderprofil, Elternkuratierung, PEGI-/USK-Gründe anzeigen, Trailer-Button, lokaler Spielkatalog, weiches Spielende, Debrief, Coop/Singleplayer-Hinweis, Zeitfenster, Manipulationshinweise.

**Notiz für TAVI:** dieser Cluster ist deinen Söhnen näher als TAVI primär adressiert. Vielleicht eigenes Sub-Projekt unter ink&iron glow (analog Stanley/Habitat aus Memory).

---

## Cluster 11 — App Fossil Finder / Smart App Inventory

| Feld | Inhalt |
|---|---|
| **Name** | App Fossil Finder |
| **Purpose** | App-Salat in nachvollziehbare Bedeutung verwandeln |
| **User State** | „Ich weiß nicht mehr, warum diese App da ist" |
| **Input** | App-Liste, Installationsdatum, Nutzung, Nutzer-Notiz |
| **Output** | Kategorie, Grund, Status, Vorschlag |
| **Visible Surface** | App-Inventar, Aufräumraum |
| **Failure Behavior** | unklar markieren, niemals blind löschen |
| **Roadmap** | Prototype now / Roadmap |
| **Dependencies** | PackageManager-Read-Permission (Android-Standard, kein Accessibility) |
| **MVP-Cut** | nach MVP — eigenständiges Feature |

**Features:** installierte Apps semantisch sortieren, nie gestartete Apps markieren, Installationsgrund rekonstruieren, App als Inspiration/Kandidat/Müll/Projektbezug markieren, App als Manipulator markieren, App als kinderrelevant markieren, Teststatus, Löschvorschläge, „Warum ist das hier?"-Ansicht.

---

## Cluster 12 — Launcher / Rooms / Zen Shell

| Feld | Inhalt |
|---|---|
| **Name** | Zen Shell |
| **Purpose** | Android-Einstiege als Räume statt App-Salat organisieren |
| **User State** | „Ich betrete einen digitalen Raum" |
| **Input** | App-Inventar, Module, Kontext, Nutzerzustand |
| **Output** | ruhige Startfläche, Räume, passende Einstiege |
| **Visible Surface** | Launcher / Homescreen |
| **Failure Behavior** | normaler App-Zugriff bleibt möglich |
| **Roadmap** | Roadmap |
| **Dependencies** | Cluster 11 (App Fossil Finder), Cluster 13 (Gesture Edge) |
| **MVP-Cut** | nicht im MVP — Launcher ist großer Aufwand, eigenständige Phase |

**Features:** Homescreen als ruhige Fläche, App-Start mit Schwelle, Intent-first Start, Module im Homescreen, Game Drawer, Tool Drawer, Fossil Drawer, seltene Apps ausblenden, Kontext-Homescreen, Kinder-Homescreen, Fokus-Homescreen ohne Dashboard-Look, Projekt-Homescreen, Homescreen als Schwellenraum.

---

## Cluster 13 — Overlay / Handles / Gesture Edge

| Feld | Inhalt |
|---|---|
| **Name** | Gesture Edge |
| **Purpose** | häufige Aktionen per Muskelgedächtnis erreichbar machen |
| **User State** | „Ich will eine Mini-Aktion ohne App-Wechsel" |
| **Input** | Swipe, Press, Flick, Kontext |
| **Output** | kleines Panel, Aktion, Handoff |
| **Visible Surface** | Randgriff / Werkzeugkante |
| **Failure Behavior** | Handle deaktivieren, normale App bleibt nutzbar |
| **Roadmap** | Roadmap |
| **Dependencies** | KEINE Accessibility (anders als Borderline!), SYSTEM_ALERT_WINDOW Permission |
| **MVP-Cut** | nicht im MVP — kritisch: muss ohne Accessibility funktionieren, das ist Forschungsaufgabe |

**Features:** Edge Handles, vier Einstiegspunkte, Keyboard-Snapping, Swipe statt Button, Einhandbedienung, Kontext-Handle, Handle nur zeigen wenn nützlich, Swipe-Richtung als Bedeutung, Press-and-hold, Flick-Geste, Mini-Panel, Werkzeugkante.

**Wichtig:** Anders als BORDERLINE darf TAVI nicht auf Accessibility setzen. Edge-Handle muss über SYSTEM_ALERT_WINDOW + Foreground Service implementiert werden.

---

## Cluster 14 — State Grammar / One Anchor

| Feld | Inhalt |
|---|---|
| **Name** | Zen State Grammar |
| **Purpose** | jedem Moment eine lesbare führende Wahrheit geben |
| **User State** | „Was verlangt dieser Zustand gerade?" |
| **Input** | Ereignis, Modul, Risiko, Kontext |
| **Output** | Zustand, Anchor, erlaubte Optionen |
| **Visible Surface** | alle TAVI-Flächen |
| **Failure Behavior** | ruhig erklären, ein Recovery-Pfad |
| **Roadmap** | Prototype now |
| **Dependencies** | keine (Foundation für alle UI) |
| **MVP-Cut** | enthalten — ohne dies funktioniert keine andere UI sauber |

**State-Liste (aus CONCEPT_CONTRACT.md):** Idle / Ready / Capture / Intent Unclear / Risk Detected / Act Now / Blocked / Failed / Private / Fallback.

**Features:** One Anchor per Moment, Public-safe Labels, Recovery ohne Drama.

---

## Cluster 15 — Safe Action Buffer ✓ Implemented

| Feld | Inhalt |
|---|---|
| **Name** | Safe Action Buffer |
| **Purpose** | irreversible oder riskante Aktionen prüfbar machen |
| **User State** | „Das könnte Folgen haben — lass mich genau sehen, was passiert" |
| **Input** | Shell-Kommando, App-Remove (AI DEMOTE_APP), App-Pin (AI PROMOTE_APP), Scope-Wechsel |
| **Output** | `ActionPreflightCard`: Typ-Icon + Kommando (NL-übersetzt wenn verfügbar) + Reversibilität + Cancel/Execute |
| **Visible Surface** | Full-width Card-Overlay am unteren Rand von `SpatialLauncherScreen` im `RiskDetected`-Zustand |
| **Failure Behavior** | Bei Execution-Fehler → `TaviState.Failed`; Action gecancelt, State zurück auf Idle |
| **Roadmap** | **Implemented (TV-005)** |
| **Dependencies** | Cluster 14 (State Grammar), Cluster 19 (Privacy/Warden) |
| **MVP-Cut** | enthalten |

**Implemented:** `PendingAction` sealed class (`ShellCommand(display, translated, executable)` / `DemoteApp` / `PromoteApp` / `ScopeChange`), `ActionPreflightCard` Composable, `pendingShellCommand: String?` → `pendingAction: PendingAction?` in TaviUiState, AI `DEMOTE_APP`/`PROMOTE_APP` durch Preflight geroutet (nicht mehr silent), `onRiskConfirmed()` pattern-matched auf Typ.

**Noch nicht implementiert (Phase 2):** Before/After Review, Blast-Radius-Schätzung, Agent Budget Governor.

**Verbindung zu ANVIL:** Strukturell identisch zu ANVILs Forge Patch-First Loop (Plan → Patch → Diff → Gate → Build → Test → Artifact). Pattern teilen, Code nicht.

---

## Cluster 16 — Desire Queue / Want Shelf

| Feld | Inhalt |
|---|---|
| **Name** | Desire Queue |
| **Purpose** | Wunsch von Druck trennen |
| **User State** | „Ich will das gerade, aber vielleicht wegen Druck" |
| **Input** | Produkt, App, Abo, Angebot, Link |
| **Output** | parken, später prüfen, bewusst ausführen |
| **Visible Surface** | Want Shelf, Purchase Preflight |
| **Failure Behavior** | Wunsch nur parken, nicht bewerten |
| **Roadmap** | Roadmap |
| **Dependencies** | Cluster 8 (Abo-Alarm), Cluster 9 (Psychotricks) |
| **MVP-Cut** | nicht im MVP |

**Features:** Wunsch parken, App installieren später prüfen, Kaufdruck dämpfen, Rabatt/FOMO sichtbar machen, Angebot speichern, Opportunity Sifter, Purchase Preflight, Decision Receipt.

---

## Cluster 17 — AI / Tool Handoff

| Feld | Inhalt |
|---|---|
| **Name** | Tool Handoff / Agent Governor |
| **Purpose** | AI-/Tool-Nutzung bewusst und zielgerichtet starten |
| **User State** | „Ich brauche ein Modell oder einen Agenten, aber sauber begrenzt" |
| **Input** | Ziel, Kontext, Budget, Dateien, Modell |
| **Output** | Prompt, Auftrag, Handoff, gespeicherter Output |
| **Visible Surface** | AI Panel, Work Capsule |
| **Failure Behavior** | Auftrag bleibt als Entwurf erhalten |
| **Roadmap** | Prototype now / Roadmap |
| **Dependencies** | Cluster 5 (Handoffs), Cluster 15 (Safe Action Buffer), Cluster 18 (Work Capsule) |
| **MVP-Cut** | reduzierte Variante im MVP (nur 1-2 Provider) |

**Features:** Modellwahl pro Aufgabe, Prompt vorbereiten, Agentenauftrag bauen, Repo-Kontext anhängen, Output speichern, Output vergleichen, Recovery Prompt, Kostenhinweis, Credit-Bewusstsein, No-Slop-Filter, Model Notes.

**Verbindung zu ANVIL:** ANVIL-FORGE-Donor liefert Patterns für on-device LLM (LiteRT), Function Calling, MCP-Integration. Direkt anwendbar.

---

## Cluster 18 — Work Capsule / Artifact

| Feld | Inhalt |
|---|---|
| **Name** | Work Capsule |
| **Purpose** | laufende Arbeitszustände einfrieren |
| **User State** | „Ich darf diesen Stand nicht verlieren" |
| **Input** | Text, Datei, Chat-Output, Status, Handoff |
| **Output** | Capsule, Markdown, Datei, Verweis |
| **Visible Surface** | Work Room, Artifact Panel |
| **Failure Behavior** | Datei / Verweis statt Verlust |
| **Roadmap** | Prototype now / Roadmap |
| **Dependencies** | Cluster 1 (Clipboard), Storage-Backend |
| **MVP-Cut** | reduzierte Variante im MVP — nur Markdown-Capsules |

**Features:** Markdown-Artefakte speichern, Artefakt zu Clipboard, Artefakt zu GitHub, Quelle markieren, Naming-Schema, FLOWINPUT-Export nur außerhalb TAVI-Kontext, Dokument-Fallback, Spec-Sammlung, Raw/Final/Discarded, Quelle behalten, PDF/DOCX/MD-Umwandlung, Repo-ready Ausgabe.

---

## Cluster 19 — Privacy / Control / Warden

| Feld | Inhalt |
|---|---|
| **Name** | TAVI Warden |
| **Purpose** | Nutzerkontrolle, Löschung, Sichtbarkeit und Modulgrenzen sichern |
| **User State** | „Ich muss wissen und kontrollieren, was TAVI tut" |
| **Input** | Module, Speicher, Rechte, App-Kontext |
| **Output** | ausschalten, löschen, privat, prüfen |
| **Visible Surface** | Provisioning / Control Surface |
| **Failure Behavior** | Modul abschalten, TAVI-Kern bleibt nutzbar |
| **Roadmap** | Prototype now / Roadmap |
| **Dependencies** | keine (Foundation parallel zu Cluster 14) |
| **MVP-Cut** | enthalten — Pflicht ab Tag 1 |

**Features:** Emergency Off, Private Mode, Delete All, App-Blacklist, Sensitive App Mode, sichtbarer Speicherstatus, sichtbarer Berechtigungsstatus, Audit-Ansicht, Modul einzeln abschalten, lokale Kontrolle, Schwärzen vor Teilen.

**Verbindung zu ANVIL:** TAVI Warden ist parallel zu ANVIL Warden, gleiche Namens-Disziplin, getrennte Implementation.

---

## Cluster-Dependency-Graph

```
Foundation Layer (parallel implementierbar, keine Deps):
  • Cluster 1   Clipboard / Transfer
  • Cluster 14  State Grammar
  • Cluster 19  Warden

Build directly on Foundation:
  • Cluster 2   Snippet Capsule       → 1
  • Cluster 5   Handoffs              → 1
  • Cluster 18  Work Capsule          → 1

Build on Foundation + 1 dependency:
  • Cluster 3   QuickActions          → 1, 5
  • Cluster 15  Safe Action Buffer    → 14, 19

Capture-heavy (need image / OCR backend):
  • Cluster 4   Image-as-Intent       → 1, 5, OCR
  • Cluster 8   Abo-Alarm             → 4
  • Cluster 9   Psychotricks          → 4

Workflow-heavy (need multiple deps):
  • Cluster 17  AI Tool Handoff       → 5, 15, 18
  • Cluster 11  App Fossil Finder     → PackageManager-API

Roadmap (need Launcher / Edge infrastructure):
  • Cluster 12  Zen Shell             → 11, 13
  • Cluster 13  Gesture Edge          → SYSTEM_ALERT_WINDOW (no Accessibility!)
  • Cluster 6   Intent Controller     → 12, 13, 14
  • Cluster 7   Notification Sifter   → Notification-Permission
  • Cluster 10  Game Drawer           → 12, 9
  • Cluster 16  Desire Queue          → 8, 9
```

---

## Empfohlener MVP (5 Cluster)

| # | Cluster | Begründung |
|---|---|---|
| 1 | Clipboard / Transfer | Foundation für alles andere |
| 2 | Snippet Capsule | Stärkster sofortiger Nutzen, du nutzt es schon in Borderline |
| 5 | Handoffs | Kern-Reibungsreduktion |
| 14 | State Grammar | Foundation für jede UI |
| 19 | Warden | Pflicht ab Tag 1, kein User-Vertrauen ohne |

Dieser MVP-Schnitt erlaubt es, TAVI in 6–8 Wochen lauffähig zu bekommen — mit klarem Wert für Christian persönlich (Snippet-Workflow + Reibungsreduktion bei Tool-Wechsel), bevor Investment in die schwereren Cluster (Launcher, Edge, OCR) startet.

---

## Verbindungen zu ANVIL (vereinfacht)

| TAVI-Cluster | ANVIL-Modul | Beziehung |
|---|---|---|
| 14 State Grammar | ANVIL State Surface (Stable/Adapting/Act Now/Failed) | Pattern-Verwandte, getrennte Implementation |
| 15 Safe Action Buffer | ANVIL Forge Patch-First Loop | Pattern-Verwandte |
| 17 AI Tool Handoff | ANVIL Bellows + Forge Tool-Bridge | Pattern-Verwandte, ANVIL-FORGE-Donor liefert konkrete Vorlage |
| 19 Warden | ANVIL Warden | Pattern-Verwandte, getrennte Implementation, gleiche Namens-Disziplin |
| 18 Work Capsule | ANVIL Artifact Output Layer | Pattern-Verwandte |

**Keine direkte Code-Sharing**, weil TAVI Android und ANVIL noch stack-offen ist. Aber die Pattern-Sprache ist konsistent — Skill-Format, Slot Contract, State Grammar.
