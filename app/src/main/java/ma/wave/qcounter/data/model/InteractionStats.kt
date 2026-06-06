package ma.wave.qcounter.data.model

import kotlin.math.roundToInt

/** Paliers d'interprétation de l'indice de clarté comportemental. */
enum class ClarityBand { VERY_CLEAR, CLEAR, MIXED, EVASIVE, VERY_EVASIVE }

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

    /**
     * Indice de Clarté synthétique (0–100) : une réponse directe vaut sa pleine valeur,
     * une indécision compte partiellement (moins évasive qu'un renvoi), une question renvoyée
     * ne crédite rien. Résume en un seul nombre la tendance à répondre clairement.
     */
    val clarityScore: Int
        get() = if (totalInteractions == 0) 0
        else ((directAnswers * 1.0 + unknownAnswers * 0.3) / totalInteractions * 100.0).roundToInt()

    /** Interprétation qualitative de l'indice de clarté. */
    val clarityBand: ClarityBand
        get() = when {
            clarityScore >= 80 -> ClarityBand.VERY_CLEAR
            clarityScore >= 60 -> ClarityBand.CLEAR
            clarityScore >= 40 -> ClarityBand.MIXED
            clarityScore >= 20 -> ClarityBand.EVASIVE
            else -> ClarityBand.VERY_EVASIVE
        }
}
