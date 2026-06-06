package ma.anh.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ma.anh.app.R
import ma.anh.app.data.model.HeatmapData

/** Largeur réservée aux libellés de jour, à gauche de la grille. */
private val DayLabelWidth = 22.dp
private val CellHeight = 16.dp
private val CellGap = 2.dp

/**
 * Heatmap d'activité : 7 lignes (jours, lundi en haut) × 24 colonnes (heures).
 * L'intensité de chaque cellule est proportionnelle au nombre d'interactions du créneau.
 */
@Composable
fun Heatmap(data: HeatmapData, modifier: Modifier = Modifier) {
    val empty = MaterialTheme.colorScheme.surfaceVariant
    val full = MaterialTheme.colorScheme.primary
    val days = stringArrayResource(R.array.weekday_initials)

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(CellGap)) {
        data.cells.forEachIndexed { dayIndex, hours ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(CellGap),
            ) {
                Text(
                    text = days.getOrElse(dayIndex) { "" },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(DayLabelWidth),
                )
                hours.forEach { count ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(CellHeight)
                            .clip(RoundedCornerShape(3.dp))
                            .background(cellColor(count, data.maxCount, empty, full)),
                    )
                }
            }
        }

        // Axe des heures : quelques repères régulièrement espacés sous la grille.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(CellGap),
        ) {
            Spacer(Modifier.width(DayLabelWidth))
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                listOf("0 h", "6 h", "12 h", "18 h", "23 h").forEach { label ->
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

/** Échelle de couleur de la légende (de « moins » à « plus »). */
@Composable
fun HeatmapLegend(modifier: Modifier = Modifier) {
    val empty = MaterialTheme.colorScheme.surfaceVariant
    val full = MaterialTheme.colorScheme.primary
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = stringArrayResource(R.array.heatmap_legend).getOrElse(0) { "" },
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        listOf(0f, 0.25f, 0.5f, 0.75f, 1f).forEach { fraction ->
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(if (fraction == 0f) empty else lerp(empty, full, 0.25f + 0.75f * fraction)),
            )
        }
        Text(
            text = stringArrayResource(R.array.heatmap_legend).getOrElse(1) { "" },
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** Couleur d'une cellule : gris neutre si vide, sinon dégradé vers la couleur primaire. */
private fun cellColor(count: Int, max: Int, empty: Color, full: Color): Color {
    if (count == 0 || max == 0) return empty
    val fraction = count.toFloat() / max
    return lerp(empty, full, 0.25f + 0.75f * fraction)
}
