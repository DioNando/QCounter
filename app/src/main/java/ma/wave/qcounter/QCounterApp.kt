package ma.wave.qcounter

import android.app.Application
import ma.wave.qcounter.data.local.QCounterDatabase
import ma.wave.qcounter.data.repository.InteractionRepository

/**
 * Conteneur de dépendances minimaliste (pas de framework DI) : la base et le
 * repository sont créés paresseusement et partagés par toute l'application.
 */
class QCounterApp : Application() {
    val database by lazy { QCounterDatabase.getInstance(this) }
    val repository by lazy { InteractionRepository(database.interactionDao()) }
}
