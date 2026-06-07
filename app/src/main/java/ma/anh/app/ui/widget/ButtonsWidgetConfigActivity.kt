package ma.anh.app.ui.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ma.anh.app.R
import ma.anh.app.data.model.AnswerType
import ma.anh.app.ui.theme.QCounterTheme

/** Écran de configuration du widget 2×1 : l'utilisateur choisit les **deux** types à afficher. */
class ButtonsWidgetConfigActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(Activity.RESULT_CANCELED)

        val appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID,
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        setContent {
            QCounterTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ButtonsConfigContent(
                        onConfirm = { first, second -> applyAndFinish(appWidgetId, first, second) },
                    )
                }
            }
        }
    }

    private fun applyAndFinish(appWidgetId: Int, first: AnswerType, second: AnswerType) {
        lifecycleScope.launch {
            val glanceId = GlanceAppWidgetManager(this@ButtonsWidgetConfigActivity)
                .getGlanceIdBy(appWidgetId)
            updateAppWidgetState(
                this@ButtonsWidgetConfigActivity,
                PreferencesGlanceStateDefinition,
                glanceId,
            ) { prefs ->
                prefs.toMutablePreferences().apply {
                    this[ButtonsFirstKey] = first.name
                    this[ButtonsSecondKey] = second.name
                }
            }
            QCounterButtonsWidget().update(this@ButtonsWidgetConfigActivity, glanceId)
            setResult(
                Activity.RESULT_OK,
                Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId),
            )
            finish()
        }
    }
}

@Composable
private fun ButtonsConfigContent(onConfirm: (AnswerType, AnswerType) -> Unit) {
    val options = listOf(AnswerType.DIRECT, AnswerType.QUESTION, AnswerType.UNKNOWN)
    // Liste ordonnée d'au plus 2 types choisis (par défaut : Directe + Question).
    var chosen by remember { mutableStateOf(listOf(AnswerType.DIRECT, AnswerType.QUESTION)) }

    WidgetConfigScreen(title = stringResource(R.string.widget_config_title_two)) {
        options.forEach { type ->
            WidgetTypeChoice(
                type = type,
                selected = type in chosen,
                onClick = {
                    chosen = when {
                        type in chosen -> chosen - type
                        chosen.size < 2 -> chosen + type
                        else -> listOf(chosen[1], type) // remplace le plus ancien
                    }
                },
            )
        }
        Spacer(Modifier.height(4.dp))
        Button(
            onClick = { if (chosen.size == 2) onConfirm(chosen[0], chosen[1]) },
            enabled = chosen.size == 2,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.confirm))
        }
    }
}
