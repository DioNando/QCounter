package ma.anh.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ma.anh.app.data.model.AnswerType

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

    /** Lecture ponctuelle de toutes les interactions (export). */
    @Query("SELECT * FROM interactions ORDER BY timestamp ASC")
    suspend fun getAll(): List<InteractionEntity>

    /** Dernière interaction enregistrée (pour l'annulation par secousse / bouton flottant). */
    @Query("SELECT * FROM interactions ORDER BY timestamp DESC, id DESC LIMIT 1")
    suspend fun lastInteraction(): InteractionEntity?

    @Query("SELECT type AS type, COUNT(*) AS count FROM interactions GROUP BY type")
    fun observeCounts(): Flow<List<TypeCount>>
}
