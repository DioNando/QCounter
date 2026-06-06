package ma.anh.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import ma.anh.app.data.model.AnswerType

/**
 * Une interaction horodatée. Chaque appui sur un bouton insère une ligne ;
 * les compteurs et KPI sont dérivés par agrégation sur cette table.
 */
@Entity(tableName = "interactions")
data class InteractionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: AnswerType,
    val timestamp: Long,
)
