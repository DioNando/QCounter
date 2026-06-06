package ma.anh.app.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ma.anh.app.data.local.InteractionDao
import ma.anh.app.data.local.InteractionEntity
import ma.anh.app.data.model.AnswerType
import ma.anh.app.data.model.InteractionStats
import ma.anh.app.data.model.StreakStats

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
            // Oui/Non sont des réponses directes qualifiées → agrégées dans « Directe ».
            directAnswers = count(AnswerType.DIRECT) + count(AnswerType.OUI) + count(AnswerType.NON),
            questionAnswers = count(AnswerType.QUESTION),
            unknownAnswers = count(AnswerType.UNKNOWN),
            customAnswers = count(AnswerType.CUSTOM),
            yesAnswers = count(AnswerType.OUI),
            noAnswers = count(AnswerType.NON),
        )
    }

    /** Historique complet, le plus récent en tête. */
    val history: Flow<List<InteractionEntity>> = dao.observeAll()

    /**
     * Séries (en cours / record). La **série en cours** porte sur les types réels (un enchaînement
     * de Oui/Non s'affiche tel quel) ; le **record** fusionne Oui/Non dans « Directe » (on ne veut
     * pas de Oui/Non dans la tuile Record).
     */
    val streaks: Flow<StreakStats> = dao.observeAll().map { rows ->
        val types = rows.map { it.type }
        val current = StreakStats.from(types)
        val record = StreakStats.from(
            types.map { if (it == AnswerType.OUI || it == AnswerType.NON) AnswerType.DIRECT else it },
        )
        StreakStats(
            currentType = current.currentType,
            currentLength = current.currentLength,
            bestType = record.bestType,
            bestLength = record.bestLength,
        )
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
