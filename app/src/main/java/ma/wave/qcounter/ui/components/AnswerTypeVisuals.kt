package ma.wave.qcounter.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material.icons.rounded.QuestionAnswer
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import ma.wave.qcounter.R
import ma.wave.qcounter.data.model.AnswerType
import ma.wave.qcounter.ui.theme.DirectGreen
import ma.wave.qcounter.ui.theme.QuestionIndigo
import ma.wave.qcounter.ui.theme.UnknownAmber

/** Métadonnées visuelles (libellé, couleur d'accent, icône) propres à chaque type de réponse. */
data class AnswerTypeVisual(
    val label: String,
    val accent: Color,
    val icon: ImageVector,
)

@Composable
fun answerTypeVisual(type: AnswerType): AnswerTypeVisual = when (type) {
    AnswerType.DIRECT -> AnswerTypeVisual(
        label = stringResource(R.string.action_direct),
        accent = DirectGreen,
        icon = Icons.Rounded.CheckCircle,
    )

    AnswerType.QUESTION -> AnswerTypeVisual(
        label = stringResource(R.string.action_question),
        accent = QuestionIndigo,
        icon = Icons.Rounded.QuestionAnswer,
    )

    AnswerType.UNKNOWN -> AnswerTypeVisual(
        label = stringResource(R.string.action_unknown),
        accent = UnknownAmber,
        icon = Icons.Rounded.HelpOutline,
    )
}
