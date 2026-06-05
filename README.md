# QCounter — Android natif (Kotlin + Jetpack Compose)

Portage natif Android de l'application QCounter décrite dans `documentation_qcounter.pdf`
(spécifiée à l'origine en Flutter + Riverpod).

L'app suit, via trois boutons d'incrémentation, la façon dont une interlocutrice répond
(Réponse Directe / Question par une Question / Je ne sais pas) et calcule en temps réel les KPI :
**Volume Global**, **Taux d'Esquive Linéaire (TEL)** et **Ratio de Clarté (RC)**.

## Stack technique

| Domaine            | Choix                                                  |
|--------------------|--------------------------------------------------------|
| Langage            | Kotlin                                                 |
| UI                 | Jetpack Compose + Material 3 (couleurs dynamiques)     |
| Architecture       | MVVM — `ViewModel` + `StateFlow` (état immuable)       |
| Persistance        | Room (SQLite), historique horodaté                     |
| Navigation         | Navigation-Compose (écran principal ↔ historique)      |
| Injection          | Conteneur manuel via la classe `Application`           |
| minSdk / targetSdk | 24 / 35                                                |
| applicationId      | `ma.wave.qcounter`                                      |

## Correspondance avec la doc Flutter

| Doc Flutter (Dart)            | Équivalent natif                                          |
|-------------------------------|-----------------------------------------------------------|
| `InteractionStats` (modèle)   | `data/model/InteractionStats.kt`                          |
| `CounterNotifier` (Riverpod)  | `ui/home/HomeViewModel.kt` + `InteractionRepository`      |
| `home_screen.dart`            | `ui/home/HomeScreen.kt`                                    |
| `counter_button` / `kpi_dashboard` | `ui/components/ActionCard.kt` / `KpiDashboard.kt` |
| (en mémoire)                  | Persistance Room + écran `ui/history/HistoryScreen.kt`    |

Source de vérité : la table `interactions` (une ligne par appui, horodatée).
Les compteurs et KPI sont **dérivés par agrégation réactive** sur cette table.

## Arborescence

```
app/src/main/java/ma/wave/qcounter/
├── QCounterApp.kt            # Application : DB + repository partagés
├── MainActivity.kt           # Point d'entrée Compose
├── data/
│   ├── model/                # AnswerType, InteractionStats
│   ├── local/                # Entity, DAO, Converters, Database (Room)
│   └── repository/           # InteractionRepository (source de vérité)
└── ui/
    ├── ViewModelFactory.kt
    ├── navigation/           # QCounterNavHost
    ├── theme/                # Couleurs, typo, thème Material 3
    ├── components/           # ActionCard, KpiDashboard, DistributionBar,
    │                         #   StatBar, AnimatedCount, légendes, dialogue reset
    ├── home/                 # HomeViewModel, HomeScreen
    └── history/              # HistoryViewModel, HistoryScreen
```

## Compiler / lancer

> Le binaire `gradle/wrapper/gradle-wrapper.jar` (fichier binaire) n'est pas inclus.

**Option A — Android Studio (recommandé)**
1. *File → Open* → sélectionner le dossier `QCounter`.
2. Android Studio télécharge Gradle 8.9 et génère le wrapper automatiquement.
3. *Run ▶* sur un émulateur ou un appareil (API 24+).

**Option B — ligne de commande**
Si Gradle est installé globalement, générer le wrapper puis builder :
```powershell
gradle wrapper --gradle-version 8.9
./gradlew assembleDebug
```

## Évolutions prévues (hors scope actuel)

Conformément à la doc : API REST légère + conteneurisation Docker (PostgreSQL / Node.js)
pour centraliser l'historique. L'architecture en couches (repository isolé) rend cette
transition simple — il suffira d'ajouter une source de données distante derrière
`InteractionRepository`.
