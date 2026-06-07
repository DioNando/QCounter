package ma.anh.app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ma.anh.app.data.model.AnswerLabels
import ma.anh.app.data.model.AnswerType
import ma.anh.app.data.model.AppSettings
import ma.anh.app.data.model.EmojiIntensity
import ma.anh.app.data.model.HomeChart

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
        val CUSTOM_ENABLED = booleanPreferencesKey("custom_enabled")
        val DISCREET = booleanPreferencesKey("discreet")
        val COMPACT_ACTIONS = booleanPreferencesKey("compact_actions")

        // Libellés personnalisés (longs et courts) par type.
        fun longLabel(type: AnswerType) = stringPreferencesKey("label_long_${type.name}")
        fun shortLabel(type: AnswerType) = stringPreferencesKey("label_short_${type.name}")
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
            labels = AnswerLabels(
                direct = prefs[Keys.longLabel(AnswerType.DIRECT)],
                question = prefs[Keys.longLabel(AnswerType.QUESTION)],
                unknown = prefs[Keys.longLabel(AnswerType.UNKNOWN)],
                custom = prefs[Keys.longLabel(AnswerType.CUSTOM)],
                directShort = prefs[Keys.shortLabel(AnswerType.DIRECT)],
                questionShort = prefs[Keys.shortLabel(AnswerType.QUESTION)],
                unknownShort = prefs[Keys.shortLabel(AnswerType.UNKNOWN)],
                customShort = prefs[Keys.shortLabel(AnswerType.CUSTOM)],
            ),
            customEnabled = prefs[Keys.CUSTOM_ENABLED] ?: false,
            discreet = prefs[Keys.DISCREET] ?: false,
            compactActions = prefs[Keys.COMPACT_ACTIONS] ?: false,
        )
    }

    suspend fun setDiscreet(value: Boolean) {
        context.dataStore.edit { it[Keys.DISCREET] = value }
    }

    suspend fun setCompactActions(value: Boolean) {
        context.dataStore.edit { it[Keys.COMPACT_ACTIONS] = value }
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

    suspend fun setCustomEnabled(value: Boolean) {
        context.dataStore.edit { it[Keys.CUSTOM_ENABLED] = value }
    }

    /** Définit (ou réinitialise si vide) le libellé long d'un bouton. */
    suspend fun setLongLabel(type: AnswerType, value: String) {
        context.dataStore.edit { prefs ->
            val key = Keys.longLabel(type)
            if (value.isBlank()) prefs.remove(key) else prefs[key] = value.trim()
        }
    }

    /** Définit (ou réinitialise si vide) le libellé court d'un bouton. */
    suspend fun setShortLabel(type: AnswerType, value: String) {
        context.dataStore.edit { prefs ->
            val key = Keys.shortLabel(type)
            if (value.isBlank()) prefs.remove(key) else prefs[key] = value.trim()
        }
    }
}
