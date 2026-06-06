package ma.wave.qcounter

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import kotlinx.coroutines.launch
import ma.wave.qcounter.data.model.AnswerType
import ma.wave.qcounter.ui.widget.refreshAllWidgets

/**
 * Activité « trampoline » sans interface : déclenchée par les raccourcis du lanceur,
 * elle enregistre l'interaction demandée, affiche une confirmation, puis se ferme
 * immédiatement (l'app ne s'ouvre pas vraiment).
 */
class QuickRecordActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val type = intent?.getStringExtra(EXTRA_TYPE)
            ?.let { runCatching { AnswerType.valueOf(it) }.getOrNull() }

        if (type != null) {
            val app = application as QCounterApp
            app.applicationScope.launch {
                app.repository.record(type)
                refreshAllWidgets(applicationContext)
            }
            val label = getString(
                when (type) {
                    AnswerType.DIRECT -> R.string.legend_direct
                    AnswerType.QUESTION -> R.string.legend_question
                    AnswerType.UNKNOWN -> R.string.legend_unknown
                    AnswerType.CUSTOM -> R.string.legend_custom
                    AnswerType.OUI -> R.string.action_yes
                    AnswerType.NON -> R.string.action_no
                },
            )
            Toast.makeText(this, getString(R.string.shortcut_recorded, label), Toast.LENGTH_SHORT).show()
        }

        finish()
    }

    companion object {
        const val EXTRA_TYPE = "qc_type"
    }
}
