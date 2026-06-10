package ma.anh.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ma.anh.app.R

/**
 * Marque animée « Anh » : logo bulle + mot-symbole, utilisée par l'écran de verrouillage (mode discret).
 *
 * Animations : apparition en fondu + glissement (logo puis mot-symbole), puis **battement** prononcé
 * et léger flottement continus du logo.
 *
 * @param showHint affiche l'indice « Touchez pour déverrouiller » (écran de verrouillage).
 */
@Composable
fun AnhBrandMark(
    modifier: Modifier = Modifier,
    showHint: Boolean = false,
) {
    val infinite = rememberInfiniteTransition(label = "brand")
    val breathe by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(tween(1300, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "breathe",
    )
    val floatY by infinite.animateFloat(
        initialValue = 0f,
        targetValue = -7f,
        animationSpec = infiniteRepeatable(tween(2400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "float",
    )
    // Apparition : fondu + glissement, le logo d'abord puis le mot-symbole (léger décalage).
    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { appeared = true }
    val enterLogo by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "enter-logo",
    )
    val enterText by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(600, delayMillis = 220, easing = FastOutSlowInEasing),
        label = "enter-text",
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_verrouillage),
            contentDescription = null,
            modifier = Modifier
                .size(160.dp)
                .graphicsLayer {
                    alpha = enterLogo
                    val s = breathe * (0.85f + 0.15f * enterLogo)
                    scaleX = s
                    scaleY = s
                    translationY = floatY + (1f - enterLogo) * 28f
                },
        )
        Spacer(Modifier.height(20.dp))
        // Nom de l'app en texte, même couleur que sur l'accueil (primary).
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.graphicsLayer {
                alpha = enterText
                translationY = (1f - enterText) * 18f
            },
        )
        if (showHint) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.discreet_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.graphicsLayer { alpha = enterText },
            )
        }
    }
}
