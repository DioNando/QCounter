package ma.wave.qcounter.ui.tile

import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ma.wave.qcounter.QCounterApp
import ma.wave.qcounter.R
import ma.wave.qcounter.data.model.AnswerType
import ma.wave.qcounter.ui.widget.refreshAllWidgets

/**
 * Tuile Quick Settings : un appui enregistre une « Question par une Question » (cas d'usage
 * principal de l'app) sans l'ouvrir, et le sous-titre affiche le total courant.
 */
class QCounterTileService : TileService() {

    private val app get() = applicationContext as QCounterApp

    override fun onStartListening() {
        super.onStartListening()
        refreshTile()
    }

    override fun onClick() {
        super.onClick()
        app.applicationScope.launch {
            app.repository.record(AnswerType.QUESTION)
            refreshAllWidgets(applicationContext)
            updateTile()
        }
    }

    private fun refreshTile() {
        app.applicationScope.launch { updateTile() }
    }

    private suspend fun updateTile() {
        val total = app.repository.stats.first().totalInteractions
        withContext(Dispatchers.Main) {
            qsTile?.apply {
                label = getString(R.string.tile_qs_label)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    subtitle = getString(R.string.tile_qs_subtitle, total)
                }
                state = Tile.STATE_INACTIVE
                updateTile()
            }
        }
    }
}
