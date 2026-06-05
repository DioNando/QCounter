package ma.wave.qcounter.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ma.wave.qcounter.data.model.InteractionStats
import ma.wave.qcounter.ui.theme.DirectGreen
import ma.wave.qcounter.ui.theme.QuestionIndigo
import ma.wave.qcounter.ui.theme.UnknownAmber

/**
 * Barre horizontale segmentée montrant la proportion de chaque type de réponse.
 * Les segments s'animent quand les compteurs changent.
 */
@Composable
fun DistributionBar(
    stats: InteractionStats,
    modifier: Modifier = Modifier,
) {
    val total = stats.totalInteractions.coerceAtLeast(1)
    val directFraction by animateFloatAsState(
        targetValue = stats.directAnswers.toFloat() / total,
        animationSpec = tween(500),
        label = "direct-fraction",
    )
    val questionFraction by animateFloatAsState(
        targetValue = stats.questionAnswers.toFloat() / total,
        animationSpec = tween(500),
        label = "question-fraction",
    )
    val unknownFraction by animateFloatAsState(
        targetValue = stats.unknownAnswers.toFloat() / total,
        animationSpec = tween(500),
        label = "unknown-fraction",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(16.dp)
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        if (stats.totalInteractions > 0) {
            Row(modifier = Modifier.fillMaxSize()) {
                if (directFraction > 0f) {
                    Box(
                        Modifier
                            .weight(directFraction)
                            .fillMaxHeight()
                            .background(DirectGreen),
                    )
                }
                if (questionFraction > 0f) {
                    Box(
                        Modifier
                            .weight(questionFraction)
                            .fillMaxHeight()
                            .background(QuestionIndigo),
                    )
                }
                if (unknownFraction > 0f) {
                    Box(
                        Modifier
                            .weight(unknownFraction)
                            .fillMaxHeight()
                            .background(UnknownAmber),
                    )
                }
            }
        }
    }
}

/** Légende : une pastille colorée, un libellé court et le compte associé. */
@Composable
fun LegendItem(
    color: Color,
    label: String,
    count: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color),
        )
        Text(
            text = "$label · $count",
            style = MaterialTheme.typography.labelMedium,
        )
    }
}
