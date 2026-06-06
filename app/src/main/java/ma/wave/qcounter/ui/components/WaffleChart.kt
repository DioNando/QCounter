package ma.wave.qcounter.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import ma.wave.qcounter.data.model.InteractionStats
import ma.wave.qcounter.ui.theme.LocalAccentPalette
import kotlin.math.floor
import kotlin.math.roundToInt

/**
 * Pictogramme « waffle » pleine largeur : grille de 100 points (20×5), chaque point ≈ 1 %.
 * Les points se remplissent en s'animant au lancement (Directe → Question → Esquive).
 */
@Composable
fun WaffleChart(
    stats: InteractionStats,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAccentPalette.current
    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    val counts = distribute(
        listOf(stats.directAnswers, stats.questionAnswers, stats.unknownAnswers),
        cells = CELLS,
    )
    val cellColors = ArrayList<Color>(CELLS).apply {
        repeat(counts[0]) { add(palette.direct) }
        repeat(counts[1]) { add(palette.question) }
        repeat(counts[2]) { add(palette.unknown) }
        while (size < CELLS) add(trackColor)
    }

    // Remplissage progressif au lancement.
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }
    val progress by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(800),
        label = "waffle-progress",
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(COLUMNS.toFloat() / ROWS),
    ) {
        val visible = (progress * CELLS).roundToInt().coerceIn(0, CELLS)
        val cell = size.width / COLUMNS
        val radius = cell * 0.36f
        for (index in 0 until CELLS) {
            val row = index / COLUMNS
            val col = index % COLUMNS
            val center = Offset(
                x = col * cell + cell / 2f,
                y = row * cell + cell / 2f,
            )
            val color = if (index < visible) cellColors[index] else trackColor
            drawCircle(color = color, radius = radius, center = center)
        }
    }
}

private const val COLUMNS = 20
private const val ROWS = 5
private const val CELLS = 100

/** Répartit [cells] cases entre les valeurs (méthode du plus fort reste, somme exacte). */
private fun distribute(values: List<Int>, cells: Int): List<Int> {
    val sum = values.sum()
    if (sum == 0) return List(values.size) { 0 }
    val raw = values.map { it.toDouble() / sum * cells }
    val result = raw.map { floor(it).toInt() }.toMutableList()
    var remaining = cells - result.sum()
    val order = raw.indices.sortedByDescending { raw[it] - floor(raw[it]) }
    var i = 0
    while (remaining > 0) {
        result[order[i % order.size]]++
        remaining--
        i++
    }
    return result
}
