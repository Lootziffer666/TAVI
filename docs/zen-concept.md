Ich habe hier ein paar kleine Ideen zu User journey, flow, digital wellbeing, stress relief und digital awareness bezüglich der aktuellen Problematik von Smartphone-UX aus einem Brainstorming und aus meinen anderen Projekten abgeleitet zusammengetragen. Ich möchte, dass Du diese Features mit den wissenschaftlichen Arbeiten abgleichst und mir granulare, verständliche und sauber strukturierte Evidenzen für meine Philosophie des Konzepts mit Zitationen lieferst, die Muster bennenst und zu jedem Cluster weitere Ideen oder Muster ergänzt, die das Projekt abrunden oder erweitern. Nenne danach auch zu jedem Cluster zu bedenkende Risiken und entsprechende Alternativen.

Zen ist:

> Android-nahe Schwellen-, Intent- und Reibungsarchitektur.



Nicht:

> IDE, OS, Custom ROM, Windows-Tool, Second Brain, Wellbeing-Dashboard, App-Blocker, heimliche Automation.




---

Cluster, die Opus planen soll

1. Clipboard / Transfer

Ideen

Clipboard+

Clipboard-Historie

große Texte / Artefakte vollständig halten

Dateiverweis statt Volltext

Clipboard-Status

Text, Bild, Datei, Link, Markdown, Prompt unterscheiden

Private Mode

Cleaner

Pinboard


Slot Contract

Feld	Inhalt

Name	Clipboard / Transfer Layer
Purpose	Inhalte zwischen Apps, Aktionen und Zuständen verlustfrei bewegen
User State	„Ich habe etwas und will es weiterverwenden“
Input	Text, Link, Datei, Bild, Screenshot, Markdown
Output	Clipboard-Inhalt, Datei, Verweis, Status
Visible Surface	kleines Panel, Share Target, später Launcher-Fläche
Failure Behavior	nichts kürzen; klar sagen, was passiert ist


Roadmap-Status: Prototype now


---

2. Snippets / Prompt-Bausteine

Ideen

Prompt-Snippets

Projekt-Snippets

Antwort-Snippets

Markdown-Snippets

Recovery-Snippets

Agenten-Aufträge

Kategorien

Favoriten

Suche

Varianten

Snippet aus Auswahl/Chat erzeugen


Slot Contract

Feld	Inhalt

Name	Snippet Capsule
Purpose	wiederkehrende Texte und Arbeitsbausteine griffbereit machen
User State	„Ich brauche denselben Baustein wieder“
Input	Text, Auswahl, Chat-Output, Datei
Output	kopierter/speicherbarer Baustein
Visible Surface	Snippet Panel, Suchfläche, Favoriten
Failure Behavior	Baustein bleibt als Datei/Eintrag verfügbar


Roadmap-Status: Prototype now


---

3. QuickActions / Kontextaktionen

Ideen

Adresse → Maps / CatchIt / kopieren

Telefonnummer → anrufen / speichern

Datum/Uhrzeit → Kalender / Erinnerung

Betrag/IBAN → kopieren / prüfen / merken

Link → öffnen / speichern / zusammenfassen

Rezept → Einkaufsliste / Notiz

Rechnung → Betrag / Fälligkeit / PDF

Code/Fehler → erklären / suchen / Agentenprompt


Slot Contract

Feld	Inhalt

Name	QuickActions
Purpose	aus erkanntem Kontext kurze sinnvolle Aktionen machen
User State	„Hier steht etwas, was eigentlich eine Handlung ist“
Input	Text, Clipboard, Share, später Screenshot
Output	kleine Aktionsauswahl
Visible Surface	Mini-Panel, Kontextmenü
Failure Behavior	keine sichere Aktion erkannt → manuelle Auswahl


Roadmap-Status: Prototype now


---

4. Image-as-Intent / Capture

Ideen

Screenshot als Handlung

Dokumentfoto → OCR / PDF / Zusammenfassung

Rechnungsscreenshot → Betrag / IBAN / Fälligkeit

Chat-Screenshot → Antwort / Termin / Aufgabe

Produktlabel → Seriennummer / Notiz

Paywall-Screenshot → Abo-Risiko

Fehlerseite → Diagnoseprompt

