# TAVI — Was es nicht ist

> Anti-Identitäten und Implementations-Anti-Patterns für TAVI.
> Diese Datei ist genauso wichtig wie der Concept Contract. Sie ist der Reject-Test, gegen den jede neue Entscheidung geprüft wird.

---

## Identitäts-Anti-Patterns

TAVI ist **nicht**:

- Keine IDE
- Kein OS, kein Custom ROM
- Kein Windows-Tool, kein Desktop-Programm
- Kein Second Brain
- Kein Wellbeing-Dashboard
- Kein App-Blocker
- Keine heimliche Automation
- Kein Hustle-Productivity-Tool
- Kein Daten-Sammler

---

## Implementations-Anti-Patterns

Auch wenn theoretisch möglich, **TAVI darf nicht** auf folgenden Pfaden gebaut werden:

- **Kein AccessibilityService als Pfad** — das ist Borderline-Territorium, dort bewusst experimentell. TAVI muss ohne Accessibility funktionieren.
- **Kein AutoInput** — keine simulierte User-Eingabe.
- **Kein Shizuku als Voraussetzung oder Kernarchitektur** — höchstens optionaler Power-Adapter für fortgeschrittene Nutzer.
- **Kein Custom ROM**, kein gerooteter Voraussetzungs-Pfad.
- **Kein Digital-Wellbeing-Dashboard-Look** — keine Charts, keine Tages-Score, keine Streak-Meter.
- **Keine heimliche Speicherung, heimliche Analyse oder heimliches Handeln** — jede Aktion ist sichtbar.

---

## Bestandteil-Anti-Patterns

Diese Projekte sind **nicht Teil von TAVI** und dürfen nicht reingezogen werden:

- LOOM / FLOW / SPIN / SMASH (Writing-Stack)
- ANVIL als Systembestandteil (TAVI nutzt nicht den ANVIL-Core)
- Fremde Wellbeing-Frameworks
- Fremde „Habit-Tracking"-Libraries

---

## Begründungen

### Warum kein AccessibilityService?

Android setzt aus Sicherheits- und UX-Gründen harte Grenzen rund um Accessibility. Ein „macht überall alles friktionslos"-Monster wäre unseriös und brüchig. Auch: Accessibility-Apps brauchen wiederholte User-Bestätigung, die der Reibungs-Reduktions-Idee von TAVI direkt widerspricht.

### Warum kein heimliches Hintergrund-Verhalten?

TAVI ist **Schwellen-Architektur**. Wenn TAVI heimlich hinter dem User Dinge tut, verschiebt es Schwellen, statt sie sichtbar zu machen. Das ist das Gegenteil des Konzepts.

### Warum kein Wellbeing-Dashboard?

Wellbeing-Apps optimieren den User. TAVI **respektiert den User-Intent** und reduziert Reibung. Ein Dashboard wäre eine Meta-App, die selbst Reibung erzeugt.

---

## Recovery-Pattern

Wenn eine TAVI-Aktion scheitert:

1. **Quarantäne** des Moduls für den aktuellen Run
2. **Unresolved Step** an User oder Chat übergeben
3. **Resume from last safe checkpoint** nach Resolution
4. **Public-safe Label** statt interner Codes

Pattern aus ANVIL-Canon übernommen.

---

*„Reale systemweite Automationslogik pro App: bewusst nicht umgesetzt. Android setzt aus Sicherheits- und UX-Gründen harte Grenzen. Ein 'macht überall alles friktionslos'-Monster wäre unseriös und brüchig."* — Borderline `NOT_IMPLEMENTED.md`, sinngemäß auf TAVI übertragen.
