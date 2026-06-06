package ma.anh.app.data.local

import androidx.room.TypeConverter
import ma.anh.app.data.model.AnswerType

/** Convertit l'enum AnswerType vers/depuis la chaîne stockée en base. */
class Converters {
    @TypeConverter
    fun fromAnswerType(type: AnswerType): String = type.name

    @TypeConverter
    fun toAnswerType(value: String): AnswerType = AnswerType.valueOf(value)
}