Bild schwärzen

Bild zu Text

Bild zu PDF


Slot Contract

Feld	Inhalt

Name	Image-as-Intent / Capture Layer
Purpose	Bilder nicht sammeln, sondern in nächste Aktionen verwandeln
User State	„Ich habe ein Bild, aber brauche den nächsten Schritt“
Input	Screenshot, Foto, Bilddatei
Output	Text, PDF, Aktionsvorschläge, Schwärzung
Visible Surface	Capture Panel, Share Target
Failure Behavior	Bild bleibt unverändert; Ergebnis als unsicher markieren


Roadmap-Status: Prototype now / Early Roadmap


---

5. Handoffs

Ideen

Text an ChatGPT / Claude / Gemini

Prompt an Hyperagent

Datei an Ziel-App

Adresse an Maps / CatchIt

Screenshot an OCR

Fehler an Suche

Code an Agent

Markdown an Speicherort

Link an Leseliste

Text als Snippet speichern


Slot Contract

Feld	Inhalt

Name	Handoff Layer
Purpose	Zustand an das richtige Werkzeug übergeben
User State	„Ich will nicht App suchen, sondern Ziel erreichen“
Input	Text, Bild, Datei, Link, Prompt
Output	Übergabe an App/Tool/Modell/Speicherort
Visible Surface	Handoff-Auswahl, Share Target
Failure Behavior	Inhalt bleibt erhalten, alternative Übergabe anbieten


Roadmap-Status: Prototype now


---

6. Intent Controller

Ideen

App-Start mit Zielklärung

Impulsbremse

Doomscroll-Warnung

Moduswahl vor App

Share-Intent-Klärung

Bild-/Text-Intent-Klärung

Entscheidung merken

Absicht statt App


Slot Contract

Feld	Inhalt

Name	Intent Controller
Purpose	zwischen Impuls und Aktion eine klärende Schwelle setzen
User State	„Ich öffne etwas, aber vielleicht ohne klares Ziel“
Input	App-Start, Share, Shortcut, Kontext
Output	weiterlassen, fragen, parken, umlenken
Visible Surface	Preflight, Launcher-Schicht
Failure Behavior	im Zweifel weiterlassen, nicht blockieren


Roadmap-Status: Roadmap


---

7. Notification Sifter

Ideen

kritisch sofort

menschliche Nachrichten

Werbung bündeln

Systemnoise sammeln

Abo-/Zahlungshinweise hervorheben

Game-Lockreiz dämpfen

Streak-Druck erkennen

Digest

Herkunft erklären

ruhige Fenster


Slot Contract

Feld	Inhalt

Name	Notification Sifter
Purpose	Benachrichtigungen nach Bedeutung statt Lautstärke sortieren
User State	„Nicht alles, was piept, verdient mich“
Input	Notification, App, Zeit, Kontext
Output	zeigen, bündeln, später, ignorieren, erklären
Visible Surface	Digest, kurzer Status, Launcher-Hinweis
Failure Behavior	wichtige Dinge nicht verschlucken


Roadmap-Status: Roadmap


---

8. Abo-Alarm / Subscription Trap Detector

Ideen

Off-Play-Billing erkennen

Probeabo prüfen

Preisintervall sichtbar machen

Kündigungsweg prüfen

Paywall-Screenshot analysieren

Dark-Pattern-Hinweise

„Nur heute“-Druck

Kleingedrucktes extrahieren

Abo-Merker

Kündigungs-Erinnerung

Risk Label


Slot Contract

Feld	Inhalt

Name	Abo-Alarm
Purpose	Abo-, Billing- und Trial-Fallen sichtbar machen
User State	„Hier will mich etwas binden oder kostenpflichtig machen“
Input	Screenshot, Text, Link, Store-Hinweis
Output	Warnung, Risiko, Merker, Reminder
Visible Surface	Paywall-Prüfer, Capture Panel
Failure Behavior	„unklar“ statt falsches Urteil


Roadmap-Status: Prototype now / Roadmap


---

9. Psychotricks / Manipulationsmuster

Ideen

Daily Rewards

Streaks

Lootboxen

Gacha

Battle Pass

Energy-Systeme

FOMO

Comeback-Belohnung

aggressive Pushs

