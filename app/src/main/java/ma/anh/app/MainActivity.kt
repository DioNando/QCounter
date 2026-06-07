package ma.anh.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import ma.anh.app.data.model.AppSettings
import ma.anh.app.ui.ViewModelFactory
import ma.anh.app.ui.components.LocalAnswerLabels
import ma.anh.app.ui.components.rememberAnswerLabels
import ma.anh.app.ui.navigation.QCounterNavHost
import ma.anh.app.ui.theme.AccentPalette
import ma.anh.app.ui.theme.AppPalettes
import ma.anh.app.ui.theme.LocalAccentPalette
import ma.anh.app.ui.theme.QCounterTheme

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as QCounterApp
        val factory = ViewModelFactory(app.repository)
        val settingsRepository = app.settingsRepository

        setContent {
            val scope = rememberCoroutineScope()
            // null tant que DataStore n'a pas émis : on n'affiche aucun contenu avant de connaître
            // l'état du verrou discret (évite tout flash de l'accueil au démarrage).
            val loadedSettings by settingsRepository.settings
                .collectAsStateWithLifecycle(initialValue = null)
            val settings = loadedSettings ?: AppSettings()
            val target = AppPalettes.getOrElse(settings.paletteId) { AppPalettes[0] }

            // Transition douce des couleurs d'indicateurs lors d'un changement de palette.
            val animSpec = tween<androidx.compose.ui.graphics.Color>(durationMillis = 450)
            val direct by animateColorAsState(target.direct, animSpec, label = "accent-direct")
            val question by animateColorAsState(target.question, animSpec, label = "accent-question")
            val unknown by animateColorAsState(target.unknown, animSpec, label = "accent-unknown")
            val custom by animateColorAsState(target.custom, animSpec, label = "accent-custom")
            val palette = AccentPalette(target.name, direct, question, unknown, custom)

            val answerLabels = rememberAnswerLabels(settings.labels)

            QCounterTheme(dynamicColor = settings.dynamicColor) {
                CompositionLocalProvider(
                    LocalAccentPalette provides palette,
                    LocalAnswerLabels provides answerLabels,
                ) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        // Rien tant que les réglages ne sont pas chargés (évite tout flash de l'accueil).
                        if (loadedSettings != null) {
                            QCounterNavHost(
                            factory = factory,
                            settings = settings,
                            onSetDiscreet = { scope.launch { settingsRepository.setDiscreet(it) } },
                            onSetCompactActions = { scope.launch { settingsRepository.setCompactActions(it) } },
                            onSetShowEmoji = { scope.launch { settingsRepository.setShowEmoji(it) } },
                            onSetPalette = { scope.launch { settingsRepository.setPalette(it) } },
                            onSetHomeChart = { scope.launch { settingsRepository.setHomeChart(it) } },
                            onSetDynamicColor = { scope.launch { settingsRepository.setDynamicColor(it) } },
                            onSetEmojiSet = { scope.launch { settingsRepository.setEmojiSet(it) } },
                            onSetEmojiIntensity = { scope.launch { settingsRepository.setEmojiIntensity(it) } },
                            onSetLongLabel = { type, value -> scope.launch { settingsRepository.setLongLabel(type, value) } },
                            onSetShortLabel = { type, value -> scope.launch { settingsRepository.setShortLabel(type, value) } },
                            onSetCustomEnabled = { scope.launch { settingsRepository.setCustomEnabled(it) } },
                            )
                        }
                    }
                }
            }
        }
    }
}
