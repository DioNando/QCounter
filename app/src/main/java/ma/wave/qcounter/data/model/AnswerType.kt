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

    /**
     * 4ᵉ catégorie optionnelle, définie par l'utilisateur (activable dans les réglages).
     * Comptée dans le Volume Global mais **neutre** : exclue des KPI, du score et de l'emoji.
     */
    CUSTOM,

    /** Dimension « polarité » : réponse positive. Sous-décompte séparé, hors Volume et KPI. */
    OUI,

    /** Dimension « polarité » : réponse négative. Sous-décompte séparé, hors Volume et KPI. */
    NON,
}
