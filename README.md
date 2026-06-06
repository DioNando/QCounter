# Anh — Android natif (Kotlin + Jetpack Compose)

> *Anciennement « QCounter » (package `ma.wave.qcounter`). Désormais l'`applicationId` **et** le package sont `ma.anh.app`.*

Anh est une application Android de **suivi comportemental conversationnel**. Pendant un
échange (entretien, négociation, discussion délicate…), trois boutons permettent d'enregistrer
**comment l'interlocuteur·rice répond** :

- une **réponse directe** (clair, assumé) ;
- une **question renvoyée par une question** (esquive) ;
- un **« je ne sais pas »** (indécision / évitement).

À partir de ces appuis horodatés, l'app calcule **en temps réel** des indicateurs (KPI) et propose
des visualisations, un historique et des graphiques temporels.

## 🎯 À quoi ça sert ?

On a souvent une impression subjective (« elle ne répond jamais franchement », « il botte toujours
en touche ») sans pouvoir l'objectiver. Anh transforme cette impression en **données** :

- **Quantifier** une tendance plutôt que la deviner — combien de fois, dans quelles proportions.
- **Repérer des schémas** : pics d'esquive, créneaux horaires, évolution dans le temps.
- **Débriefer** après coup grâce à un historique horodaté et des graphiques.
- **Saisie discrète et rapide** : panneau au pouce, widget d'écran d'accueil, raccourcis, tuile
  Quick Settings et annulation par secousse — pour compter sans casser le fil de la conversation.

C'est un outil d'**observation personnelle**, pas un jugement : les libellés et l'interprétation
restent à la main de l'utilisateur.

---

## 📊 Les indicateurs (KPI)

Tout est dérivé d'un seul total et des trois compteurs (Directe `d`, Question `q`, Esquive `u`),
avec `total = d + q + u`.

| Indicateur | Formule | Lecture |
|---|---|---|
| **Volume Global** | `total` | Nombre total d'interactions enregistrées. |
| **Ratio de Clarté (RC)** | `d / total` | % de réponses directes. Plus c'est haut, plus la personne répond franchement. |
| **Taux d'Esquive Linéaire (TEL)** | `q / total` | % de questions renvoyées par une question. Mesure l'esquive « en miroir ». |
| **Taux d'Indécision** | `u / total` | % de « je ne sais pas ». |

### Indice de Clarté (score synthétique 0–100)

Un score unique qui résume la tendance globale :

```
Indice = (d × 1,0 + u × 0,3 + q × 0,0) / total × 100
```

