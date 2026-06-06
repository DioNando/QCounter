package ma.wave.qcounter.ui.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import kotlin.math.sqrt

/** Seuil de force (en g) au-delà duquel on considère qu'il y a une secousse franche. */
private const val SHAKE_THRESHOLD_G = 2.7f

/** Délai minimal entre deux secousses prises en compte (anti-rebond), en ms. */
private const val SHAKE_DEBOUNCE_MS = 1_000L

/**
 * Enregistre un écouteur d'accéléromètre tant que le composable est présent et appelle
 * [onShake] lorsqu'une secousse franche est détectée. Sans effet si l'appareil n'a pas
 * d'accéléromètre. [enabled] permet d'activer/désactiver la détection.
 */
@Composable
fun ShakeToAction(enabled: Boolean = true, onShake: () -> Unit) {
    val context = LocalContext.current
    val currentOnShake by rememberUpdatedState(onShake)

    DisposableEffect(enabled) {
        if (!enabled) return@DisposableEffect onDispose { }

        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (sensorManager == null || accelerometer == null) {
            return@DisposableEffect onDispose { }
        }

        var lastShakeTimestamp = 0L
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val (x, y, z) = event.values
                val gForce = sqrt(x * x + y * y + z * z) / SensorManager.GRAVITY_EARTH
                if (gForce > SHAKE_THRESHOLD_G) {
                    val now = event.timestamp / 1_000_000L // ns → ms
                    if (now - lastShakeTimestamp >= SHAKE_DEBOUNCE_MS) {
                        lastShakeTimestamp = now
                        currentOnShake()
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        onDispose { sensorManager.unregisterListener(listener) }
    }
}
