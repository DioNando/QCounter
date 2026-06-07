package ma.anh.app.ui.charts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ma.anh.app.data.model.AnswerType
import ma.anh.app.data.model.HeatmapData
import ma.anh.app.data.model.InteractionStats
import ma.anh.app.data.model.StreakStats
import ma.anh.app.data.repository.InteractionRepository

/** Alimente la page Graphiques : heatmap d'activité + meilleures séries par type. */
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

    /** Compteurs exacts par catégorie, pour la carte de détail. */
    val stats: StateFlow<InteractionStats> = repository.stats
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = InteractionStats(),
        )

    /** Plus longue série par type (Oui/Non inclus séparément), pour la carte « Records ». */
    val records: StateFlow<List<Pair<AnswerType, Int>>> = repository.history
        .map { rows ->
            StreakStats.bestByType(rows.map { it.type })
                .toList()
                .sortedBy { it.first.ordinal }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )
}