Kaufdruck

Manipulationslexikon

Kinderhinweis

Reflexionsfrage


Slot Contract

Feld	Inhalt

Name	Psychotricks
Purpose	manipulative Mechaniken benennen, ohne moralisch zu blockieren
User State	„Diese App zieht an mir“
Input	Screenshot, App, Notification, Beschreibung
Output	Mustername, Erklärung, nächste Option
Visible Surface	Prüfer, Lexikon, Game-Preflight
Failure Behavior	als unklar markieren


Roadmap-Status: Prototype now / Roadmap


---

10. Game / Kids / Supervision

Ideen

Supervised Game Drawer

Spiel-Preflight

Session-Timer

Kinderprofil

Elternkuratierung

PEGI-/USK-Gründe anzeigen

Trailer-Button

lokaler Spielkatalog

weiches Spielende

Debrief

Coop/Singleplayer-Hinweis

Zeitfenster

Manipulationshinweise


Slot Contract

Feld	Inhalt

Name	Supervised Game Drawer
Purpose	Spiele bewusst starten und sauber verlassen
User State	„Ich / mein Kind will spielen, aber nicht versinken“
Input	Spiel, Profil, Zeit, Freigabe, Risiko
Output	Start, Timer, Hinweis, Debrief
Visible Surface	Game Room, Kinderfläche
Failure Behavior	nicht hart abstürzen; normale App bleibt erreichbar


Roadmap-Status: Roadmap


---

11. App Fossil Finder / Smart App Inventory

Ideen

installierte Apps semantisch sortieren

nie gestartete Apps markieren

Installationsgrund rekonstruieren

App als Inspiration / Kandidat / Müll / Projektbezug markieren

App als Manipulator markieren

App als kinderrelevant markieren

Teststatus

Löschvorschläge

„Warum ist das hier?“-Ansicht


Slot Contract

Feld	Inhalt

Name	App Fossil Finder
Purpose	App-Salat in nachvollziehbare Bedeutung verwandeln
User State	„Ich weiß nicht mehr, warum diese App da ist“
Input	App-Liste, Installationsdatum, Nutzung, Nutzer-Notiz
Output	Kategorie, Grund, Status, Vorschlag
Visible Surface	App-Inventar, Aufräumraum
Failure Behavior	unklar markieren, niemals blind löschen


Roadmap-Status: Prototype now / Roadmap


---

12. Launcher / Rooms / Zen Shell

Ideen

Homescreen als ruhige Fläche

App-Start mit Schwelle

Intent-first Start

Module im Homescreen

Game Drawer

Tool Drawer

Fossil Drawer

seltene Apps ausblenden

Kontext-Homescreen

Kinder-Homescreen

Fokus-Homescreen ohne Dashboard-Look

Projekt-Homescreen

Homescreen als Schwellenraum


Slot Contract

Feld	Inhalt

Name	Zen Shell
Purpose	Android-Einstiege als Räume statt App-Salat organisieren
User State	„Ich betrete einen digitalen Raum“
Input	App-Inventar, Module, Kontext, Nutzerzustand
Output	ruhige Startfläche, Räume, passende Einstiege
Visible Surface	Launcher / Homescreen
Failure Behavior	normaler App-Zugriff bleibt möglich


Roadmap-Status: Roadmap


---

13. Overlay / Handles / Gesten

Ideen

Edge Handles

vier Einstiegspunkte

Keyboard-Snapping

Swipe statt Button

Einhandbedienung

Kontext-Handle

Handle nur zeigen, wenn nützlich

Swipe-Richtung als Bedeutung

Press-and-hold

Flick-Geste

Mini-Panel

Werkzeugkante


Slot Contract

Feld	Inhalt

Name	Gesture Edge
Purpose	häufige Aktionen per Muskelgedächtnis erreichbar machen
User State	„Ich will eine Mini-Aktion ohne App-Wechsel“
Input	Swipe, Press, Flick, Kontext
Output	kleines Panel, Aktion, Handoff
Visible Surface	Randgriff / Werkzeugkante
Failure Behavior	Handle deaktivieren, normale App bleibt nutzbar


Roadmap-Status: Roadmap


---

14. State Grammar / One Anchor

Ideen

Idle

Ready

Capture

