# TAVI Concept Contract

**Stand:** 2026-05-20
**Status:** Verbindlich (für Konzeptphase)
**Quelle:** [`docs/zen-concept.md`](docs/zen-concept.md)

---

## Kanonische Begriffe

### TAVI

Android-nahe Schwellen-, Intent- und Reibungsarchitektur. Pure Android-App ohne System-Privilegien — operiert über Share Intents, Notifications, Clipboard API und optionalen Power-Adapter (Shizuku) für Fortgeschrittene.

### Schwelle

Der Moment zwischen Impuls und Aktion. Wo TAVI eingreift: bevor der User in eine App fällt, bevor er einen Wunsch in einen Klick verwandelt, bevor eine riskante Aktion irreversibel wird.

### Intent

Was der User eigentlich erreichen will. TAVI fragt nicht „welche App?", sondern „welche Aktion?".

### Reibung

Was zwischen User und Intent steht. TAVI baut sie ab, wenn sie hinderlich ist, und baut sie auf, wenn sie schützt.

### Cluster

Eine funktionale Familie. 19 Cluster sind definiert (Clipboard / Transfer Layer, Snippet Capsule, QuickActions, …). Siehe [`docs/CLUSTER_MAP.md`](docs/CLUSTER_MAP.md).

### Slot Contract

Standardisiertes 7-Feld-Schema pro Modul/Cluster:

| Feld | Inhalt |
|---|---|
| Name | Eindeutiger Modul-Name |
| Purpose | Kernnutzen in einem Satz |
| User State | In user's words, was der User gerade denkt |
| Input | Was das Modul akzeptiert |
| Output | Was das Modul liefert |
| Visible Surface | Wo der User es sieht (Mini-Panel, Share Target, …) |
| Failure Behavior | Was passiert, wenn es scheitert |

### One Anchor per Moment

Jeder Zustand hat eine führende Wahrheit. Keine konkurrierenden Primär-Elemente.

### Visible Surface

Wo das Modul für den User sichtbar wird. Mini-Panel, Share Target, Edge Handle, Capture Panel — nicht Vollbild-App.

### Failure Behavior

Was passiert, wenn das Modul scheitert. Pflichtfeld pro Slot Contract. Default: ruhig erklären, ein Recovery-Pfad, letzter sicherer Stand bleibt erhalten.

### Public-safe Label

Status-Anzeige, die in fremder Umgebung lesbar bleibt. Keine internen Codes, keine Beleidigung des Users.

---

## State Grammar

Erlaubte Zustände eines TAVI-Moduls:

| State | Bedeutung |
|---|---|
| `Idle` | Modul ist da, aber inaktiv |
| `Ready` | Modul wartet auf Input |
| `Capture` | Modul nimmt etwas auf |
| `Intent Unclear` | Modul weiß nicht, was der User will |
| `Risk Detected` | Modul hat ein Risiko erkannt (Abo, Manipulation, irreversible Aktion) |
| `Act Now` | Modul fordert User zur Handlung auf |
| `Blocked / Failed` | Modul kann nicht weiterarbeiten |
| `Private` | Modul arbeitet im Private Mode (keine Persistenz) |
| `Fallback` | Modul ist in Recovery |

---

## Verbotene Begriffe

| Begriff | Grund | Korrekte Alternative |
|---|---|---|
| Dashboard | Anti-Dashboard-Prinzip | Surface, Panel, Anker |
| App Blocker | Negatives Framing | Schwelle, Preflight |
| Wellbeing | Klingt nach Selbstoptimierung | Reibung, Intent |
| Productivity | Klingt nach Hustle-Kultur | Reibung, Intent |
| Helper | Zu unkonkret | konkretes Modul nennen |
| Smart | Hohle Marketing-Floskel | Konkrete Capability beschreiben |

---

## Abgrenzungen

### TAVI ≠ ANVIL

TAVI ist Android-Smartphone-Tool für End-User. ANVIL ist Coder-IDE-Werkbank.
Geteiltes Vokabular (Warden, State Grammar, Slot Contract, Safe Action Buffer als Forge-Äquivalent), aber **getrennte Produkte, getrennte Repos**.

### TAVI ≠ BORDERLINE

BORDERLINE ist Accessibility-Overlay-Experiment. TAVI darf **nicht** auf Accessibility setzen. BORDERLINE bleibt als Donor für Snippet/Clipper-Patterns, der TAVI-Code-Pfad ist eigenständig.

### TAVI ≠ Mobile-Geschwister

TAVI teilt mit anderen Mobile-Projekten die Design-Sprache (siehe `flubber-design` Skill), aber nicht den Code.

---

## Mini-Glossar

- **Capture** — wenn TAVI etwas vom User entgegennimmt (Screenshot, Text, Clipboard).
- **Handoff** — wenn TAVI etwas an eine andere App/Tool/Modell übergibt.
- **Preflight** — die Schwelle vor einer Aktion. Klärt, was der User wirklich will.
- **Recovery** — die kontrollierte Rückkehr aus einem Failure-Zustand.
- **Surface** — die User-sichtbare Oberfläche eines Moduls.

---

## Diese Datei ändert sich nur durch Gate-Commit.

Begriffsänderungen sind keine Implementationsdetails. Sie sind Verträge.
