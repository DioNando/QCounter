package ma.wave.qcounter.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ma.wave.qcounter.data.model.AnswerType
import ma.wave.qcounter.data.model.InteractionStats
import ma.wave.qcounter.data.model.StreakStats
import ma.wave.qcounter.data.repository.InteractionRepository

/** Événement émis après un enregistrement, pour proposer l'annulation. */
data class RecordedEvent(val id: Long, val type: AnswerType)

class HomeViewModel(
    private val repository: InteractionRepository,
) : ViewModel() {

    /** KPI exposés à l'UI, recalculés en continu depuis la base. */
    val stats: StateFlow<InteractionStats> = repository.stats.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = InteractionStats(),
    )

    /** Séries (en cours / record) exposées à l'UI. */
    val streaks: StateFlow<StreakStats> = repository.streaks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = StreakStats(),
    )

    // On ne garde que le dernier événement : un appui rapide remplace le snackbar précédent.
    private val _events = MutableSharedFlow<RecordedEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val events: SharedFlow<RecordedEvent> = _events

    fun record(type: AnswerType) {
        viewModelScope.launch {
            val id = repository.record(type)
            _events.emit(RecordedEvent(id, type))
        }
    }

    fun undo(id: Long) {
        viewModelScope.launch { repository.deleteById(id) }
    }

    /** Annule la dernière interaction (bouton flottant / secousse). */
    fun undoLast() {
        viewModelScope.launch { repository.deleteLast() }
    }

    fun reset() {
        viewModelScope.launch { repository.reset() }
    }
}
