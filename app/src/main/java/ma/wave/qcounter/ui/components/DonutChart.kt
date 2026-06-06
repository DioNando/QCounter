package ma.wave.qcounter.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import ma.wave.qcounter.data.model.InteractionStats
import ma.wave.qcounter.ui.theme.LocalAccentPalette

/**
 * Anneau (donut) montrant la répartition des trois types de réponse.
 * Se remplit en s'animant au lancement, puis suit les changements de compteurs.
 * [content] est centré au milieu.
 */
@Composable
fun DonutChart(
    stats: InteractionStats,
    modifier: Modifier = Modifier,
    diameter: Dp = 184.dp,
    ringThickness: Dp = 24.dp,
    content: @Composable () -> Unit = {},
) {
    val palette = LocalAccentPalette.current
    val total = stats.totalInteractions.coerceAtLeast(1)

    // Joue l'animation de remplissage depuis 0 au premier affichage.
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }
    val factor = if (started) 1f else 0f

    val directSweep by animateFloatAsState(
        targetValue = stats.directAnswers.toFloat() / total * 360f * factor,
        animationSpec = tween(700),
        label = "donut-direct",
    )
    val questionSweep by animateFloatAsState(
        targetValue = stats.questionAnswers.toFloat() / total * 360f * factor,
        animationSpec = tween(700),
        label = "donut-question",
    )
    val unknownSweep by animateFloatAsState(
        targetValue = stats.unknownAnswers.toFloat() / total * 360f * factor,
        animationSpec = tween(700),
        label = "donut-unknown",
    )

    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val hasData = stats.totalInteractions > 0

    Box(
        modifier = modifier.size(diameter),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(diameter)) {
            val stroke = ringThickness.toPx()
            val arcSize = Size(size.width - stroke, size.height - stroke)
            val topLeft = Offset(stroke / 2f, stroke / 2f)
            val cap = StrokeCap.Butt

            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )

            if (hasData) {
                var start = -90f
                drawArc(
                    color = palette.direct,
                    startAngle = start,
                    sweepAngle = directSweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = cap),
                )
                start += directSweep
                drawArc(
                    color = palette.question,
                    startAngle = start,
                    sweepAngle = questionSweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = cap),
                )
                start += questionSweep
                drawArc(
                    color = palette.unknown,
                    startAngle = start,
                    sweepAngle = unknownSweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = cap),
                )
            }
        }

        content()
    }
}
