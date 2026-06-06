package ma.anh.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ma.anh.app.R
import ma.anh.app.data.model.StreakStats

/**
 * Carte « Séries » : série en cours et record, colorées par le type concerné. Comme les séries
 * portent sur les types réels, une suite de Oui (ou de Non) s'affiche directement (ex. « ×3 Oui »).
 */
@Composable
fun StreakCard(streaks: StreakStats, modifier: Modifier = Modifier) {
    val currentType = streaks.currentType ?: return
    val current = answerTypeVisual(currentType)
    val bestType = streaks.bestType
    val best = bestType?.let { answerTypeVisual(it) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            CardHeader(
                icon = Icons.Rounded.LocalFireDepartment,
                title = stringResource(R.string.streak_title),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StreakStat(
                    caption = stringResource(R.string.streak_current),
                    count = streaks.currentLength,
                    typeLabel = answerTypeShortLabel(currentType),
                    icon = current.icon,
                    accent = current.accent,
                    modifier = Modifier.weight(1f),
                )
                if (best != null && bestType != null) {
                    StreakStat(
                        caption = stringResource(R.string.streak_best),
                        count = streaks.bestLength,
                        typeLabel = answerTypeShortLabel(bestType),
                        icon = best.icon,
                        accent = best.accent,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun StreakStat(
    caption: String,
    count: Int,
    typeLabel: String,
    icon: ImageVector,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    // Tuile colorée par le type, avec un léger dégradé et l'icône en filigrane.
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(listOf(accent, lerp(accent, Color.Black, 0.22f))),
            ),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.16f),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(6.dp)
                .size(46.dp),
        )
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = caption,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.85f),
            )
            Text(
                text = "×$count",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                maxLines = 1,
            )
            Text(
                text = typeLabel,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                maxLines = 1,
            )
        }
    }
}

/** En-tête de carte : badge d'icône teinté + titre. */
@Composable
fun CardHeader(icon: ImageVector, title: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp),
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
