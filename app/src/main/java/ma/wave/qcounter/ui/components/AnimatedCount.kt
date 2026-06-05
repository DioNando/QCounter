package ma.wave.qcounter.ui.components

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

/** Affiche un entier en l'animant en douceur vers sa nouvelle valeur. */
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
    Text(
        text = animated.toString(),
        modifier = modifier,
        style = style,
        color = color,
    )
}
