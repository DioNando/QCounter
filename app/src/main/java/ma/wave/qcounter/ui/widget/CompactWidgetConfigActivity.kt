package ma.wave.qcounter.ui.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ma.wave.qcounter.R
import ma.wave.qcounter.data.model.AnswerType
import ma.wave.qcounter.ui.theme.QCounterTheme

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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.widget_config_title),
            style = MaterialTheme.typography.titleLarge,
        )
        FilledTonalButton(
            onClick = { onPick(AnswerType.DIRECT) },
            modifier = Modifier.fillMaxWidth(),
        ) { Text(stringResource(R.string.action_direct)) }
        FilledTonalButton(
            onClick = { onPick(AnswerType.QUESTION) },
            modifier = Modifier.fillMaxWidth(),
        ) { Text(stringResource(R.string.action_question)) }
        FilledTonalButton(
            onClick = { onPick(AnswerType.UNKNOWN) },
            modifier = Modifier.fillMaxWidth(),
        ) { Text(stringResource(R.string.action_unknown)) }
    }
}
