package ma.anh.app.data.model

/**
 * Séries de réponses consécutives d'un même type.
 *
 * @property currentType type de la série en cours (la plus récente), ou null si aucune interaction.
 * @property currentLength longueur de la série en cours.
 * @property bestType type de la plus longue série jamais enregistrée.
 * @property bestLength longueur de la plus longue série.
 */
data class StreakStats(
    val currentType: AnswerType? = null,
    val currentLength: Int = 0,
    val bestType: AnswerType? = null,
    val bestLength: Int = 0,
) {
    companion object {
        /**
         * Calcule les séries à partir de l'historique trié du plus récent au plus ancien
         * (ordre de `observeAll`). La série « en cours » est la suite de tête ; la « meilleure »
         * est la plus longue suite consécutive (le sens de parcours n'a pas d'importance).
         */
        fun from(historyMostRecentFirst: List<AnswerType>): StreakStats {
            if (historyMostRecentFirst.isEmpty()) return StreakStats()

            val currentType = historyMostRecentFirst.first()
            val currentLength = historyMostRecentFirst.takeWhile { it == currentType }.size

            var bestType = currentType
            var bestLength = 0
            var runType: AnswerType? = null
            var runLength = 0
            for (type in historyMostRecentFirst) {
                if (type == runType) {
                    runLength++
                } else {
                    runType = type
                    runLength = 1
                }
                if (runLength > bestLength) {
                    bestLength = runLength
                    bestType = type
                }
            }
            return StreakStats(currentType, currentLength, bestType, bestLength)
        }

        /** Plus longue série consécutive **par type** présente dans l'historique. */
        fun bestByType(history: List<AnswerType>): Map<AnswerType, Int> {
            val best = LinkedHashMap<AnswerType, Int>()
            var runType: AnswerType? = null
            var runLength = 0
            for (type in history) {
                runLength = if (type == runType) runLength + 1 else 1
                runType = type
                if (runLength > (best[type] ?: 0)) best[type] = runLength
            }
            return best
        }
    }
}
