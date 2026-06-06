package ma.anh.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ma.anh.app.data.model.InteractionStats
import ma.anh.app.ui.theme.AccentDirect
import ma.anh.app.ui.theme.AccentQuestion
import ma.anh.app.ui.theme.AccentUnknown

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
                            .background(AccentDirect),
                    )
                }
                if (questionFraction > 0f) {
                    Box(
                        Modifier
                            .weight(questionFraction)
                            .fillMaxHeight()
                            .background(AccentQuestion),
                    )
                }
                if (unknownFraction > 0f) {
                    Box(
                        Modifier
                            .weight(unknownFraction)
                            .fillMaxHeight()
                            .background(AccentUnknown),
                    )
                }
            }
        }
    }
}

/**
 * Légende : une pastille colorée, un libellé court et le compte associé.
 *
 * Interactive si [onClick] est fourni : un toucher met le type « en avant » ([selected]),
 * tandis que [dimmed] estompe les autres entrées pour focaliser le regard.
 */
@Composable
fun LegendItem(
    color: Color,
    label: String,
    count: Int,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    dimmed: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val targetAlpha = if (dimmed) 0.4f else 1f
    val itemAlpha by animateFloatAsState(targetAlpha, tween(220), label = "legend-alpha")

    val interactive = onClick != null
    val shape = RoundedCornerShape(50)
    Row(
        modifier = modifier
            .clip(shape)
            .then(if (interactive) Modifier.clickable { onClick!!() } else Modifier)
            .then(if (selected) Modifier.background(color.copy(alpha = 0.18f)) else Modifier)
            .alpha(itemAlpha)
            .padding(horizontal = if (interactive) 8.dp else 0.dp, vertical = 4.dp),
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
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
        )
    }
}
