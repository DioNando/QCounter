package ma.wave.qcounter.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ma.wave.qcounter.data.local.InteractionDao
import ma.wave.qcounter.data.local.InteractionEntity
import ma.wave.qcounter.data.model.AnswerType
import ma.wave.qcounter.data.model.InteractionStats
import ma.wave.qcounter.data.model.StreakStats

/**
 * Source de vérité unique de l'app. La table `interactions` est l'origine des
 * données ; les compteurs/KPI en sont dérivés par agrégation réactive.
 */
class InteractionRepository(
    private val dao: InteractionDao,
    private val now: () -> Long = System::currentTimeMillis,
) {

    /** Flux des KPI recalculés à chaque changement de la base. */
    val stats: Flow<InteractionStats> = dao.observeCounts().map { counts ->
        fun count(type: AnswerType) = counts.firstOrNull { it.type == type }?.count ?: 0
        InteractionStats(
            directAnswers = count(AnswerType.DIRECT),
            questionAnswers = count(AnswerType.QUESTION),
            unknownAnswers = count(AnswerType.UNKNOWN),
        )
    }

    /** Historique complet, le plus récent en tête. */
    val history: Flow<List<InteractionEntity>> = dao.observeAll()

    /** Séries (en cours / record), dérivées réactivement de l'historique. */
    val streaks: Flow<StreakStats> = dao.observeAll().map { rows ->
        StreakStats.from(rows.map { it.type })
    }

    /** Enregistre une interaction et retourne son id (pour pouvoir l'annuler). */
    suspend fun record(type: AnswerType): Long =
        dao.insert(InteractionEntity(type = type, timestamp = now()))

    suspend fun deleteById(id: Long) = dao.deleteById(id)

    /** Supprime la dernière interaction enregistrée. Retourne true si une ligne a été retirée. */
    suspend fun deleteLast(): Boolean {
        val last = dao.lastInteraction() ?: return false
        dao.deleteById(last.id)
        return true
    }

    suspend fun deleteByIds(ids: List<Long>) {
        if (ids.isNotEmpty()) dao.deleteByIds(ids)
    }

    /** Restaure des éléments précédemment supprimés (annulation). */
    suspend fun restore(items: List<InteractionEntity>) {
        if (items.isNotEmpty()) dao.insertAll(items)
    }

    suspend fun reset() {
        dao.clear()
    }

    /** Toutes les interactions, pour l'export. */
    suspend fun exportAll(): List<InteractionEntity> = dao.getAll()

    /**
     * Importe des interactions sans écraser l'existant ni créer de doublon : on ignore celles
     * dont le couple (type, horodatage) est déjà présent, ainsi que les répétitions du fichier.
     * Retourne le nombre d'interactions réellement ajoutées.
     */
    suspend fun importMerging(items: List<Pair<AnswerType, Long>>): Int {
        val existing = dao.getAll().map { it.type to it.timestamp }.toHashSet()
        val toInsert = items
            .distinct()
            .filter { it !in existing }
            .map { (type, timestamp) -> InteractionEntity(type = type, timestamp = timestamp) }
        if (toInsert.isNotEmpty()) dao.insertAll(toInsert)
        return toInsert.size
    }
}
