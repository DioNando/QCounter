package ma.wave.qcounter.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ma.wave.qcounter.data.local.InteractionEntity
import ma.wave.qcounter.data.repository.InteractionRepository

class HistoryViewModel(
    private val repository: InteractionRepository,
) : ViewModel() {

    val history: StateFlow<List<InteractionEntity>> = repository.history.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    fun reset() {
        viewModelScope.launch { repository.reset() }
    }
}
