package ma.wave.qcounter.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ma.wave.qcounter.data.model.AnswerType
import ma.wave.qcounter.data.model.InteractionStats
import ma.wave.qcounter.data.repository.InteractionRepository

class HomeViewModel(
    private val repository: InteractionRepository,
) : ViewModel() {

    /** KPI exposés à l'UI, recalculés en continu depuis la base. */
    val stats: StateFlow<InteractionStats> = repository.stats.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = InteractionStats(),
    )

    fun record(type: AnswerType) {
        viewModelScope.launch { repository.record(type) }
    }

    fun reset() {
        viewModelScope.launch { repository.reset() }
    }
}
