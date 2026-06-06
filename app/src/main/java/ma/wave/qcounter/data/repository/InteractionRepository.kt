package ma.wave.qcounter.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ma.wave.qcounter.data.local.InteractionDao
import ma.wave.qcounter.data.local.InteractionEntity
import ma.wave.qcounter.data.model.AnswerType
import ma.wave.qcounter.data.model.InteractionStats

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

    /** Enregistre une interaction et retourne son id (pour pouvoir l'annuler). */
    suspend fun record(type: AnswerType): Long =
        dao.insert(InteractionEntity(type = type, timestamp = now()))

    suspend fun deleteById(id: Long) = dao.deleteById(id)

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
}
