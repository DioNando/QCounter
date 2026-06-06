package ma.wave.qcounter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import ma.wave.qcounter.data.model.AppSettings
import ma.wave.qcounter.ui.ViewModelFactory
import ma.wave.qcounter.ui.components.LocalAnswerLabels
import ma.wave.qcounter.ui.components.rememberAnswerLabels
import ma.wave.qcounter.ui.navigation.QCounterNavHost
import ma.wave.qcounter.ui.theme.AccentPalette
import ma.wave.qcounter.ui.theme.AppPalettes
import ma.wave.qcounter.ui.theme.LocalAccentPalette
import ma.wave.qcounter.ui.theme.QCounterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as QCounterApp
        val factory = ViewModelFactory(app.repository)
        val settingsRepository = app.settingsRepository

        setContent {
            val scope = rememberCoroutineScope()
            val settings by settingsRepository.settings
                .collectAsStateWithLifecycle(initialValue = AppSettings())
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
                        QCounterNavHost(
                            factory = factory,
                            settings = settings,
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
