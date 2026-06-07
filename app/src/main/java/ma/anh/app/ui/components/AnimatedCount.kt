package ma.anh.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

/**
 * Affiche un entier en l'animant vers sa nouvelle valeur, avec un léger « pop »
 * (mise à l'échelle) à chaque changement.
 */
@Composable
fun AnimatedCount(
    count: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
) {
    val animated by animateIntAsState(
        targetValue = count,
        animationSpec = tween(durationMillis = 450),
        label = "animated-count",
    )
    val scale = remember { Animatable(1f) }
    LaunchedEffect(count) {
        scale.animateTo(1.18f, animationSpec = tween(110))
        scale.animateTo(1f, animationSpec = spring())
    }
    Text(
        text = compactCount(animated),
        modifier = modifier.scale(scale.value),
        style = style,
        color = color,
    )
}
