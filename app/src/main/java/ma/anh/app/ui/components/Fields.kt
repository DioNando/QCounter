package ma.anh.app.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Style commun des champs de saisie — RÈGLE MAÎTRE de la charte (« champs tonals sans bordure ») :
 * un `TextField` rempli (jamais `OutlinedTextField`), conteneur `surfaceContainerHighest`, tous les
 * indicateurs transparents (zéro ligne/bordure) et de grands coins arrondis identiques partout.
 *
 * À appliquer sur chaque champ : `shape = QCounterFieldShape, colors = qcounterFieldColors()`.
 */
val QCounterFieldShape = RoundedCornerShape(16.dp)

@Composable
fun qcounterFieldColors(): TextFieldColors = TextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
    errorContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    disabledIndicatorColor = Color.Transparent,
    errorIndicatorColor = Color.Transparent,
)
