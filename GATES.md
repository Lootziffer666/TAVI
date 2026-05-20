# 🚪 GATES — TAVI

**Stand:** Konzept-Phase. Noch keine Gates abgeschlossen.

---

## 🌱 Geplante Gates (initial skeleton)

### Gate TV-001: Concept Contract Lock
- **Ziel:** Sprachregeln in `CONCEPT_CONTRACT.md` und `NOT_IMPLEMENTED.md` als verbindlich markieren
- **To-Dos:**
  - [x] CONCEPT_CONTRACT.md angelegt
  - [x] NOT_IMPLEMENTED.md angelegt
  - [x] zen-concept.md als Quelle dokumentiert
  - [ ] Christian gegenliest und freigibt
- **Akzeptanz:** Beide Dateien sind reviewed und marked als verbindlich
- **Kill:** Begriffsdrift in Folge-Commits

### Gate TV-002: Stack-Entscheidung
- **Branch:** `gate/tv-002-stack`
- **Ziel:** Sprache, Build-System, Test-Framework, UI-Framework festlegen
- **To-Dos:**
  - [ ] Kotlin-only vs Kotlin Multiplatform entscheiden
  - [ ] Gradle vs anderes Build-System
  - [ ] Compose vs nativ Views
  - [ ] Test-Framework wählen
  - [ ] Dependency-Injection Pattern festlegen
- **Akzeptanz:** Stack-Dokument in `docs/STACK.md`
- **Kill:** Mehrere Sprachen im selben Repo

### Gate TV-003: Opus Product Planning Map
- **Ziel:** Den Opus-Auftrag aus `docs/opus-prompt.md` durchspielen, Output als `docs/PRODUCT_PLAN.md` festhalten
- **To-Dos:**
  - [ ] Systemverständnis
  - [ ] Clusterübersicht
  - [ ] Modul-Slot-Contracts (alle 19)
  - [ ] Roadmap (Prototype now / Roadmap aufteilen)
  - [ ] Offene Fragen
  - [ ] Stärkste Prototyp-Kandidaten
  - [ ] Risiken und Gegenmaßnahmen
  - [ ] Produktformel
- **Akzeptanz:** `docs/PRODUCT_PLAN.md` ist da, von Christian gegengelesen
- **Kill:** Plan ohne Produktformel

### Gate TV-004: Erster Prototyp-Cluster
- **Ziel:** Einen einzelnen Cluster (vermutlich Snippet Capsule oder Clipboard / Transfer) als minimalen lauffähigen Prototyp implementieren
- **To-Dos:**
  - [ ] Cluster aus Top-3-Kandidaten wählen
  - [ ] Slot Contract finalisieren
  - [ ] UI mit `flubber-design` Skill
  - [ ] Failure Behavior implementiert
  - [ ] Smoke Test
- **Akzeptanz:** APK build-bar, App startet, ein Cluster funktional
- **Kill:** Mehr als 1 Cluster gleichzeitig

---

## Regel

Gate-Reihenfolge wird nicht nachträglich geändert. Neue Gates haben höhere Nummern.
