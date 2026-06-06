package ma.wave.qcounter.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ma.wave.qcounter.R
import ma.wave.qcounter.data.model.AnswerType
import ma.wave.qcounter.data.model.StreakStats

/** Carte « Séries » : série en cours et record, avec icône/couleur du type concerné. */
@Composable
fun StreakCard(streaks: StreakStats, modifier: Modifier = Modifier) {
    val currentType = streaks.currentType ?: return

    val current = answerTypeVisual(currentType)
    val best = streaks.bestType?.let { answerTypeVisual(it) }
    val bestType = streaks.bestType

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
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.LocalFireDepartment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = stringResource(R.string.streak_title),
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                StreakStat(
                    caption = stringResource(R.string.streak_current),
                    count = streaks.currentLength,
                    typeLabel = shortLabel(currentType),
                    accent = current.accent,
                    modifier = Modifier.weight(1f),
                )
                if (best != null && bestType != null) {
                    StreakStat(
                        caption = stringResource(R.string.streak_best),
                        count = streaks.bestLength,
                        typeLabel = shortLabel(bestType),
                        accent = best.accent,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

/** Libellé court du type (Directe / Question / Esquive), adapté aux espaces étroits. */
@Composable
private fun shortLabel(type: AnswerType): String = stringResource(
    when (type) {
        AnswerType.DIRECT -> R.string.legend_direct
        AnswerType.QUESTION -> R.string.legend_question
        AnswerType.UNKNOWN -> R.string.legend_unknown
    },
)

@Composable
private fun StreakStat(
    caption: String,
    count: Int,
    typeLabel: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(accent),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "×$count",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
            )
        }
        Column {
            Text(
                text = caption,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = typeLabel,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
            )
        }
    }
}
