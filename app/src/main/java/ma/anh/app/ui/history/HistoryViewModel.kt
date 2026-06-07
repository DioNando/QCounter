package ma.anh.app.ui.history

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
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
import kotlinx.coroutines.withContext
import ma.anh.app.data.io.InteractionTransfer
import ma.anh.app.data.local.InteractionEntity
import ma.anh.app.data.model.AnswerType
import ma.anh.app.data.repository.InteractionRepository

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

    /** Granularité de regroupement (jour / semaine / mois). */
    private val _grouping = MutableStateFlow(HistoryGrouping.DAY)
    val grouping: StateFlow<HistoryGrouping> = _grouping.asStateFlow()

    /** Catégories masquées (filtre d'affichage). Vide = tout afficher. */
    private val _hiddenTypes = MutableStateFlow<Set<AnswerType>>(emptySet())
    val hiddenTypes: StateFlow<Set<AnswerType>> = _hiddenTypes.asStateFlow()

    /** Nombre d'interactions affichées (50 / 100 / 250). */
    private val _pageSize = MutableStateFlow(100)
    val pageSize: StateFlow<Int> = _pageSize.asStateFlow()

    fun setPageSize(value: Int) {
        _pageSize.value = value
    }

    fun setGrouping(value: HistoryGrouping) {
        _grouping.value = value
    }

    fun toggleType(type: AnswerType) {
        _hiddenTypes.update { if (type in it) it - type else it + type }
    }

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

    /**
     * Exporte l'historique en JSON vers [uri]. Si [onlyVisibleCategories] est vrai, n'exporte que
     * les catégories actuellement affichées (filtres) ; sinon tout. [onResult] reçoit true si succès.
     */
    fun exportTo(
        resolver: ContentResolver,
        uri: Uri,
        onlyVisibleCategories: Boolean,
        onResult: (Boolean) -> Unit,
    ) {
        viewModelScope.launch {
            val success = runCatching {
                val all = repository.exportAll()
                val items = if (onlyVisibleCategories) {
                    all.filterNot { it.type in _hiddenTypes.value }
                } else {
                    all
                }
                val json = InteractionTransfer.encode(items)
                withContext(Dispatchers.IO) {
                    resolver.openOutputStream(uri)?.use { it.write(json.toByteArray()) }
                        ?: error("flux de sortie indisponible")
                }
            }.isSuccess
            onResult(success)
        }
    }

    /**
     * Importe depuis [uri] en fusionnant (sans écraser ni dupliquer).
     * [onResult] reçoit le nombre ajouté, ou null en cas d'erreur de lecture/format.
     */
    fun importFrom(resolver: ContentResolver, uri: Uri, onResult: (Int?) -> Unit) {
        viewModelScope.launch {
            val added = runCatching {
                val text = withContext(Dispatchers.IO) {
                    resolver.openInputStream(uri)?.use { it.readBytes().decodeToString() }
                        ?: error("flux d'entrée indisponible")
                }
                repository.importMerging(InteractionTransfer.decode(text))
            }.getOrNull()
            onResult(added)
        }
    }
}
