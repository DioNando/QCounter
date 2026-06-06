package ma.anh.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = InkLight,
    onPrimary = Color.White,
    primaryContainer = BrandYellow,
    onPrimaryContainer = OnYellowLight,
    secondary = SecondaryLight,
    onSecondary = Color.White,
    secondaryContainer = BrandYellowDeep,
    onSecondaryContainer = OnYellowLight,
    tertiary = BrandRed,
    onTertiary = Color.White,
)

private val DarkColors = darkColorScheme(
    primary = YellowDark,
    onPrimary = OnYellowDark,
    primaryContainer = ContainerDark,
    onPrimaryContainer = OnContainerDark,
    secondary = Color(0xFFE0CE92),
    secondaryContainer = ContainerDark,
    onSecondaryContainer = OnContainerDark,
    tertiary = Color(0xFFFF8A78),
)

/** Material You disponible uniquement à partir d'Android 12 (API 31). */
val dynamicColorSupported: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

/**
 * Thème de marque QCounter. Par défaut on garantit la palette bleu ciel / rouge / gris,
 * mais l'utilisateur peut réactiver les couleurs dynamiques (Material You) sur Android 12+.
 */
@Composable
fun QCounterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && dynamicColorSupported -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
