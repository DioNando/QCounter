package ma.anh.app.ui.components

import java.util.Locale
import kotlin.math.roundToInt

/**
 * Format compact d'un compteur (gain de place) : 1060 → « 1,1k », 12345 → « 12k »,
 * 2_000_000 → « 2M ». En dessous de 1000, le nombre est affiché tel quel.
 */
fun compactCount(n: Int): String = when {
    n < 1000 -> n.toString()
    n < 10_000 -> String.format(Locale.getDefault(), "%.1fk", n / 1000.0)
    n < 1_000_000 -> "${(n / 1000.0).roundToInt()}k"
    n < 10_000_000 -> String.format(Locale.getDefault(), "%.1fM", n / 1_000_000.0)
    else -> "${(n / 1_000_000.0).roundToInt()}M"
}