Intent Unclear

Risk Detected

Act Now

Blocked / Failed

Private

Fallback

One Anchor per Moment

Public-safe Labels

Recovery ohne Drama


Slot Contract

Feld	Inhalt

Name	Zen State Grammar
Purpose	jedem Moment eine lesbare führende Wahrheit geben
User State	„Was verlangt dieser Zustand gerade?“
Input	Ereignis, Modul, Risiko, Kontext
Output	Zustand, Anchor, erlaubte Optionen
Visible Surface	alle Zen-Flächen
Failure Behavior	ruhig erklären, ein Recovery-Pfad


Roadmap-Status: Prototype now


---

15. Safe Action Buffer

Ideen

riskante Aktionen parken

Entwurf → Prüfung → Ausführung

Before/After Review

Permission Mirror

Failure Explainer

Blast-Radius Control

Work Capsule

Agent Budget Governor


Slot Contract

Feld	Inhalt

Name	Safe Action Buffer
Purpose	irreversible oder riskante Aktionen prüfbar machen
User State	„Das könnte Folgen haben“
Input	senden, löschen, kaufen, posten, installieren, Agent starten
Output	Entwurf, Prüfung, Ausführung, Abbruch
Visible Surface	Preflight, Review, Capsule
Failure Behavior	letzter sicherer Stand bleibt erhalten


Roadmap-Status: Prototype now / Roadmap


---

16. Desire Queue / Want Shelf

Ideen

Wunsch parken

App installieren später prüfen

Kaufdruck dämpfen

Rabatt/FOMO sichtbar machen

Angebot speichern

Opportunity Sifter

Purchase Preflight

Decision Receipt


Slot Contract

Feld	Inhalt

Name	Desire Queue
Purpose	Wunsch von Druck trennen
User State	„Ich will das gerade, aber vielleicht wegen Druck“
Input	Produkt, App, Abo, Angebot, Link
Output	parken, später prüfen, bewusst ausführen
Visible Surface	Want Shelf, Purchase Preflight
Failure Behavior	Wunsch nur parken, nicht bewerten


Roadmap-Status: Roadmap


---

17. AI / Tool Handoff

Ideen

Modellwahl pro Aufgabe

Prompt vorbereiten

Agentenauftrag bauen

Repo-Kontext anhängen

Output speichern

Output vergleichen

Recovery Prompt

Kostenhinweis

Credit-Bewusstsein

No-Slop-Filter

Model Notes


Slot Contract

Feld	Inhalt

Name	Tool Handoff / Agent Governor
Purpose	AI-/Tool-Nutzung bewusst und zielgerichtet starten
User State	„Ich brauche ein Modell oder einen Agenten, aber sauber begrenzt“
Input	Ziel, Kontext, Budget, Dateien, Modell
Output	Prompt, Auftrag, Handoff, gespeicherter Output
Visible Surface	AI Panel, Work Capsule
Failure Behavior	Auftrag bleibt als Entwurf erhalten


Roadmap-Status: Prototype now / Roadmap


---

18. Artifact / Work Capsule

Ideen

Markdown-Artefakte speichern

Artefakt zu Clipboard

Artefakt zu GitHub

Quelle markieren

Naming-Schema

FLOWINPUT-Export nur außerhalb Zen-Kontext

Dokument-Fallback

Spec-Sammlung

Raw/Final/Discarded

Quelle behalten

PDF/DOCX/MD-Umwandlung

Repo-ready Ausgabe


Slot Contract

Feld	Inhalt

Name	Work Capsule
Purpose	laufende Arbeitszustände einfrieren
User State	„Ich darf diesen Stand nicht verlieren“
Input	Text, Datei, Chat-Output, Status, Handoff
Output	Capsule, Markdown, Datei, Verweis
Visible Surface	Work Room, Artifact Panel
Failure Behavior	Datei/Verweis statt Verlust


Roadmap-Status: Prototype now / Roadmap


---

19. Privacy / Control / Warden

Ideen

Emergency Off

Private Mode

Delete All

App-Blacklist

Sensitive App Mode

sichtbarer Speicherstatus

sichtbarer Berechtigungsstatus

Audit-Ansicht

Modul einzeln abschalten

lokale Kontrolle

Schwärzen vor Teilen


