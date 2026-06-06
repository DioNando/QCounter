package ma.wave.qcounter.ui.components

import ma.wave.qcounter.data.model.InteractionStats

/**
 * Emoji « d'humeur » reflétant le comportement dominant ET son intensité.
 * Plus la part du type dominant est forte, plus l'expression est marquée — fun à voir évoluer.
 */
fun moodEmoji(stats: InteractionStats): String {
    val total = stats.totalInteractions
    if (total == 0) return "🙂"

    val d = stats.directAnswers
    val q = stats.questionAnswers
    val u = stats.unknownAnswers
    val max = maxOf(d, q, u)

    // Égalité entre au moins deux types dominants → indécis/pensif.
    if (listOf(d, q, u).count { it == max } > 1) return "🤔"

    val share = max.toDouble() / total
    return when (max) {
        d -> when {                 // Réponse Directe = clarté (positif)
            share >= 0.66 -> "🤩"
            share >= 0.40 -> "😄"
            else -> "🙂"
        }
        q -> when {                 // Question par une Question (esquive, sceptique)
            share >= 0.66 -> "🙄"
            share >= 0.40 -> "🤨"
            else -> "❓"
        }
        else -> when {              // Je ne sais pas (indécision)
            share >= 0.66 -> "🤯"
            share >= 0.40 -> "🤷"
            else -> "😕"
        }
    }
}
