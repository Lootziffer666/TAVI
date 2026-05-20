<h1 align="center">🌿 TAVI</h1>

<p align="center">
  <strong>Tiny Action · Visual Intent</strong><br/>
  <em>Android-nahe Schwellen-, Intent- und Reibungsarchitektur</em>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-green?style=flat-square" />
  <img src="https://img.shields.io/badge/Stack-undecided-lightgrey?style=flat-square" />
  <img src="https://img.shields.io/badge/Status-Concept-orange?style=flat-square" />
  <img src="https://img.shields.io/badge/Sibling-Borderline%20%28donor%29-blue?style=flat-square" />
</p>

---

## 🧭 Was TAVI ist

Drei Kernbegriffe:

- **Schwelle** — der Moment zwischen Impuls und Aktion
- **Intent** — was der User eigentlich erreichen will
- **Reibung** — was zwischen User und Intent steht, was sie abbaut, was sie aufbaut

TAVI operiert auf der **Android-User-Seite**: vor der App, neben der App, statt der App. Es ist eine Schicht aus 19 Clustern, die zwischen Impuls und irreversibler Aktion eine klärende Schwelle setzen, ohne sich zum Wellbeing-Dashboard zu verwandeln.

## 🛑 Was TAVI nicht ist

- Keine IDE
- Kein OS, kein Custom ROM
- Kein Windows-Tool
- Kein Second Brain
- Kein Wellbeing-Dashboard
- Kein App-Blocker
- Keine heimliche Automation

Implementations-Anti-Patterns: kein AccessibilityService, kein AutoInput, kein Shizuku-Zwang, keine heimliche Speicherung. Volle Liste in [`NOT_IMPLEMENTED.md`](NOT_IMPLEMENTED.md).

## 🌱 Status

**Konzept-Phase.** Hervorgegangen aus dem Zen-Konzeptdokument vom Mai 2026 (siehe [`docs/zen-concept.md`](docs/zen-concept.md)). Ausgekoppelt aus [BORDERLINE](https://github.com/Lootziffer666/BORDERLINE), das auf Accessibility setzt — TAVI darf das nicht.

## 🔤 Naming

**TA** = Tiny Action. **VI** = Visual Intent.

Alternative Auflösung: **Tap + Vision**. Final entschieden später.

## 🏗 Struktur (geplant)

```
TAVI/
├── README.md              this file
├── CONCEPT_CONTRACT.md    Was TAVI ist (Sprachregeln)
├── NOT_IMPLEMENTED.md     Was TAVI nicht ist (Anti-Identitäten)
├── GATES.md               Gate-Definitionen (Skelett)
├── AGENTS.md              Agent-Regeln
├── CLAUDE.md              Claude-spezifische Regeln
└── docs/
    ├── zen-concept.md     Quellen-Konzept (16 KB original)
    ├── CLUSTER_MAP.md     19 Cluster mit Slot Contracts
    └── opus-prompt.md     Der Opus-Auftrag für die Product Planning Map
```

## 🧠 19 Cluster

Siehe [`docs/CLUSTER_MAP.md`](docs/CLUSTER_MAP.md). Kurzliste:

| # | Cluster | Roadmap |
|---|---|---|
| 1 | Clipboard / Transfer Layer | Prototype now |
| 2 | Snippet Capsule | Prototype now |
| 3 | QuickActions | Prototype now |
| 4 | Image-as-Intent / Capture | Prototype now |
| 5 | Handoffs | Prototype now |
| 6 | Intent Controller | Roadmap |
| 7 | Notification Sifter | Roadmap |
| 8 | Abo-Alarm / Subscription Trap Detector | Prototype now |
| 9 | Psychotricks / Manipulationsmuster | Prototype now |
| 10 | Game / Kids / Supervision | Roadmap |
| 11 | App Fossil Finder / Smart App Inventory | Prototype now |
| 12 | Zen Shell / Launcher Rooms | Roadmap |
| 13 | Overlay / Handles / Gesture Edge | Roadmap |
| 14 | State Grammar / One Anchor | Prototype now |
| 15 | Safe Action Buffer | Prototype now |
| 16 | Desire Queue / Want Shelf | Roadmap |
| 17 | AI / Tool Handoff | Prototype now / Roadmap |
| 18 | Work Capsule / Artifact | Prototype now |
| 19 | Privacy / Control / Warden | Prototype now |

## 🔗 Verwandt

| Projekt | Beziehung |
|---|---|
| [ANVIL](https://github.com/Lootziffer666/ANVIL) | Geteiltes Vokabular (Warden, State Grammar, Slot Contract). Parallele Domäne (Coder-IDE). NICHT Teil von TAVI. |
| [BORDERLINE](https://github.com/Lootziffer666/BORDERLINE) | Donor für Snippet/Clipper-Patterns. Code-Pfad ist getrennt (Borderline mit Accessibility, TAVI ohne). |
| [FLUBBER](https://github.com/Lootziffer666/FLUBBER) | Design-Sprache (als Skill installiert). |

---

<p align="center"><em>Schwelle. Intent. Reibung. Räume statt App-Salat.</em></p>
