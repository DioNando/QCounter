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
- **Visualisation au choix** sur l'accueil (réglages) : **pictogramme waffle**, **anneau (donut)**
  ou **anneaux d'activité** — avec remplissage animé, légende et pourcentages KPI compacts.
- **Emoji d'humeur** évolutif selon le comportement dominant **et son intensité** (10 expressions,
  masquable dans les réglages).
- **3 palettes** de couleurs d'indicateurs sélectionnables (Ciel / Forêt / Crépuscule), persistées.
- **Réglages** accessibles via la barre du haut (DataStore).
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
├── QCounterApp.kt            # Application : base + repositories partagés
├── MainActivity.kt           # Point d'entrée Compose, palette animée fournie ici
├── data/
│   ├── model/                # AnswerType, InteractionStats (+ KPI), AppSettings/HomeChart
│   ├── local/                # Entity, DAO, Converters, Database (Room)
│   └── repository/           # InteractionRepository + SettingsRepository (DataStore)
└── ui/
    ├── ViewModelFactory.kt
    ├── navigation/           # QCounterNavHost (transitions animées)
    ├── theme/                # Color, Theme, Type, AccentPalette (3 palettes)
    ├── components/           # ActionCard, AnimatedCount, AnswerTypeVisuals,
    │                         #   WaffleChart · DonutChart · ActivityRings (graphiques),
    │                         #   MoodEmoji, SettingsSheet, ResetConfirmationDialog, LegendItem
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

## 🚀 Évolutions envisageables

Liste d'idées pour faire grandir l'app, classées par thème. La mention indique l'effort estimé :
🟢 rapide · 🟠 moyen · 🔴 conséquent.

### 📊 Données & analyse
- 🟠 **Sessions nommées** : démarrer/clore une session (ex. « Réunion du 6/6 »), comparer les sessions entre elles.
- 🟠 **Filtres par période** dans l'historique (aujourd'hui / semaine / mois) + recherche.
- 🟠 **Statistiques temporelles** : évolution du TEL/RC dans le temps, moyenne par session, meilleures/pires plages horaires.
- 🟢 **Séries (streaks)** : « 5 réponses directes d'affilée », plus longue série, etc.
- 🟠 **Profils d'interlocuteurs** : suivre plusieurs personnes et comparer leurs comportements.
- 🟢 **Note / contexte** par interaction (sujet, lieu, humeur).
- 🟠 **Score comportemental** synthétique (ex. « indice de clarté » global) avec interprétation.

### 📈 Visualisations
- 🟠 **Page Graphiques optionnelle** réactivable (courbe d'évolution lissée, barres, comparaison de sessions).
- 🟠 **Heatmap** par heure/jour de la semaine.
- 🟢 **Sparkline** compacte dans l'en-tête ou l'historique.
- 🟢 **Mode plein écran** d'un graphique (rotation paysage).

### 🖐️ Saisie & UX
- 🔴 **Widget écran d'accueil** : compter sans ouvrir l'app.
- 🟠 **App Shortcuts / Quick Settings tile** : saisie rapide depuis le lanceur ou les réglages rapides.
- 🟢 **Boutons de volume physiques** ou **gestes (swipe)** pour incrémenter sans regarder.
- 🔴 **Compagnon Wear OS** (montre connectée).
- 🟢 **Annuler par secousse** (shake-to-undo) en plus du snackbar.
- 🟢 **Mode une main** / très gros boutons.

### 🎨 Personnalisation
- 🟠 **Renommer les catégories** et/ou ajouter une **4ᵉ catégorie** personnalisée.
- 🟠 **Couleurs personnalisées** (color picker) en plus des 3 presets.
- 🟢 **Couleurs dynamiques Material You** réactivables en option.
- 🟢 **Forcer le thème** clair/sombre (override système).
- 🟢 **Réglage des emojis** : choisir un jeu d'emojis ou ajuster les seuils d'intensité.

### ☁️ Persistance & synchro
- 🟢 **Export / partage** des données (CSV, JSON, image récap).
- 🟠 **Import** de données / restauration.
- 🔴 **Back-end REST + Docker** (PostgreSQL / Node.js) — déjà prévu dans la doc d'origine :
  synchro multi-appareils, historique centralisé. L'architecture (repository isolé) le rend simple :
  il suffira d'ajouter une source distante derrière `InteractionRepository`.
- 🟠 **Sauvegarde cloud** (Google Drive / compte utilisateur).

### 🔔 Notifications & objectifs
- 🟢 **Rappel quotidien** de bilan / **résumé hebdomadaire** en notification.
- 🟠 **Objectifs** (ex. « monter le RC à 50 % ») avec suivi de progression et **gamification** (badges).

### 🔒 Confidentialité
- 🟠 **Verrouillage** par biométrie / code à l'ouverture.
- 🟢 **Masquage rapide** / mode discret.

### 🛠️ Qualité & technique
- 🟢 **Tests unitaires** : KPI dérivés, répartition du waffle (`distribute`), `moodEmoji`.
- 🟠 **Tests UI Compose** des écrans clés.
- 🟢 **CI** (GitHub Actions) : build + tests + lint à chaque push.
- 🟢 **ktlint / Detekt** pour la cohérence du style.
- 🟠 **Accessibilité** : audit TalkBack, contrastes AA, tailles de police dynamiques.
- 🟠 **Internationalisation** : anglais, **arabe (RTL)** — l'app est déjà en `supportsRtl`.
- 🟠 **Hilt** pour l'injection si l'app grossit (remplace le conteneur manuel).
- 🟢 **Migrations Room** versionnées + **Baseline Profiles** (perfs de démarrage).

> 💡 Suggestions de **quick wins** pour démarrer : export CSV, séries (streaks), tests unitaires des KPI,
> et le widget écran d'accueil pour l'usage au quotidien.
