# CLAUDE.md — Spezifische Regeln für Claude / Codex / Hyperagent

## Kontext

TAVI ist eine Android-nahe Schwellen-, Intent- und Reibungsarchitektur. Konzept-Phase.

## Regeln

1. **Lies zuerst (in dieser Reihenfolge):**
   - `CONCEPT_CONTRACT.md`
   - `NOT_IMPLEMENTED.md`
   - `docs/CLUSTER_MAP.md`
   - `docs/zen-concept.md`

2. **Pure Android.** Pure Kotlin oder Kotlin Multiplatform. Kein React Native, kein Flutter, kein Compose Web-Hack.

3. **Kein Accessibility-Pfad.** Wenn du als Agent die Lösung über AccessibilityService entwickelst, hast du die falsche Lösung. Borderline ist der Accessibility-Experimentier-Raum.

4. **Slot Contract zuerst.** Bevor du Code für einen Cluster schreibst, fülle den Slot Contract aus (7 Felder).

5. **Failure Behavior implementieren.** Nicht nur dokumentieren. Code für DEGRADED/BLOCKED/FAILED states.

6. **Public-safe Labels.** Status-Strings müssen in fremder Umgebung lesbar bleiben.

7. **Anti-Dashboard.** Wenn du mehr als 1 dominantes UI-Element pro Screen baust, ist es falsch.

8. **State Grammar einhalten.** Nur die 9 erlaubten States: Idle / Ready / Capture / Intent Unclear / Risk Detected / Act Now / Blocked / Failed / Private / Fallback.

## Bei Unsicherheit

Stoppen, nicht raten. Lieber Pause als Drift.
