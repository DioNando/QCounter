package ma.anh.app.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import ma.anh.app.data.local.InteractionDao
import ma.anh.app.data.local.InteractionEntity
import ma.anh.app.data.local.TypeCount
import ma.anh.app.data.model.AnswerType
import org.junit.Assert.assertEquals
import org.junit.Test

/** DAO en mémoire, suffisant pour exercer la logique du dépôt sans Room. */
private class FakeInteractionDao : InteractionDao {
    val rows = mutableListOf<InteractionEntity>()
    private var nextId = 1L

    override suspend fun insert(interaction: InteractionEntity): Long {
        val e = interaction.copy(id = nextId++)
        rows.add(e)
        return e.id
    }

    override suspend fun insertAll(interactions: List<InteractionEntity>) {
        interactions.forEach { rows.add(if (it.id == 0L) it.copy(id = nextId++) else it) }
    }

    override suspend fun clear() = rows.clear()

    override suspend fun deleteById(id: Long) {
        rows.removeAll { it.id == id }
    }

    override suspend fun deleteByIds(ids: List<Long>) {
        rows.removeAll { it.id in ids }
    }

    override fun observeAll(): Flow<List<InteractionEntity>> =
        flowOf(rows.sortedByDescending { it.timestamp })

    override suspend fun getAll(): List<InteractionEntity> = rows.sortedBy { it.timestamp }

    override suspend fun lastInteraction(): InteractionEntity? = rows.maxByOrNull { it.timestamp }

    override fun observeCounts(): Flow<List<TypeCount>> =
        flowOf(rows.groupBy { it.type }.map { (type, list) -> TypeCount(type, list.size) })
}

/** Vérifie la fusion à l'import : pas d'écrasement, pas de doublon. */
class InteractionRepositoryTest {

    @Test
    fun importMerging_skipsExistingAndInFileDuplicates() = runBlocking {
        val dao = FakeInteractionDao()
        val repo = InteractionRepository(dao, now = { 0L })
        dao.insert(InteractionEntity(type = AnswerType.DIRECT, timestamp = 1_000L))

        val added = repo.importMerging(
            listOf(
                AnswerType.DIRECT to 1_000L,   // déjà présent → ignoré
                AnswerType.QUESTION to 2_000L, // nouveau
                AnswerType.QUESTION to 2_000L, // doublon dans le fichier → ignoré
            ),
        )

        assertEquals(1, added)
        assertEquals(2, dao.rows.size)
    }

    @Test
    fun importMerging_returnsZeroWhenAllDuplicates() = runBlocking {
        val dao = FakeInteractionDao()
        val repo = InteractionRepository(dao, now = { 0L })
        dao.insert(InteractionEntity(type = AnswerType.UNKNOWN, timestamp = 5_000L))

        val added = repo.importMerging(listOf(AnswerType.UNKNOWN to 5_000L))

        assertEquals(0, added)
        assertEquals(1, dao.rows.size)
    }
}
