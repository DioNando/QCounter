package ma.anh.app.data.io

import ma.anh.app.data.local.InteractionEntity
import ma.anh.app.data.model.AnswerType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Vérifie la (dé)sérialisation JSON de l'import/export. */
class InteractionTransferTest {

    @Test
    fun encodeThenDecode_roundTripsTypeAndTimestamp() {
        val items = listOf(
            InteractionEntity(id = 1, type = AnswerType.DIRECT, timestamp = 1_000L),
            InteractionEntity(id = 2, type = AnswerType.QUESTION, timestamp = 2_000L),
            InteractionEntity(id = 3, type = AnswerType.OUI, timestamp = 3_000L),
        )
        val decoded = InteractionTransfer.decode(InteractionTransfer.encode(items))
        assertEquals(
            listOf(
                AnswerType.DIRECT to 1_000L,
                AnswerType.QUESTION to 2_000L,
                AnswerType.OUI to 3_000L,
            ),
            decoded,
        )
    }

    @Test
    fun decode_skipsInvalidEntries() {
        val json = """
            {
              "version": 1,
              "interactions": [
                { "type": "DIRECT", "timestamp": 1000 },
                { "type": "INCONNU", "timestamp": 2000 },
                { "type": "QUESTION" },
                { "type": "UNKNOWN", "timestamp": 3000 }
              ]
            }
        """.trimIndent()
        val decoded = InteractionTransfer.decode(json)
        assertEquals(
            listOf(AnswerType.DIRECT to 1_000L, AnswerType.UNKNOWN to 3_000L),
            decoded,
        )
    }

    @Test
    fun decode_handlesMissingArray() {
        assertTrue(InteractionTransfer.decode("""{ "version": 1 }""").isEmpty())
    }
}
