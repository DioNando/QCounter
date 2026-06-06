package ma.wave.qcounter.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ma.wave.qcounter.data.model.AppSettings
import ma.wave.qcounter.data.model.EmojiIntensity
import ma.wave.qcounter.data.model.HomeChart

private val Context.dataStore by preferencesDataStore(name = "qcounter_settings")

/** Persiste les préférences (emoji, palette, graphique) via DataStore et les expose en Flow. */
class SettingsRepository(private val context: Context) {

    private object Keys {
        val SHOW_EMOJI = booleanPreferencesKey("show_emoji")
        val PALETTE = intPreferencesKey("palette_id")
        val HOME_CHART = intPreferencesKey("home_chart")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val EMOJI_SET = intPreferencesKey("emoji_set")
        val EMOJI_INTENSITY = intPreferencesKey("emoji_intensity")
    }

    val settings: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        val chartIndex = prefs[Keys.HOME_CHART] ?: 0
        val intensityIndex = prefs[Keys.EMOJI_INTENSITY] ?: EmojiIntensity.NORMAL.ordinal
        AppSettings(
            showEmoji = prefs[Keys.SHOW_EMOJI] ?: true,
            paletteId = prefs[Keys.PALETTE] ?: 0,
            homeChart = HomeChart.entries.getOrElse(chartIndex) { HomeChart.WAFFLE },
            dynamicColor = prefs[Keys.DYNAMIC_COLOR] ?: false,
            emojiSetId = prefs[Keys.EMOJI_SET] ?: 0,
            emojiIntensity = EmojiIntensity.entries.getOrElse(intensityIndex) { EmojiIntensity.NORMAL },
        )
    }

    suspend fun setShowEmoji(value: Boolean) {
        context.dataStore.edit { it[Keys.SHOW_EMOJI] = value }
    }

    suspend fun setPalette(id: Int) {
        context.dataStore.edit { it[Keys.PALETTE] = id }
    }

    suspend fun setHomeChart(chart: HomeChart) {
        context.dataStore.edit { it[Keys.HOME_CHART] = chart.ordinal }
    }

    suspend fun setDynamicColor(value: Boolean) {
        context.dataStore.edit { it[Keys.DYNAMIC_COLOR] = value }
    }

    suspend fun setEmojiSet(id: Int) {
        context.dataStore.edit { it[Keys.EMOJI_SET] = id }
    }

    suspend fun setEmojiIntensity(intensity: EmojiIntensity) {
        context.dataStore.edit { it[Keys.EMOJI_INTENSITY] = intensity.ordinal }
    }
}
