package ma.wave.qcounter.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ma.wave.qcounter.data.local.InteractionEntity
import ma.wave.qcounter.data.repository.InteractionRepository

/** Événement émis après une suppression, pour proposer l'annulation. */
data class DeletedEvent(val items: List<InteractionEntity>)

class HistoryViewModel(
    private val repository: InteractionRepository,
) : ViewModel() {

    val history: StateFlow<List<InteractionEntity>> = repository.history.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    private val _selectedIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedIds: StateFlow<Set<Long>> = _selectedIds.asStateFlow()

    private val _events = MutableSharedFlow<DeletedEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val events: SharedFlow<DeletedEvent> = _events

    fun toggleSelection(id: Long) {
        _selectedIds.update { current ->
            if (current.contains(id)) current - id else current + id
        }
    }

    fun selectAll() {
        _selectedIds.value = history.value.map { it.id }.toSet()
    }

    fun clearSelection() {
        _selectedIds.value = emptySet()
    }

    /** Supprime les éléments sélectionnés en mémorisant leur contenu pour l'annulation. */
    fun deleteSelected() {
        val toDelete = history.value.filter { _selectedIds.value.contains(it.id) }
        if (toDelete.isEmpty()) return
        viewModelScope.launch {
            repository.deleteByIds(toDelete.map { it.id })
            _events.emit(DeletedEvent(toDelete))
        }
        clearSelection()
    }

    /** Réinsère les éléments d'un événement de suppression. */
    fun undoDelete(items: List<InteractionEntity>) {
        viewModelScope.launch { repository.restore(items) }
    }

    fun reset() {
        viewModelScope.launch { repository.reset() }
        clearSelection()
    }
}
