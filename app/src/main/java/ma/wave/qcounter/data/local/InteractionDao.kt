package ma.wave.qcounter.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ma.wave.qcounter.data.model.AnswerType

/** Projection d'un compte par type (résultat de l'agrégation GROUP BY). */
data class TypeCount(
    val type: AnswerType,
    val count: Int,
)

@Dao
interface InteractionDao {

    /** Retourne le rowId inséré, utilisé pour l'annulation ciblée. */
    @Insert
    suspend fun insert(interaction: InteractionEntity): Long

    /** Réinsère des éléments (en conservant leurs id) pour annuler une suppression. */
    @Insert
    suspend fun insertAll(interactions: List<InteractionEntity>)

    @Query("DELETE FROM interactions")
    suspend fun clear()

    @Query("DELETE FROM interactions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM interactions WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Long>)

    @Query("SELECT * FROM interactions ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<InteractionEntity>>

    @Query("SELECT type AS type, COUNT(*) AS count FROM interactions GROUP BY type")
    fun observeCounts(): Flow<List<TypeCount>>
}
