package ma.anh.app.ui.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.WindowManager
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import ma.anh.app.R

/** Remonte la chaîne des [ContextWrapper] jusqu'à l'Activity hôte (ou null). */
fun Context.findActivity(): Activity? {
    var current: Context? = this
    while (current is ContextWrapper) {
        if (current is Activity) return current
        current = current.baseContext
    }
    return null
}

/**
 * Applique (et retire) `FLAG_SECURE` sur la fenêtre selon [active].
 * En mode discret, cela masque le contenu dans l'aperçu multitâche et bloque les captures d'écran.
 */
@Composable
fun SecureFlagEffect(active: Boolean) {
    val context = LocalContext.current
    DisposableEffect(active) {
        val window = context.findActivity()?.window
        if (active) {
            window?.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE,
            )
        } else {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
        onDispose { window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE) }
    }
}

/**
 * Fournit une action qui demande une authentification biométrique avant d'exécuter [onSuccess].
 *
 * - Si l'appareil dispose d'une biométrie utilisable, une invite système s'affiche et [onSuccess]
 *   n'est appelé qu'en cas de succès.
 * - Si aucune biométrie n'est configurée (ou l'hôte n'est pas une [FragmentActivity]), on n'enferme
 *   pas l'utilisateur : [onSuccess] est exécuté directement.
 */
@Composable
fun rememberBiometricReveal(onSuccess: () -> Unit): () -> Unit {
    val context = LocalContext.current
    val latestOnSuccess = rememberUpdatedState(onSuccess)
    val title = stringResource(R.string.biometric_title)
    val subtitle = stringResource(R.string.biometric_subtitle)
    val negative = stringResource(R.string.cancel)

    return remember(context, title, subtitle, negative) {
        {
            val activity = context.findActivity() as? FragmentActivity
            val canAuthenticate = activity != null &&
                BiometricManager.from(activity).canAuthenticate(
                    BiometricManager.Authenticators.BIOMETRIC_WEAK,
                ) == BiometricManager.BIOMETRIC_SUCCESS

            if (activity == null || !canAuthenticate) {
                latestOnSuccess.value()
            } else {
                val prompt = BiometricPrompt(
                    activity,
                    ContextCompat.getMainExecutor(activity),
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationSucceeded(
                            result: BiometricPrompt.AuthenticationResult,
                        ) {
                            latestOnSuccess.value()
                        }
                    },
                )
                val info = BiometricPrompt.PromptInfo.Builder()
                    .setTitle(title)
                    .setSubtitle(subtitle)
                    .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
                    .setNegativeButtonText(negative)
                    .build()
                prompt.authenticate(info)
            }
        }
    }
}
