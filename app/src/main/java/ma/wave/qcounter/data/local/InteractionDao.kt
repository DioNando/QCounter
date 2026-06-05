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

    @Insert
    suspend fun insert(interaction: InteractionEntity)

    @Query("DELETE FROM interactions")
    suspend fun clear()

    @Query("SELECT * FROM interactions ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<InteractionEntity>>

    @Query("SELECT type AS type, COUNT(*) AS count FROM interactions GROUP BY type")
    fun observeCounts(): Flow<List<TypeCount>>
}