Une réponse directe vaut son plein crédit, une indécision compte partiellement (moins évasive
qu'un renvoi), une question renvoyée ne crédite rien. Le score est accompagné d'une **interprétation**
qualitative :

| Score | Palier | Interprétation |
|---|---|---|
| ≥ 80 | Très clair | Répond presque toujours de façon directe et assumée. |
| ≥ 60 | Plutôt clair | Tendance nette à répondre clairement. |
| ≥ 40 | Mitigé | Alterne entre réponses claires et esquives. |
| ≥ 20 | Évasif | Esquive ou renvoie souvent les questions. |
| < 20 | Très évasif | Évite presque systématiquement de répondre directement. |

### Séries (streaks)

L'app suit la **série en cours** (suite de réponses consécutives du même type, ex. « ×5 Directe »)
et le **record** (plus longue série jamais atteinte). La page Graphiques détaille le **record par type**.

### Polarité Oui / Non

En plus de la *manière*, on peut noter la *polarité* d'une réponse directe (**Oui** / **Non**). Un
Oui/Non **compte comme une réponse directe** (Volume, RC, clarté) ; en parallèle, une **carte ratio**
montre la proportion Oui vs Non. C'est une qualification, pas une catégorie séparée → aucun double comptage.

---

## ✨ Fonctionnalités

**Saisie & accueil**
- **Trois actions de saisie instantanée** (libellés personnalisables) avec compteurs animés, cartes
  colorées et retour haptique ; panneau ancré en bas, accessible au pouce.
- **Boutons Oui / Non** sous « Réponse Directe » : ce sont des **réponses directes qualifiées**
  (comptées comme directes dans les KPI), avec une **carte Ratio Oui/Non** dédiée.
- **4ᵉ catégorie optionnelle** activable dans les réglages (libellé + couleur) — **neutre** dans les KPI.
- **Visualisation au choix** : **pictogramme waffle**, **anneau (donut)** ou **anneaux d'activité**,
  avec remplissage animé, légende et pourcentages KPI compacts.
- **Légende interactive** : toucher un type dans la légende du héro le **met en avant** et **estompe**
  les autres dans le graphique (re-toucher pour tout réafficher).
- **Indice de clarté** (avec emoji reflétant le score) et **séries** (en cours / record) en cartes dédiées.
- **Emoji d'humeur** évolutif selon le comportement dominant **et son intensité** : 4 **jeux d'emojis**
  (Classique, Expressif, Animaux, Météo) et 3 niveaux de **sensibilité** (Subtil / Normal / Marqué).

**Historique & graphiques**
- **Historique horodaté** persistant, **paginé** (100 entrées, bouton « Afficher plus »), avec **mode
  sélection multiple** (appui long → suppression, confirmation et annulation).
- **Page Graphiques** : **records de séries par type** (plus longue série de chaque type), **heatmap**
  d'activité par heure × jour de la semaine, **activité par jour** et **activité par heure** (barres).
- **Réinitialisation** complète depuis l'accueil ou l'historique.

**Annulation & confidentialité**
- **Annulation** par **bouton flottant éphémère** (apparaît après chaque saisie, disparaît au bout de
  quelques secondes) **et par secousse** (shake-to-undo, avec retour haptique).
- **Mode discret** : un appui sur l'icône « œil » masque instantanément tout le contenu derrière un
  écran neutre. Tant qu'il est actif, **`FLAG_SECURE`** est posé (contenu masqué dans l'aperçu des
  apps récentes et captures d'écran bloquées), et le réaffichage exige un **déverrouillage
  biométrique** (`BiometricPrompt` ; réaffichage direct si aucune biométrie n'est configurée).
  Le verrou est **persisté** (DataStore) : si l'app est fermée alors qu'elle est en mode discret,
  elle **rouvre verrouillée** (aucun flash de l'accueil — rien ne s'affiche avant le chargement de
  l'état), et il faut toucher l'écran pour lancer le déverrouillage biométrique.

**Personnalisation**
- **Couleurs dynamiques Material You** (Android 12+) activables en option.
- **5 palettes** d'indicateurs : **Soleil** (marque, par défaut) / Lagon / Ciel / Forêt / Crépuscule.
- **Libellés des boutons** personnalisables (longs et courts), 4ᵉ catégorie incluse.

**Hors application**
- **Widget d'écran d'accueil** en **4 variantes** : complet (titre + total + 3 boutons), compact sur une
  ligne (total + 3 boutons), **2×1** (total + Directe & Question) et **1×1** (bouton Question). **Fond
  Material You**, boutons aux **couleurs de marque**. La **1×1 est configurable** (action au choix).
- **Icônes de raccourcis distinctes** par action (bulle colorée : Directe / Question / Esquive).
- **Raccourcis du lanceur** (appui long sur l'icône) pour enregistrer une action sans ouvrir l'app.
- **Tuile Quick Settings** pour compter une « Question » depuis le volet des réglages rapides.

**Données**
- **Export / Import** en JSON, depuis la barre du haut de l'**Historique** (icônes ⬆ / ⬇). L'import
  **fusionne** sans écraser ni créer de doublon (dédup sur `type + horodatage`).

Réglages persistés via **DataStore** ; accessibles depuis l'icône engrenage de l'accueil.

---

## 🧱 Stack technique

| Domaine            | Choix                                                  |
|--------------------|--------------------------------------------------------|
| Langage            | Kotlin 2.2                                              |
| UI                 | Jetpack Compose + Material 3                            |
| Widgets            | Jetpack **Glance** (Compose pour App Widgets)           |
| Architecture       | MVVM — `ViewModel` + `StateFlow` (état immuable)       |
| Persistance        | **Room** (SQLite) pour l'historique, **DataStore** pour les réglages |
| Navigation         | Navigation-Compose (accueil ↔ historique ↔ graphiques) |
| Injection          | Conteneur manuel via la classe `Application`           |
| Sécurité           | **BiometricPrompt** (`androidx.biometric`) + `FLAG_SECURE` (mode discret) |
| Tests              | **JUnit 4** (KPI, séries, (dé)sérialisation, fusion d'import) |
| compileSdk         | 36                                                     |
| minSdk / targetSdk | 24 / 35                                                |
| applicationId      | `ma.anh.app`                                      |

Aucune dépendance de graphique externe : anneau, waffle, heatmap et barres sont dessinés avec des
composables Compose (`Canvas` / `Box`) et animés via `animate*AsState`.

---

## 🏗️ Architecture

Source de vérité unique : la table `interactions` (une ligne horodatée par appui). Les
compteurs, KPI, séries et graphiques en sont **dérivés par agrégation réactive** (`Flow`), ce qui
alimente tableau de bord, historique et page graphiques sans dupliquer l'état.

```
UI (Compose)  ─watch─►  ViewModel (StateFlow)  ─►  Repository  ─►  Room DAO  ─►  SQLite
   ▲                                                   │
   └───────────── record / undo / delete / import ─────┘
```

### Arborescence

```
app/src/main/java/ma/anh/app/
├── QCounterApp.kt            # Application : base, repositories, applicationScope
├── MainActivity.kt           # Point d'entrée Compose (palette + libellés + Material You)
├── QuickRecordActivity.kt    # Trampoline sans UI pour les raccourcis du lanceur
├── data/
│   ├── model/                # AnswerType, InteractionStats (+ KPI), StreakStats,
│   │                         #   HeatmapData, AppSettings (palette, emoji, libellés…)
│   ├── local/                # Entity, DAO, Converters, Database (Room)
│   ├── io/                   # InteractionTransfer (export/import JSON)
│   └── repository/           # InteractionRepository + SettingsRepository (DataStore)
└── ui/
    ├── ViewModelFactory.kt
    ├── navigation/           # QCounterNavHost (transitions animées)
    ├── theme/                # Color, Theme (Material You), Type, AccentPalette
    ├── util/                 # ShakeDetector (accéléromètre), SecureReveal (FLAG_SECURE + biométrie)
    ├── components/           # ActionCard, AnimatedCount, AnswerTypeVisuals (+ libellés),
    │                         #   WaffleChart · DonutChart · ActivityRings · Heatmap ·
    │                         #   ActivityBarChart · ClarityScoreCard · StreakCard,
    │                         #   MoodEmoji (jeux d'emojis), SettingsSheet, LegendItem…
    ├── widget/               # Widget Glance (4 variantes) + receivers + actions
    ├── tile/                 # QCounterTileService (tuile Quick Settings)
    ├── home/                 # HomeViewModel, HomeScreen
    ├── charts/               # ChartsViewModel, ChartsScreen (heatmap + barres)
    └── history/              # HistoryViewModel, HistoryScreen (+ export/import JSON)

app/src/test/java/ma/anh/app/      # Tests unitaires JUnit (KPI, séries, transfert JSON, import)
```

---

## 🎨 Identité visuelle

Charte de marque : **jaune** (`#FFE167` → `#FDD05E`, cf. logo) + **rouge** `#D5442D`. Le jaune habille
les **surfaces** (héro, conteneurs) tandis que les **accents de premier plan** (titre, icônes, barres)
sont en **encre foncée** pour la lisibilité (et en jaune clair sur fond sombre). Le logo « bulle de
discussion » intègre le mot‑symbole **Anh**. Palette d'indicateurs **Soleil** par défaut (doré · rouge ·
or grisé). L'utilisateur peut activer **Material You** (couleurs dynamiques, Android 12+).
Détail dans [`LOGO.md`](LOGO.md).

**Style (Material 3 « tonal »)** : les cartes se distinguent par la **teinte** (`surfaceContainer`)
plutôt que par des ombres, avec de **grands arrondis** et des **en‑têtes à badge d'icône** ; l'ombre
est réservée aux éléments à mettre en focus (barre de saisie ancrée, bouton flottant).

---

## ▶️ Compiler / lancer

> Le binaire `gradle/wrapper/gradle-wrapper.jar` n'est pas inclus.

**Option A — Android Studio (recommandé)**
1. *File → Open* → sélectionner le dossier `QCounter`.
2. Laisser Android Studio télécharger Gradle, le **SDK Android 36** et générer le wrapper.
3. *Run ▶* sur un émulateur ou un appareil (API 24+). Pour tester Material You et la secousse,
   privilégier un **appareil physique Android 12+**.

**Option B — ligne de commande** (Gradle installé globalement)
```powershell
gradle wrapper --gradle-version 8.13
./gradlew assembleDebug
```

---

## 🚀 Évolutions envisageables

Idées pour faire grandir l'app. Effort estimé : 🟢 rapide · 🟠 moyen · 🔴 conséquent.

### 🌱 Au-delà du compteur (nouvelles directions)

Des pistes pour transformer l'app d'un simple compteur en véritable **outil d'observation et de
debrief de conversations** :

- 🟠 **Sessions** comme objets de premier plan : une conversation = une session datée, nommée, avec
  son contexte (personne, lieu, sujet) et son propre rapport. Liste de sessions, reprise, comparaison.
- 🔴 **Enregistrement audio synchronisé** : poser des « marqueurs » (les appuis) sur une timeline
  audio, puis réécouter les moments d'esquive. (À cadrer côté consentement / confidentialité.)
- 🔴 **Assistant IA de débrief** : à partir des compteurs (et d'une note libre), générer un résumé
  et des conseils (« elle esquive surtout en fin d'échange »). Via l'API d'un LLM.
- 🔴 **Détection assistée** : suggérer le type d'une réplique à partir d'une transcription vocale
  (speech-to-text) — l'utilisateur valide d'un tap.
- 🟠 **Notes & moments clés** : annoter une session (citations, ressenti), épingler des instants.
- 🟠 **Rapport partageable** (PDF / image) : synthèse d'une session ou d'une période, à exporter.
- 🟠 **Objectifs & gamification** : se fixer un cap (« RC > 50 % »), badges, encouragements.
- 🔴 **Multi-appareils / web** : back-end de synchro (l'architecture repository isolé le permet).
- 🔴 **Compagnon Wear OS** : compter discrètement depuis la montre.
- 🟠 **Modèles de contexte** : jeux de catégories prêts à l'emploi (entretien d'embauche, négociation,
  médiation, thérapie…) qui renomment/colorent les boutons selon la situation.
- 🟠 **Mode minuteur de session** : durée de l'échange + cadence (interactions / minute).

### 📊 Données & analyse
- 🟠 **Sessions nommées** : démarrer/clore une session (ex. « Réunion du 6/6 »), comparer les sessions.
- 🟠 **Filtres par période** dans l'historique (aujourd'hui / semaine / mois) + recherche.
- 🟠 **Évolution temporelle** du TEL/RC et de l'indice de clarté (courbe par jour).
- 🟠 **Profils d'interlocuteurs** : suivre plusieurs personnes et comparer leurs comportements.
- 🟢 **Note / contexte** par interaction (sujet, lieu, humeur).
- 🟢 **Étiquettes / tags** sur une interaction ou une session, pour filtrer ensuite.
- 🟠 **Comparaison à la session précédente** (delta du RC/TEL, moyennes glissantes).
- 🟠 **Annulation multiple** (pile d'undo) plutôt que la seule dernière action.

### 📈 Visualisations
- 🟢 **Sparkline** compacte dans l'en-tête ou l'historique.
- 🟢 **Mode plein écran** d'un graphique (rotation paysage).
- 🟠 **Comparaison de deux périodes** côte à côte (cette semaine vs précédente).

### 🖐️ Saisie & UX
- 🟢 **Boutons de volume physiques** ou **gestes (swipe)** pour incrémenter sans regarder.
- 🔴 **Compagnon Wear OS** (montre connectée).
- 🟢 **Mode une main** / très gros boutons.
- 🟠 **Widget redimensionnable réactif** (Glance `SizeMode.Responsive`) : une seule définition qui
  s'adapte de 1×1 à 4×2, au lieu de 4 variantes distinctes.
- 🟠 **Tuile Quick Settings / widget 1×1 configurables** : choisir l'action enregistrée (Directe,
  Question ou Esquive) plutôt que « Question » par défaut.
- 🟠 **Raccourcis dynamiques** (`ShortcutManager`) reflétant les **libellés personnalisés** des boutons.

### 🎨 Personnalisation
- 🟠 **4ᵉ catégorie** personnalisée.
- 🟠 **Couleurs personnalisées** (color picker) en plus des presets.
- 🟢 **Forcer le thème** clair/sombre (override système).
- 🟢 **Icônes de raccourcis sur‑mesure** (glyphes ✓ / ↩ / ? au lieu de la bulle colorée).

### ☁️ Persistance & synchro
- 🟢 **Partage** d'un récapitulatif (image / CSV) en plus de l'export JSON.
- 🔴 **Back-end REST + Docker** (PostgreSQL / Node.js) : synchro multi-appareils. L'architecture
  (repository isolé) le rend simple — ajouter une source distante derrière `InteractionRepository`.
- 🟠 **Sauvegarde cloud** (Google Drive / compte utilisateur).

### 🔔 Notifications & objectifs
- 🟢 **Rappel quotidien** de bilan / **résumé hebdomadaire** en notification.
- 🟠 **Objectifs** (ex. « monter le RC à 50 % ») avec suivi de progression et **gamification** (badges).

### 🔒 Confidentialité
- 🟠 **Verrouillage à l'ouverture** de l'app (biométrie / code), en plus du mode discret déjà protégé
  par biométrie + `FLAG_SECURE`.
- 🟢 **Effacement auto** après une durée d'inactivité (option).

### 🛠️ Qualité & technique
- 🟢 **Tests unitaires** : ✅ KPI, indice de clarté, séries et fusion d'import couverts — reste à
  étendre (`moodEmoji`, bandes d'emoji, polarité).
- 🟠 **Tests UI Compose** des écrans clés.
- 🟢 **CI** (GitHub Actions) : build + tests + lint à chaque push.
- 🟢 **ktlint / Detekt** pour la cohérence du style.
- 🟠 **Accessibilité** : audit TalkBack, contrastes AA, tailles de police dynamiques.
- 🟠 **Internationalisation** : anglais, **arabe (RTL)** — l'app est déjà en `supportsRtl`.
- 🟠 **Hilt** pour l'injection si l'app grossit (remplace le conteneur manuel).
- 🟢 **Migrations Room** versionnées + **Baseline Profiles** (perfs de démarrage).
