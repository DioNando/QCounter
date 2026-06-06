package ma.wave.qcounter.ui.components

import ma.wave.qcounter.data.model.ClarityBand
import ma.wave.qcounter.data.model.EmojiIntensity
import ma.wave.qcounter.data.model.InteractionStats

/**
 * Jeu d'emojis « d'humeur ». Pour chaque comportement dominant (Directe / Question / Esquive),
 * trois expressions du plus discret (palier bas) au plus marqué (palier haut), plus un visage
 * neutre (aucune interaction) et un visage d'égalité (pas de dominant clair).
 */
data class EmojiSet(
    val name: String,
    val neutral: String,
    val tie: String,
    /** [bas, moyen, marqué] pour Réponse Directe (clarté, positif). */
    val direct: List<String>,
    /** [bas, moyen, marqué] pour Question par une Question (esquive, sceptique). */
    val question: List<String>,
    /** [bas, moyen, marqué] pour Je ne sais pas (indécision). */
    val unknown: List<String>,
)

/** Jeux d'emojis sélectionnables dans les réglages. */
val EmojiSets: List<EmojiSet> = listOf(
    EmojiSet(
        name = "Classique",
        neutral = "🙂",
        tie = "🤔",
        direct = listOf("🙂", "😄", "🤩"),
        question = listOf("❓", "🤨", "🙄"),
        unknown = listOf("😕", "🤷", "🤯"),
    ),
    EmojiSet(
        name = "Expressif",
        neutral = "😐",
        tie = "🤔",
        direct = listOf("🙂", "😁", "🥳"),
        question = listOf("🤔", "🧐", "😏"),
        unknown = listOf("😶", "😵‍💫", "🤯"),
    ),
    EmojiSet(
        name = "Animaux",
        neutral = "🐱",
        tie = "🐼",
        direct = listOf("🐶", "🦁", "🦅"),
        question = listOf("🦊", "🐍", "🦉"),
        unknown = listOf("🐢", "🐌", "🦥"),
    ),
    EmojiSet(
        // Glyphes volontairement classiques (Unicode 6.x) pour un rendu fiable partout.
        name = "Météo",
        neutral = "⚪",
        tie = "🌈",
        direct = listOf("⛅", "☀️", "🌟"),
        question = listOf("☁️", "🌀", "⚡"),
        unknown = listOf("❄️", "☔", "🌊"),
    ),
)

/** Jeu d'emojis correspondant à l'index sélectionné, avec repli sur le premier. */
fun emojiSetOf(id: Int): EmojiSet = EmojiSets.getOrElse(id) { EmojiSets[0] }

/**
 * Emoji illustrant l'indice de clarté, dans le thème du jeu choisi : plus c'est clair, plus
 * on tend vers les expressions « Directe » ; plus c'est évasif, vers les « Question » ; le
 * palier mitigé reprend l'emoji d'égalité.
 */
fun clarityEmoji(band: ClarityBand, set: EmojiSet): String = when (band) {
    ClarityBand.VERY_CLEAR -> set.direct.last()
    ClarityBand.CLEAR -> set.direct[1]
    ClarityBand.MIXED -> set.tie
    ClarityBand.EVASIVE -> set.question[1]
    ClarityBand.VERY_EVASIVE -> set.question.last()
}

/**
 * Emoji du ratio Oui/Non, dans le thème du jeu : majorité de « Oui » → expressions positives
 * (Directe), majorité de « Non » → expressions « Question », équilibre → emoji d'égalité.
 */
fun yesNoEmoji(yesRatio: Double, set: EmojiSet): String = when {
    yesRatio >= 66 -> set.direct.last()
    yesRatio >= 55 -> set.direct[1]
    yesRatio > 45 -> set.tie
    yesRatio > 34 -> set.question[1]
    else -> set.question.last()
}

/**
 * Emoji « d'humeur » reflétant le comportement dominant ET son intensité, selon le jeu
 * choisi et la sensibilité des paliers. Plus la part du type dominant est forte, plus
 * l'expression est marquée — fun à voir évoluer.
 */
fun moodEmoji(
    stats: InteractionStats,
    set: EmojiSet = EmojiSets[0],
    intensity: EmojiIntensity = EmojiIntensity.NORMAL,
): String {
    // On se base sur le cœur comportemental (la 4ᵉ catégorie est neutre).
    val total = stats.coreTotal
    if (total == 0) return set.neutral

    val d = stats.directAnswers
    val q = stats.questionAnswers
    val u = stats.unknownAnswers
    val max = maxOf(d, q, u)

    // Égalité entre au moins deux types dominants → indécis/pensif.
    if (listOf(d, q, u).count { it == max } > 1) return set.tie

    val share = max.toDouble() / total
    val tier = when {                       // 0 = bas, 1 = moyen, 2 = marqué
        share >= intensity.highThreshold -> 2
        share >= intensity.mediumThreshold -> 1
        else -> 0
    }
    val palette = when (max) {
        d -> set.direct
        q -> set.question
        else -> set.unknown
    }
    return palette[tier]
}
