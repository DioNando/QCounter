package ma.wave.qcounter.ui.components

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ThumbsUpDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ma.wave.qcounter.R
import ma.wave.qcounter.data.model.AnswerType
import kotlin.math.roundToInt

/** Carte « Ratio Oui / Non » : emoji + jauge (Oui = accent, Non = nuance sombre) + décompte. */
@Composable
fun YesNoCard(yes: Int, no: Int, modifier: Modifier = Modifier, emoji: String? = null) {
    val total = yes + no
    val yesPct = if (total == 0) 0 else (yes.toFloat() / total * 100f).roundToInt()
    val noPct = if (total == 0) 0 else 100 - yesPct
    val accent = answerTypeVisual(AnswerType.DIRECT).accent
    val nonColor = darkerAccent(accent)
    val yesFraction by animateFloatAsState(
        targetValue = if (total == 0) 0f else yes.toFloat() / total,
        animationSpec = tween(600),
        label = "yesno-fraction",
    )

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
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // En-tête : badge (emoji si activé, sinon icône) + titre.
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(accent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    if (emoji != null) {
                        Text(text = emoji, fontSize = 20.sp)
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.ThumbsUpDown,
                            contentDescription = null,
                            tint = accent,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
                Text(
                    text = stringResource(R.string.yesno_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            // Jauge : portion « Oui » (accent), reste « Non » (nuance sombre).
            // Seul le conteneur est arrondi → pas d'arrondi à la jonction au milieu.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(nonColor),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(yesFraction.coerceIn(0f, 1f))
                        .height(14.dp)
                        .background(accent),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "${stringResource(R.string.action_yes)} · $yes ($yesPct%)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = accent,
                )
                Text(
                    text = "${stringResource(R.string.action_no)} · $no ($noPct%)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = nonColor,
                )
            }
        }
    }
}