Slot Contract

Feld	Inhalt

Name	Zen Warden
Purpose	Nutzerkontrolle, Löschung, Sichtbarkeit und Modulgrenzen sichern
User State	„Ich muss wissen und kontrollieren, was Zen tut“
Input	Module, Speicher, Rechte, App-Kontext
Output	ausschalten, löschen, privat, prüfen
Visible Surface	Provisioning / Control Surface
Failure Behavior	Modul abschalten, Zen-Kern bleibt nutzbar


Roadmap-Status: Prototype now / Roadmap


---

Bereinigte Roadmap statt Kill-Liste

Prototype now

Clipboard / Transfer Layer

Snippet Capsule

Handoffs

QuickActions aus Text/Clipboard

Image-as-Intent manuell

Abo-Alarm als Screenshot/Text-Prüfer

Psychotricks als Lexikon/Prüfer

App Fossil Finder als Inventar/Review

State Grammar / One Anchor

Safe Action Buffer

Work Capsule

Privacy / Control Surface


Roadmap

Intent Controller

Notification Sifter

Game / Kids / Supervision

Zen Shell / Launcher Rooms

Gesture Edge / Handles

Desire Queue / Want Shelf

Tool Handoff / Agent Governor als stärker integrierte Schicht

Activity Reflection ohne Dashboard-Look

Opportunity Sifter

Shared Context / Family Layer



---

Opus-Auftrag, sauber formuliert

Wir planen Zen.

Wichtig:
Zen ist ein Android-nahes System für Schwellen, Intent, Reibungsreduktion und kontextuelle Handlung.
Zen ist kein echtes OS, kein Custom ROM, keine IDE, kein Windows-Tool, kein Digital-Wellbeing-Dashboard und kein heimliches Automationssystem.

Nicht in Zen einplanen:
- LOOM / FLOW / SPIN / SMASH
- ANVIL als Systembestandteil
- Custom ROM
- AccessibilityService als Pfad
- AutoInput
- heimliche Speicherung, heimliche Analyse oder heimliches Handeln
- Digital-Wellbeing-Dashboard-Look
- Shizuku als Voraussetzung oder Kernarchitektur

Shizuku darf höchstens als optionaler Power-Adapter erwähnt werden, aber nicht als Planungsachse.

Ziel:
Plane Zen frei und produktnah ausgehend von diesen Clustern:

1. Clipboard / Transfer Layer
2. Snippet Capsule
3. QuickActions
4. Image-as-Intent / Capture
5. Handoffs
6. Intent Controller
7. Notification Sifter
8. Abo-Alarm
9. Psychotricks / Manipulationsmuster
10. Game / Kids / Supervision
11. App Fossil Finder / Smart App Inventory
12. Zen Shell / Launcher Rooms
13. Overlay / Handles / Gesture Edge
14. State Grammar / One Anchor
15. Safe Action Buffer
16. Desire Queue / Want Shelf
17. AI / Tool Handoff
18. Artifact / Work Capsule
19. Privacy / Control / Warden

Für jeden Cluster:
- beschreibe den Kernnutzen
- beschreibe die Nutzer-Situation
- beschreibe mögliche Oberflächen
- beschreibe sinnvolle Android-Andockpunkte ohne Accessibility und ohne Custom ROM
- beschreibe Risiken
- beschreibe Abhängigkeiten zu anderen Zen-Clustern
- beschreibe eine natürliche Entwicklungsreihenfolge
- verschiebe nicht sofort gebaute Ideen in eine Roadmap, statt sie zu verwerfen

Keine Gates.
Keine MVP-Restriktionsliste.
Keine fremden Projekte in Zen hineinziehen.
Keine Dashboard-Logik.
Keine heimliche Automationslogik.

Output:
Erstelle eine klare Zen Product Planning Map mit:
1. Systemverständnis
2. Clusterübersicht
3. Modul-Slot-Contracts
4. Roadmap
5. offene Fragen
6. stärkste Prototyp-Kandidaten
7. Risiken und Gegenmaßnahmen
8. eine knappe Produktformel

Das ist jetzt sauberer: Zen bleibt Zen.
Android, Schwellen, Intent, Reibung, Räume. Kein Fremdsystem-Ballast.
