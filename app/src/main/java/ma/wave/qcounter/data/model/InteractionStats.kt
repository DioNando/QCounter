package ma.wave.qcounter.data.model

/**
 * Instantané immuable des compteurs et des KPI dérivés.
 * Équivalent natif du `InteractionStats` Dart de la documentation d'origine.
 */
data class InteractionStats(
    val directAnswers: Int = 0,
    val questionAnswers: Int = 0,
    val unknownAnswers: Int = 0,
) {
    /** Volume Global : cumul total des interactions de la session. */
    val totalInteractions: Int
        get() = directAnswers + questionAnswers + unknownAnswers

    /** Taux d'Esquive Linéaire (TEL), en pourcentage. */
    val questionRatio: Double
        get() = if (totalInteractions == 0) 0.0
        else questionAnswers.toDouble() / totalInteractions * 100.0

    /** Ratio de Clarté (RC), en pourcentage. */
    val directRatio: Double
        get() = if (totalInteractions == 0) 0.0
        else directAnswers.toDouble() / totalInteractions * 100.0

    /** Taux d'indécision, en pourcentage. */
    val unknownRatio: Double
        get() = if (totalInteractions == 0) 0.0
        else unknownAnswers.toDouble() / totalInteractions * 100.0
}
