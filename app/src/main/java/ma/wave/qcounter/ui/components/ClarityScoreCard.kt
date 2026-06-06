package ma.wave.qcounter.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ma.wave.qcounter.R
import ma.wave.qcounter.data.model.ClarityBand
import ma.wave.qcounter.data.model.InteractionStats

/** Couleur associée à chaque palier d'interprétation (vert = clair → rouge = évasif). */
private fun bandColor(band: ClarityBand): Color = when (band) {
    ClarityBand.VERY_CLEAR -> Color(0xFF2E7D32)
    ClarityBand.CLEAR -> Color(0xFF66BB6A)
    ClarityBand.MIXED -> Color(0xFFF9A825)
    ClarityBand.EVASIVE -> Color(0xFFEF6C00)
    ClarityBand.VERY_EVASIVE -> Color(0xFFC62828)
}

@Composable
private fun bandLabel(band: ClarityBand): String = stringResource(
    when (band) {
        ClarityBand.VERY_CLEAR -> R.string.clarity_very_clear
        ClarityBand.CLEAR -> R.string.clarity_clear
        ClarityBand.MIXED -> R.string.clarity_mixed
        ClarityBand.EVASIVE -> R.string.clarity_evasive
        ClarityBand.VERY_EVASIVE -> R.string.clarity_very_evasive
    },
)

@Composable
private fun bandInterpretation(band: ClarityBand): String = stringResource(
    when (band) {
        ClarityBand.VERY_CLEAR -> R.string.clarity_desc_very_clear
        ClarityBand.CLEAR -> R.string.clarity_desc_clear
        ClarityBand.MIXED -> R.string.clarity_desc_mixed
        ClarityBand.EVASIVE -> R.string.clarity_desc_evasive
        ClarityBand.VERY_EVASIVE -> R.string.clarity_desc_very_evasive
    },
)

/** Carte « Indice de Clarté » : score synthétique /100, jauge colorée et interprétation. */
@Composable
fun ClarityScoreCard(
    stats: InteractionStats,
    modifier: Modifier = Modifier,
    emoji: String? = null,
) {
    val band = stats.clarityBand
    val color by animateColorAsState(bandColor(band), tween(450), label = "clarity-color")
    val progress by animateFloatAsState(
        targetValue = stats.clarityScore / 100f,
        animationSpec = tween(600),
        label = "clarity-progress",
    )

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (emoji != null) {
                        Text(text = emoji, fontSize = 24.sp)
                    }
                    Text(
                        text = stringResource(R.string.clarity_title),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = stats.clarityScore.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = color,
                    )
                    Text(
                        text = "/100",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp, start = 2.dp),
                    )
                }
            }

            // Jauge horizontale arrondie.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.coerceIn(0f, 1f))
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(color),
                )
            }

            Text(
                text = bandLabel(band),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = color,
            )
            Text(
                text = bandInterpretation(band),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
