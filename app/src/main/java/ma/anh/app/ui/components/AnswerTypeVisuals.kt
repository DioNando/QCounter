package ma.anh.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material.icons.rounded.QuestionAnswer
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material.icons.rounded.ThumbDown
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import ma.anh.app.R
import ma.anh.app.data.model.AnswerLabels
import ma.anh.app.data.model.AnswerType
import ma.anh.app.ui.theme.LocalAccentPalette

/** Nuance plus sombre d'un accent (utilisée pour différencier « Non » de « Oui »). */
fun darkerAccent(color: Color): Color = lerp(color, Color.Black, 0.30f)

/** Métadonnées visuelles (libellé, couleur d'accent, icône) propres à chaque type de réponse. */
data class AnswerTypeVisual(
    val label: String,
    val accent: Color,
    val icon: ImageVector,
)

/** Libellés (longs et courts) résolus pour les 3 types, fournis au niveau racine. */
data class AnswerLabelProvider(
    val long: Map<AnswerType, String>,
    val short: Map<AnswerType, String>,
)

/** null = repli sur les libellés par défaut traduits (utile pour les previews). */
val LocalAnswerLabels = staticCompositionLocalOf<AnswerLabelProvider?> { null }

/**
 * Résout les libellés personnalisés en complétant les valeurs vides par les libellés
 * par défaut traduits. À fournir via [LocalAnswerLabels] au niveau racine.
 */
@Composable
fun rememberAnswerLabels(labels: AnswerLabels): AnswerLabelProvider {
    val directDef = stringResource(R.string.action_direct)
    val questionDef = stringResource(R.string.action_question)
    val unknownDef = stringResource(R.string.action_unknown)
    val customDef = stringResource(R.string.action_custom)
    val directShortDef = stringResource(R.string.legend_direct)
    val questionShortDef = stringResource(R.string.legend_question)
    val unknownShortDef = stringResource(R.string.legend_unknown)
    val customShortDef = stringResource(R.string.legend_custom)

    fun pick(custom: String?, default: String) =
        custom?.takeIf { it.isNotBlank() } ?: default

    return AnswerLabelProvider(
        long = mapOf(
            AnswerType.DIRECT to pick(labels.direct, directDef),
            AnswerType.QUESTION to pick(labels.question, questionDef),
            AnswerType.UNKNOWN to pick(labels.unknown, unknownDef),
            AnswerType.CUSTOM to pick(labels.custom, customDef),
        ),
        short = mapOf(
            AnswerType.DIRECT to pick(labels.directShort, directShortDef),
            AnswerType.QUESTION to pick(labels.questionShort, questionShortDef),
            AnswerType.UNKNOWN to pick(labels.unknownShort, unknownShortDef),
            AnswerType.CUSTOM to pick(labels.customShort, customShortDef),
        ),
    )
}

@Composable
fun answerTypeVisual(type: AnswerType): AnswerTypeVisual {
    val palette = LocalAccentPalette.current
    val customLong = LocalAnswerLabels.current?.long?.get(type)
    return when (type) {
        AnswerType.DIRECT -> AnswerTypeVisual(
            label = customLong ?: stringResource(R.string.action_direct),
            accent = palette.direct,
            icon = Icons.Rounded.CheckCircle,
        )

        AnswerType.QUESTION -> AnswerTypeVisual(
            label = customLong ?: stringResource(R.string.action_question),
            accent = palette.question,
            icon = Icons.Rounded.QuestionAnswer,
        )

        AnswerType.UNKNOWN -> AnswerTypeVisual(
            label = customLong ?: stringResource(R.string.action_unknown),
            accent = palette.unknown,
            icon = Icons.Rounded.HelpOutline,
        )

        AnswerType.CUSTOM -> AnswerTypeVisual(
            label = customLong ?: stringResource(R.string.action_custom),
            accent = palette.custom,
            icon = Icons.Rounded.StarOutline,
        )

        AnswerType.OUI -> AnswerTypeVisual(
            label = stringResource(R.string.action_yes),
            accent = palette.direct, // Oui = réponse directe → même accent
            icon = Icons.Rounded.ThumbUp,
        )

        AnswerType.NON -> AnswerTypeVisual(
            label = stringResource(R.string.action_no),
            accent = darkerAccent(palette.direct), // Non = réponse directe, nuance plus sombre
            icon = Icons.Rounded.ThumbDown,
        )
    }
}

/** Libellé court d'un type (légende, séries), personnalisable. */
@Composable
fun answerTypeShortLabel(type: AnswerType): String {
    val custom = LocalAnswerLabels.current?.short?.get(type)
    return custom ?: stringResource(
        when (type) {
            AnswerType.DIRECT -> R.string.legend_direct
            AnswerType.QUESTION -> R.string.legend_question
            AnswerType.UNKNOWN -> R.string.legend_unknown
            AnswerType.CUSTOM -> R.string.legend_custom
            AnswerType.OUI -> R.string.action_yes
            AnswerType.NON -> R.string.action_no
        },
    )
}
