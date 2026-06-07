package ma.anh.app.data.model

/** Type de graphique affiché sur l'écran d'accueil. */
enum class HomeChart { WAFFLE, DONUT, RINGS }

/**
 * Sensibilité de l'emoji d'humeur : à quel point une part dominante doit être forte
 * pour faire évoluer l'expression vers ses paliers « moyen » puis « marqué ».
 * Seuils plus bas = l'emoji réagit plus vite.
 */
enum class EmojiIntensity(val mediumThreshold: Double, val highThreshold: Double) {
    SUBTLE(mediumThreshold = 0.55, highThreshold = 0.80),
    NORMAL(mediumThreshold = 0.40, highThreshold = 0.66),
    STRONG(mediumThreshold = 0.33, highThreshold = 0.55),
}

/**
 * Libellés personnalisés des 3 boutons (null = libellé par défaut traduit).
 * Purement cosmétique : la sémantique des KPI/score/emoji reste inchangée.
 */
data class AnswerLabels(
    val direct: String? = null,
    val question: String? = null,
    val unknown: String? = null,
    val custom: String? = null,
    val directShort: String? = null,
    val questionShort: String? = null,
    val unknownShort: String? = null,
    val customShort: String? = null,
)

/** Préférences utilisateur persistées. */
data class AppSettings(
    val showEmoji: Boolean = true,
    val paletteId: Int = 0,
    val homeChart: HomeChart = HomeChart.WAFFLE,
    /** Couleurs dynamiques Material You (API 31+) : désactivées par défaut. */
    val dynamicColor: Boolean = false,
    /** Index du jeu d'emojis sélectionné (voir EmojiSets). */
    val emojiSetId: Int = 0,
    /** Sensibilité des paliers d'intensité de l'emoji. */
    val emojiIntensity: EmojiIntensity = EmojiIntensity.NORMAL,
    /** Libellés personnalisés des boutons (cosmétique). */
    val labels: AnswerLabels = AnswerLabels(),
    /** Active une 4ᵉ catégorie optionnelle (neutre dans les KPI). */
    val customEnabled: Boolean = false,
    /** Mode discret actif : persisté pour rester verrouillé même après fermeture de l'app. */
    val discreet: Boolean = false,
    /** Boutons d'action réduits : panneau remplacé par un bouton flottant qui déploie les icônes. */
    val compactActions: Boolean = false,
)
