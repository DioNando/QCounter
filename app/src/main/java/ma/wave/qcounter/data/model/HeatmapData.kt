package ma.wave.qcounter.data.model

import java.util.Calendar

/**
 * Grille d'activité par jour de la semaine (0 = lundi … 6 = dimanche) et par heure (0–23).
 *
 * @property cells [jour][heure] = nombre d'interactions enregistrées sur ce créneau.
 * @property maxCount valeur maximale d'une cellule (pour normaliser l'intensité des couleurs).
 * @property total nombre total d'interactions prises en compte.
 */
data class HeatmapData(
    val cells: List<List<Int>> = List(7) { List(24) { 0 } },
    val maxCount: Int = 0,
    val total: Int = 0,
) {
    /** Total d'interactions par jour de la semaine (index 0 = lundi … 6 = dimanche). */
    val perDay: List<Int> get() = cells.map { it.sum() }

    /** Total d'interactions par heure de la journée (index 0 = 0 h … 23 = 23 h). */
    val perHour: List<Int> get() = (0 until 24).map { hour -> cells.sumOf { it[hour] } }

    companion object {
        fun from(timestamps: List<Long>): HeatmapData {
            if (timestamps.isEmpty()) return HeatmapData()

            val grid = Array(7) { IntArray(24) }
            val calendar = Calendar.getInstance()
            var max = 0
            for (timestamp in timestamps) {
                calendar.timeInMillis = timestamp
                // Calendar : dimanche = 1 … samedi = 7 → on réindexe en lundi = 0 … dimanche = 6.
                val dayIndex = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val value = grid[dayIndex][hour] + 1
                grid[dayIndex][hour] = value
                if (value > max) max = value
            }
            return HeatmapData(
                cells = grid.map { it.toList() },
                maxCount = max,
                total = timestamps.size,
            )
        }
    }
}
