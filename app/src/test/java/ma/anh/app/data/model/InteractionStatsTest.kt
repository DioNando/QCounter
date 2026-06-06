package ma.anh.app.data.model

import org.junit.Assert.assertEquals
import org.junit.Test

/** Vérifie les KPI dérivés : totaux, ratios, indice de clarté et ses paliers. */
class InteractionStatsTest {

    @Test
    fun emptyStats_areAllZeroAndVeryEvasive() {
        val stats = InteractionStats()
        assertEquals(0, stats.totalInteractions)
        assertEquals(0, stats.coreTotal)
        assertEquals(0.0, stats.directRatio, 0.0)
        assertEquals(0.0, stats.questionRatio, 0.0)
        assertEquals(0.0, stats.unknownRatio, 0.0)
        assertEquals(0.0, stats.yesRatio, 0.0)
        assertEquals(0, stats.clarityScore)
        assertEquals(ClarityBand.VERY_EVASIVE, stats.clarityBand)
    }

    @Test
    fun totalIncludesCustom_butCoreDoesNot() {
        val stats = InteractionStats(
            directAnswers = 6,
            questionAnswers = 2,
            unknownAnswers = 2,
            customAnswers = 5,
        )
        assertEquals(15, stats.totalInteractions)
        assertEquals(10, stats.coreTotal)
    }

    @Test
    fun ratios_areComputedOverCoreTotal() {
        val stats = InteractionStats(directAnswers = 6, questionAnswers = 2, unknownAnswers = 2)
        assertEquals(60.0, stats.directRatio, 0.001)
        assertEquals(20.0, stats.questionRatio, 0.001)
        assertEquals(20.0, stats.unknownRatio, 0.001)
    }

    @Test
    fun yesRatio_isShareOfYesAmongYesNo() {
        val stats = InteractionStats(directAnswers = 4, yesAnswers = 3, noAnswers = 1)
        assertEquals(4, stats.yesNoTotal)
        assertEquals(75.0, stats.yesRatio, 0.001)
    }

    @Test
    fun clarityScore_creditsDirectFullyAndUnknownPartially() {
        // (6*1.0 + 2*0.3) / 10 * 100 = 66
        val stats = InteractionStats(directAnswers = 6, questionAnswers = 2, unknownAnswers = 2)
        assertEquals(66, stats.clarityScore)
        assertEquals(ClarityBand.CLEAR, stats.clarityBand)
    }

    @Test
    fun clarityBands_coverEachThreshold() {
        assertEquals(ClarityBand.VERY_CLEAR, InteractionStats(directAnswers = 9, questionAnswers = 1).clarityBand)
        assertEquals(ClarityBand.CLEAR, InteractionStats(directAnswers = 6, questionAnswers = 4).clarityBand)
        assertEquals(ClarityBand.MIXED, InteractionStats(directAnswers = 4, questionAnswers = 6).clarityBand)
        assertEquals(ClarityBand.EVASIVE, InteractionStats(directAnswers = 2, questionAnswers = 8).clarityBand)
        assertEquals(ClarityBand.VERY_EVASIVE, InteractionStats(questionAnswers = 10).clarityBand)
    }
}
