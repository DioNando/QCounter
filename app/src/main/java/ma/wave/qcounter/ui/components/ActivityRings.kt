package ma.wave.qcounter.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ma.wave.qcounter.data.model.InteractionStats
import ma.wave.qcounter.ui.theme.LocalAccentPalette

/**
 * Anneaux d'activité (style montre connectée) : 3 anneaux concentriques, un par type.
 * Chaque anneau est rempli selon la part du type dans le total. Animé au lancement.
 */
@Composable
fun ActivityRings(
    stats: InteractionStats,
    modifier: Modifier = Modifier,
    diameter: Dp = 180.dp,
    ringThickness: Dp = 16.dp,
    ringGap: Dp = 8.dp,
    content: @Composable () -> Unit = {},
) {
    val palette = LocalAccentPalette.current

    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }
    val factor = if (started) 1f else 0f

    val directFraction by animateFloatAsState(
        targetValue = (stats.directRatio / 100.0).toFloat() * factor,
        animationSpec = tween(800), label = "ring-direct",
    )
    val questionFraction by animateFloatAsState(
        targetValue = (stats.questionRatio / 100.0).toFloat() * factor,
        animationSpec = tween(800), label = "ring-question",
    )
    val unknownFraction by animateFloatAsState(
        targetValue = (stats.unknownRatio / 100.0).toFloat() * factor,
        animationSpec = tween(800), label = "ring-unknown",
    )

    Box(
        modifier = modifier.size(diameter),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(diameter)) {
            val stroke = ringThickness.toPx()
            val gap = ringGap.toPx()
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = (size.minDimension - stroke) / 2f

            val rings = listOf(
                palette.direct to directFraction,
                palette.question to questionFraction,
                palette.unknown to unknownFraction,
            )

            rings.forEachIndexed { i, (color, fraction) ->
                val radius = maxRadius - i * (stroke + gap)
                if (radius <= 0f) return@forEachIndexed
                val topLeft = Offset(center.x - radius, center.y - radius)
                val arcSize = Size(radius * 2f, radius * 2f)

                // Piste de fond
                drawArc(
                    color = color.copy(alpha = 0.18f),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                )
                // Remplissage
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = (fraction.coerceIn(0f, 1f)) * 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                )
            }
        }

        content()
    }
}
