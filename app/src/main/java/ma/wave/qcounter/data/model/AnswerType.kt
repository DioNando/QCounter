package ma.wave.qcounter.data.model

/**
 * Les trois catégories de réponse suivies par QCounter.
 * L'ordre/le nom sont stables : ils servent de clé persistée en base (voir Converters).
 */
enum class AnswerType {
    /** Réponse claire et sans détour. */
    DIRECT,

    /** Cas d'usage principal : une question renvoyée au lieu d'une réponse. */
    QUESTION,

    /** Esquive systématique ou indécision ("Je ne sais pas"). */
    UNKNOWN,
}
