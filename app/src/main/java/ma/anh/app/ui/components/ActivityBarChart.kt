package ma.anh.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ma.anh.app.R

private val MaxBarHeight = 104.dp
private val ValueRowHeight = 16.dp

/**
 * Barres verticales sur un rail de fond (chaque créneau reste visible même à 0),
 * avec valeurs optionnelles au-dessus.
 */
@Composable
private fun BarRow(
    values: List<Int>,
    showValues: Boolean,
    barFraction: Float,
    modifier: Modifier = Modifier,
) {
    val max = values.maxOrNull() ?: 0
    val barColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(MaxBarHeight + ValueRowHeight),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        values.forEach { value ->
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
            ) {
                Box(
                    modifier = Modifier.height(ValueRowHeight),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    if (showValues && value > 0) {
                        Text(
                            text = "$value",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                // Rail de fond (hauteur pleine) + barre colorée alignée en bas.
                Box(
                    modifier = Modifier
                        .fillMaxWidth(barFraction)
                        .height(MaxBarHeight)
                        .clip(RoundedCornerShape(4.dp))
                        .background(trackColor),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    val fraction = if (max <= 0) 0f else value.toFloat() / max
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(MaxBarHeight * fraction)
                            .background(barColor),
                    )
                }
            }
        }
    }
}

/** Fine ligne de base sous les barres. */
@Composable
private fun Baseline() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.outlineVariant),
    )
}

/** Graphique « activité par jour de la semaine » (7 barres, lundi → dimanche). */
@Composable
fun WeekdayBarChart(perDay: List<Int>, modifier: Modifier = Modifier) {
    val days = androidx.compose.ui.res.stringArrayResource(R.array.weekday_initials)
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        BarRow(values = perDay, showValues = true, barFraction = 0.55f)
        Baseline()
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            perDay.indices.forEach { index ->
                Text(
                    text = days.getOrElse(index) { "" },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

/** Graphique « activité par heure » (24 barres, repères 0/6/12/18/23 h). */
@Composable
fun HourlyBarChart(perHour: List<Int>, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        BarRow(values = perHour, showValues = false, barFraction = 0.85f)
        Baseline()
        Row(
            modifier = Modifier.fillMaxWidth(),
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
