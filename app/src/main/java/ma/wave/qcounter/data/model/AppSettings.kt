package ma.wave.qcounter.data.model

/** Type de graphique affiché sur l'écran d'accueil. */
enum class HomeChart { WAFFLE, DONUT, RINGS }

/** Préférences utilisateur persistées. */
data class AppSettings(
    val showEmoji: Boolean = true,
    val paletteId: Int = 0,
    val homeChart: HomeChart = HomeChart.WAFFLE,
)
