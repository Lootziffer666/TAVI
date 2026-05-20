# AGENTS.md — Agent-Regeln für TAVI

## Wer darf was

| Agent | Darf | Darf nicht |
|---|---|---|
| Coding-Agent (Claude, Codex, Hyperagent) | Cluster implementieren, PRs erstellen, Docs schreiben | Concept Contract ändern, neue Cluster definieren, Anti-Patterns aufweichen |
| Design-Agent (mit `flubber-design` Skill) | UI-Mockups, Tokens, Motion-Patterns vorschlagen | Anti-Pattern-Liste übergehen (kein Dashboard, kein Glow) |
| Mensch | Alles | — |

## Regeln für alle Agenten

1. **Lies zuerst:**
   - `CONCEPT_CONTRACT.md` — Sprachregeln
   - `NOT_IMPLEMENTED.md` — was TAVI nicht ist
   - `docs/CLUSTER_MAP.md` — die 19 Cluster mit Slot Contracts
   - `docs/zen-concept.md` — das Quellen-Dokument

2. **Kein AccessibilityService.** Wenn die einfachste Lösung Accessibility braucht, ist es nicht TAVI.

3. **Kein heimliches Verhalten.** Jede Aktion sichtbar, jede Speicherung erklärbar.

4. **Slot Contract Pflicht.** Neue Module/Cluster brauchen alle 7 Felder ausgefüllt.

5. **Failure Behavior ist Pflichtfeld.** Module ohne expliziten Recovery-Pfad werden nicht akzeptiert.

6. **Verbotene Begriffe vermeiden.** Siehe `CONCEPT_CONTRACT.md`.

7. **Acceptance Test reflexartig anwenden:**
   - „Riecht das nach Dashboard?" → reject
   - „Riecht das nach Productivity-Coach?" → reject
   - „Riecht das nach Daten-Sammler?" → reject
   - „Fühlt sich das wie Schwelle, Intent oder Reibungsreduktion an?" → keep

## Bei Drift

1. `NOT_IMPLEMENTED.md` lesen
2. Im Zweifel: Cluster pausieren, nicht weitermachen
3. Mensch fragen

## FLUBBER-Skill

UI-Generierung sollte den `flubber-design` Skill verwenden (Dark-first, Vollton, Lilita One / Barlow / JetBrains Mono, 10 state-driven Motion-Tokens). Siehe Skill für volle Spec.
