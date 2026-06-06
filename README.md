# QCounter — Android natif (Kotlin + Jetpack Compose)

QCounter est une application Android de **suivi comportemental** : via trois boutons, on
enregistre la façon dont une interlocutrice répond, et l'app calcule en temps réel des
indicateurs (KPI) sur sa tendance à répondre directement, à esquiver, ou à renvoyer une
question par une question.

Portage natif de la spécification d'origine (décrite en Flutter + Riverpod dans
`documentation_qcounter.pdf`), refait ici en **Android natif**.

---

## ✨ Fonctionnalités

- **Trois actions de saisie instantanée** : Réponse Directe, Question par une Question, Je ne sais pas.
- **KPI en temps réel** :
  - **Volume Global** — total des interactions.
  - **Taux d'Esquive Linéaire (TEL)** — % de questions renvoyées.
  - **Ratio de Clarté (RC)** — % de réponses directes.
  - **Taux d'Indécision** — % d'esquives.
- **Visualisation** : anneau (donut) animé de la répartition au centre de l'écran + légende,
  et barres de progression animées pour chaque KPI.
- **Annulation** : un *snackbar* « Annuler » apparaît après chaque saisie (et après chaque
  suppression dans l'historique) pour revenir en arrière.
- **Historique horodaté** persistant, avec **mode sélection multiple** : appui long pour
  sélectionner, puis suppression d'un ou plusieurs éléments (avec confirmation et annulation).
- **Réinitialisation** complète depuis l'accueil ou l'historique.
- UI conviviale : compteurs animés, cartes d'action colorées avec retour haptique, panneau
  de saisie ancré en bas (accessible au pouce), thème de marque bleu ciel.

---

## 🧱 Stack technique

| Domaine            | Choix                                                  |
|--------------------|--------------------------------------------------------|
| Langage            | Kotlin                                                 |
| UI                 | Jetpack Compose + Material 3                           |
| Architecture       | MVVM — `ViewModel` + `StateFlow` (état immuable)       |
| Persistance        | Room (SQLite), historique horodaté                     |
| Navigation         | Navigation-Compose (accueil ↔ historique)              |
| Injection          | Conteneur manuel via la classe `Application`           |
| minSdk / targetSdk | 24 / 35                                                |
| applicationId      | `ma.wave.qcounter`                                      |

Aucune dépendance d'animation ou de graphique externe : l'anneau est dessiné au `Canvas`
Compose et les animations utilisent `animate*AsState`.

---

## 🏗️ Architecture

Source de vérité unique : la table `interactions` (une ligne horodatée par appui). Les
compteurs et KPI en sont **dérivés par agrégation réactive** (`Flow`), ce qui alimente
aussi bien le tableau de bord que l'historique sans dupliquer l'état.

```
UI (Compose)  ─watch─►  ViewModel (StateFlow)  ─►  Repository  ─►  Room DAO  ─►  SQLite
   ▲                                                   │
   └───────────────── record / undo / delete ──────────┘
```

### Arborescence

```
app/src/main/java/ma/wave/qcounter/
├── QCounterApp.kt            # Application : base + repository partagés
├── MainActivity.kt           # Point d'entrée Compose
├── data/
│   ├── model/                # AnswerType, InteractionStats (+ KPI dérivés)
│   ├── local/                # Entity, DAO, Converters, Database (Room)
│   └── repository/           # InteractionRepository (source de vérité)
└── ui/
    ├── ViewModelFactory.kt
    ├── navigation/           # QCounterNavHost
    ├── theme/                # Color, Theme, Type (palette bleu ciel)
    ├── components/           # ActionCard, DonutChart, KpiDashboard, StatBar,
    │                         #   DistributionBar/LegendItem, AnimatedCount,
    │                         #   AnswerTypeVisuals, ResetConfirmationDialog
    ├── home/                 # HomeViewModel, HomeScreen
    └── history/              # HistoryViewModel, HistoryScreen
```

### Correspondance avec la doc Flutter d'origine

| Doc Flutter (Dart)            | Équivalent natif                                          |
|-------------------------------|-----------------------------------------------------------|
| `InteractionStats` (modèle)   | `data/model/InteractionStats.kt`                          |
| `CounterNotifier` (Riverpod)  | `ui/home/HomeViewModel.kt` + `InteractionRepository`      |
| `home_screen.dart`            | `ui/home/HomeScreen.kt`                                    |
| `counter_button`/`kpi_dashboard` | `ui/components/ActionCard.kt` / `KpiDashboard.kt`      |
| (état en mémoire)             | Persistance Room + écran `ui/history/HistoryScreen.kt`    |

---

## 🎨 Identité visuelle

Palette de marque centrée sur le **bleu ciel**, avec accents **rouge** et **gris** pour les
indicateurs. Le détail complet des couleurs (et les besoins pour le logo) se trouve dans
[`LOGO.md`](LOGO.md).

---

## ▶️ Compiler / lancer

> Le binaire `gradle/wrapper/gradle-wrapper.jar` n'est pas inclus.

**Option A — Android Studio (recommandé)**
1. *File → Open* → sélectionner le dossier `QCounter`.
2. Android Studio télécharge Gradle et génère le wrapper automatiquement.
3. *Run ▶* sur un émulateur ou un appareil (API 24+).

**Option B — ligne de commande** (Gradle installé globalement)
```powershell
gradle wrapper --gradle-version 8.13
./gradlew assembleDebug
```

---

## 🚀 Évolutions prévues (hors scope actuel)

Conformément à la doc d'origine : API REST légère + conteneurisation Docker (PostgreSQL /
Node.js) pour centraliser l'historique et analyser les métriques sur le long terme.
L'architecture en couches (repository isolé) rend cette transition simple : il suffira
d'ajouter une source de données distante derrière `InteractionRepository`.
