package ma.wave.qcounter.ui.charts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ma.wave.qcounter.data.model.HeatmapData
import ma.wave.qcounter.data.repository.InteractionRepository

/** Alimente la page Graphiques : grille d'activité dérivée des horodatages de l'historique. */
class ChartsViewModel(
    repository: InteractionRepository,
) : ViewModel() {

    val heatmap: StateFlow<HeatmapData> = repository.history
        .map { rows -> HeatmapData.from(rows.map { it.timestamp }) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HeatmapData(),
        )
}
