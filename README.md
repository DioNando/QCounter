# Anh — Android natif (Kotlin + Jetpack Compose)

> *Anciennement « QCounter ». Le `applicationId`/package restent `ma.wave.qcounter`.*

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
et le **record** (plus longue série jamais atteinte).

---

## ✨ Fonctionnalités

**Saisie & accueil**
- **Trois actions de saisie instantanée** (libellés personnalisables) avec compteurs animés, cartes
  colorées et retour haptique ; panneau ancré en bas, accessible au pouce.
- **Visualisation au choix** : **pictogramme waffle**, **anneau (donut)** ou **anneaux d'activité**,
  avec remplissage animé, légende et pourcentages KPI compacts.
- **Indice de clarté** et **séries** affichés en cartes dédiées.
- **Emoji d'humeur** évolutif selon le comportement dominant **et son intensité** : 4 **jeux d'emojis**
  (Classique, Expressif, Animaux, Météo) et 3 niveaux de **sensibilité** (Subtil / Normal / Marqué).

**Historique & graphiques**
- **Historique horodaté** persistant avec **mode sélection multiple** (appui long → suppression,
  confirmation et annulation).
- **Page Graphiques** : **heatmap** d'activité par heure × jour de la semaine, **activité par jour**
  et **activité par heure** (graphiques en barres).
- **Réinitialisation** complète depuis l'accueil ou l'historique.

**Annulation & confidentialité**
- **Annulation** par **bouton flottant éphémère** (apparaît après chaque saisie, disparaît au bout de
  quelques secondes) **et par secousse** (shake-to-undo, avec retour haptique).
- **Mode discret** : un appui sur l'icône « œil » masque instantanément tout le contenu derrière un
  écran neutre.

**Personnalisation**
- **Couleurs dynamiques Material You** (Android 12+) activables en option.
- **4 palettes** d'indicateurs : **Lagon** (charte de marque, par défaut) / Ciel / Forêt / Crépuscule.
- **Libellés des 3 boutons** personnalisables (longs et courts).

**Hors application**
- **Widget d'écran d'accueil** en **4 variantes** : complet (titre + total + 3 boutons), compact sur une
  ligne (total + 3 boutons), **2×1** (total + Directe & Question) et **1×1** (bouton Question). Material You.
- **Raccourcis du lanceur** (appui long sur l'icône) pour enregistrer une action sans ouvrir l'app.
- **Tuile Quick Settings** pour compter une « Question » depuis le volet des réglages rapides.

**Données**
- **Export / Import** en JSON. L'import **fusionne** sans écraser ni créer de doublon (dédup sur
  `type + horodatage`).

Réglages persistés via **DataStore** ; accessibles depuis la barre du haut.

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
| compileSdk         | 36                                                     |
| minSdk / targetSdk | 24 / 35                                                |
| applicationId      | `ma.wave.qcounter`                                      |

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
app/src/main/java/ma/wave/qcounter/
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
    ├── util/                 # ShakeDetector (accéléromètre)
    ├── components/           # ActionCard, AnimatedCount, AnswerTypeVisuals (+ libellés),
    │                         #   WaffleChart · DonutChart · ActivityRings · Heatmap ·
    │                         #   ActivityBarChart · ClarityScoreCard · StreakCard,
    │                         #   MoodEmoji (jeux d'emojis), SettingsSheet, LegendItem…
    ├── widget/               # Widget Glance (4 variantes) + receivers + actions
    ├── tile/                 # QCounterTileService (tuile Quick Settings)
    ├── home/                 # HomeViewModel, HomeScreen
    ├── charts/               # ChartsViewModel, ChartsScreen (heatmap + barres)
    └── history/              # HistoryViewModel, HistoryScreen
```

---

## 🎨 Identité visuelle

Charte de marque : **jaune** (`#FFE167` → `#FDD05E`, cf. logo) + **rouge** `#D5442D`. Le jaune habille
les **surfaces** (héro, FAB, conteneurs) tandis que les **accents de premier plan** (le « Q », icônes,
barres) sont en **encre foncée** pour la lisibilité (et en jaune clair sur fond sombre). Palette
d'indicateurs **Lagon** par défaut : sarcelle · rouge · gris. L'utilisateur peut aussi activer
**Material You** (couleurs dynamiques tirées du fond d'écran, Android 12+). Détail dans [`LOGO.md`](LOGO.md).

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

Idées restantes pour faire grandir l'app. Effort estimé : 🟢 rapide · 🟠 moyen · 🔴 conséquent.

### 📊 Données & analyse
- 🟠 **Sessions nommées** : démarrer/clore une session (ex. « Réunion du 6/6 »), comparer les sessions.
- 🟠 **Filtres par période** dans l'historique (aujourd'hui / semaine / mois) + recherche.
- 🟠 **Évolution temporelle** du TEL/RC et de l'indice de clarté (courbe par jour).
- 🟠 **Profils d'interlocuteurs** : suivre plusieurs personnes et comparer leurs comportements.
- 🟢 **Note / contexte** par interaction (sujet, lieu, humeur).

### 📈 Visualisations
- 🟢 **Sparkline** compacte dans l'en-tête ou l'historique.
- 🟢 **Mode plein écran** d'un graphique (rotation paysage).

### 🖐️ Saisie & UX
- 🟢 **Boutons de volume physiques** ou **gestes (swipe)** pour incrémenter sans regarder.
- 🔴 **Compagnon Wear OS** (montre connectée).
- 🟢 **Mode une main** / très gros boutons.

### 🎨 Personnalisation
- 🟠 **4ᵉ catégorie** personnalisée.
- 🟠 **Couleurs personnalisées** (color picker) en plus des 3 presets.
- 🟢 **Forcer le thème** clair/sombre (override système).

### ☁️ Persistance & synchro
- 🟢 **Partage** d'un récapitulatif (image / CSV) en plus de l'export JSON.
- 🔴 **Back-end REST + Docker** (PostgreSQL / Node.js) : synchro multi-appareils. L'architecture
  (repository isolé) le rend simple — ajouter une source distante derrière `InteractionRepository`.
- 🟠 **Sauvegarde cloud** (Google Drive / compte utilisateur).

### 🔔 Notifications & objectifs
- 🟢 **Rappel quotidien** de bilan / **résumé hebdomadaire** en notification.
- 🟠 **Objectifs** (ex. « monter le RC à 50 % ») avec suivi de progression et **gamification** (badges).

### 🔒 Confidentialité
- 🟠 **Verrouillage** par biométrie / code à l'ouverture.

### 🛠️ Qualité & technique
- 🟢 **Tests unitaires** : KPI dérivés, indice de clarté, séries, `moodEmoji`, fusion d'import.
- 🟠 **Tests UI Compose** des écrans clés.
- 🟢 **CI** (GitHub Actions) : build + tests + lint à chaque push.
- 🟢 **ktlint / Detekt** pour la cohérence du style.
- 🟠 **Accessibilité** : audit TalkBack, contrastes AA, tailles de police dynamiques.
- 🟠 **Internationalisation** : anglais, **arabe (RTL)** — l'app est déjà en `supportsRtl`.
- 🟠 **Hilt** pour l'injection si l'app grossit (remplace le conteneur manuel).
- 🟢 **Migrations Room** versionnées + **Baseline Profiles** (perfs de démarrage).
