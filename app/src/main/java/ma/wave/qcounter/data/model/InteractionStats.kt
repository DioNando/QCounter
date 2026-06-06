package ma.wave.qcounter.data.model

import kotlin.math.roundToInt

/** Paliers d'interprétation de l'indice de clarté comportemental. */
enum class ClarityBand { VERY_CLEAR, CLEAR, MIXED, EVASIVE, VERY_EVASIVE }

/**
 * Instantané immuable des compteurs et des KPI dérivés.
 *
 * Le cœur comportemental (Directe / Question / Esquive) alimente tous les KPI, le score et l'emoji.
 * Oui / Non sont des **réponses directes qualifiées** : déjà agrégés dans `directAnswers`, ils
 * comptent donc pleinement (Volume, RC, clarté). `yesAnswers`/`noAnswers` servent juste au ratio.
 * La 4ᵉ catégorie (`customAnswers`) est comptée dans le Volume mais **neutre** dans les KPI.
 */
data class InteractionStats(
    val directAnswers: Int = 0,
    val questionAnswers: Int = 0,
    val unknownAnswers: Int = 0,
    val customAnswers: Int = 0,
    val yesAnswers: Int = 0,
    val noAnswers: Int = 0,
) {
    /** Volume Global : Directe (Oui/Non inclus) + Question + Esquive + 4ᵉ catégorie. */
    val totalInteractions: Int
        get() = directAnswers + questionAnswers + unknownAnswers + customAnswers

    /** Total de la dimension polarité (Oui + Non). */
    val yesNoTotal: Int
        get() = yesAnswers + noAnswers

    /** Part de « Oui » dans Oui+Non, en pourcentage. */
    val yesRatio: Double
        get() = if (yesNoTotal == 0) 0.0 else yesAnswers.toDouble() / yesNoTotal * 100.0

    /** Total du cœur comportemental (les 3 types qui alimentent les KPI). */
    val coreTotal: Int
        get() = directAnswers + questionAnswers + unknownAnswers

    /** Taux d'Esquive Linéaire (TEL), en pourcentage du cœur. */
    val questionRatio: Double
        get() = if (coreTotal == 0) 0.0
        else questionAnswers.toDouble() / coreTotal * 100.0

    /** Ratio de Clarté (RC), en pourcentage du cœur. */
    val directRatio: Double
        get() = if (coreTotal == 0) 0.0
        else directAnswers.toDouble() / coreTotal * 100.0

    /** Taux d'indécision, en pourcentage du cœur. */
    val unknownRatio: Double
        get() = if (coreTotal == 0) 0.0
        else unknownAnswers.toDouble() / coreTotal * 100.0

    /**
     * Indice de Clarté synthétique (0–100) sur le cœur comportemental : une réponse directe vaut
     * sa pleine valeur, une indécision compte partiellement, une question renvoyée ne crédite rien.
     */
    val clarityScore: Int
        get() = if (coreTotal == 0) 0
        else ((directAnswers * 1.0 + unknownAnswers * 0.3) / coreTotal * 100.0).roundToInt()

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
