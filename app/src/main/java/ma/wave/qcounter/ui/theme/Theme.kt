package ma.wave.qcounter.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = SkyBlue,
    onPrimary = Color.White,
    primaryContainer = SkyContainerLight,
    onPrimaryContainer = OnSkyContainerLight,
    secondary = AccentUnknown,
    tertiary = AccentQuestion,
)

private val DarkColors = darkColorScheme(
    primary = SkyBlue80,
    onPrimary = Color(0xFF00344C),
    primaryContainer = SkyContainerDark,
    onPrimaryContainer = OnSkyContainerDark,
    secondary = Color(0xFFB7C2D0),
    tertiary = Color(0xFFFF8A80),
)

/**
 * Thème de marque QCounter. Les couleurs dynamiques (Material You) sont
 * désactivées par défaut afin de garantir la palette bleu ciel / rouge / gris.
 */
@Composable
fun QCounterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content,
    )
}
