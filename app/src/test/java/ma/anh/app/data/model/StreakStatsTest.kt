package ma.anh.app.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Vérifie le calcul des séries (en cours / record) et des records par type. */
class StreakStatsTest {

    @Test
    fun empty_returnsNeutralStreak() {
        val s = StreakStats.from(emptyList())
        assertNull(s.currentType)
        assertEquals(0, s.currentLength)
        assertNull(s.bestType)
        assertEquals(0, s.bestLength)
    }

    @Test
    fun current_isLeadingRunOfMostRecentFirstList() {
        // L'historique est trié du plus récent au plus ancien.
        val history = listOf(AnswerType.DIRECT, AnswerType.DIRECT, AnswerType.QUESTION)
        val s = StreakStats.from(history)
        assertEquals(AnswerType.DIRECT, s.currentType)
        assertEquals(2, s.currentLength)
    }

    @Test
    fun best_isLongestConsecutiveRun() {
        val history = listOf(
            AnswerType.QUESTION,
            AnswerType.DIRECT, AnswerType.DIRECT, AnswerType.DIRECT,
            AnswerType.UNKNOWN,
        )
        val s = StreakStats.from(history)
        assertEquals(AnswerType.QUESTION, s.currentType)
        assertEquals(1, s.currentLength)
        assertEquals(AnswerType.DIRECT, s.bestType)
        assertEquals(3, s.bestLength)
    }

    @Test
    fun bestByType_tracksLongestRunPerType() {
        val history = listOf(
            AnswerType.DIRECT, AnswerType.DIRECT,
            AnswerType.QUESTION,
            AnswerType.DIRECT,
        )
        val best = StreakStats.bestByType(history)
        assertEquals(2, best[AnswerType.DIRECT])
        assertEquals(1, best[AnswerType.QUESTION])
        assertNull(best[AnswerType.UNKNOWN])
    }
}
