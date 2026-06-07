package ma.anh.app.ui.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ma.anh.app.R
import ma.anh.app.data.model.AnswerType
import ma.anh.app.ui.theme.QCounterTheme

/** Écran de configuration du widget 1×1 : l'utilisateur choisit l'action enregistrée. */
class CompactWidgetConfigActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Par défaut, annuler : si l'utilisateur sort sans choisir, le widget n'est pas posé.
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
                    ConfigContent(onPick = { type -> applyAndFinish(appWidgetId, type) })
                }
            }
        }
    }

    private fun applyAndFinish(appWidgetId: Int, type: AnswerType) {
        lifecycleScope.launch {
            val glanceId = GlanceAppWidgetManager(this@CompactWidgetConfigActivity)
                .getGlanceIdBy(appWidgetId)
            updateAppWidgetState(
                this@CompactWidgetConfigActivity,
                PreferencesGlanceStateDefinition,
                glanceId,
            ) { prefs ->
                prefs.toMutablePreferences().apply { this[CompactActionKey] = type.name }
            }
            QCounterCompactWidget().update(this@CompactWidgetConfigActivity, glanceId)
            setResult(
                Activity.RESULT_OK,
                Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId),
            )
            finish()
        }
    }
}

@Composable
private fun ConfigContent(onPick: (AnswerType) -> Unit) {
    WidgetConfigScreen(title = stringResource(R.string.widget_config_title)) {
        WidgetTypeChoice(type = AnswerType.DIRECT, onClick = { onPick(AnswerType.DIRECT) })
        WidgetTypeChoice(type = AnswerType.QUESTION, onClick = { onPick(AnswerType.QUESTION) })
        WidgetTypeChoice(type = AnswerType.UNKNOWN, onClick = { onPick(AnswerType.UNKNOWN) })
    }
}
