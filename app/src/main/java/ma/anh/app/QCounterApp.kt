package ma.anh.app

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import ma.anh.app.data.local.QCounterDatabase
import ma.anh.app.data.repository.InteractionRepository
import ma.anh.app.data.repository.SettingsRepository

/**
 * Conteneur de dépendances minimaliste (pas de framework DI) : la base et les
 * repositories sont créés paresseusement et partagés par toute l'application.
 */
class QCounterApp : Application() {
    val database by lazy { QCounterDatabase.getInstance(this) }
    val repository by lazy { InteractionRepository(database.interactionDao()) }
    val settingsRepository by lazy { SettingsRepository(this) }

    /**
     * Portée liée au cycle de vie du process, pour les enregistrements « fire-and-forget »
     * déclenchés hors UI (raccourcis du lanceur, tuile Quick Settings, widget).
     */
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
}
