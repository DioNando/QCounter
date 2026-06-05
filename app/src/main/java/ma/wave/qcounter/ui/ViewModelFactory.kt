package ma.wave.qcounter.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ma.wave.qcounter.data.repository.InteractionRepository
import ma.wave.qcounter.ui.history.HistoryViewModel
import ma.wave.qcounter.ui.home.HomeViewModel

/**
 * Fabrique unique injectant le repository partagé dans les ViewModels.
 * Évite d'ajouter Hilt pour une app de cette taille.
 */
class ViewModelFactory(
    private val repository: InteractionRepository,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(HomeViewModel::class.java) ->
            HomeViewModel(repository) as T

        modelClass.isAssignableFrom(HistoryViewModel::class.java) ->
            HistoryViewModel(repository) as T

        else -> throw IllegalArgumentException("ViewModel inconnu : ${modelClass.name}")
    }
}
