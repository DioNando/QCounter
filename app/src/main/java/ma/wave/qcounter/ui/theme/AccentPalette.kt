package ma.wave.qcounter.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

/** Trio de couleurs des indicateurs (Directe / Question / Esquive). */
data class AccentPalette(
    val name: String,
    val direct: Color,
    val question: Color,
    val unknown: Color,
)

/** Presets fixes sélectionnables dans les réglages. */
val AppPalettes: List<AccentPalette> = listOf(
    AccentPalette(
        name = "Soleil",              // palette de marque (par défaut)
        direct = Color(0xFFF2B705),   // jaune doré (clarté) — assombri pour rester lisible
        question = Color(0xFFD5442D), // rouge de marque (esquive)
        unknown = Color(0xFFBFA94E),  // jaune-or grisé (indécision)
    ),
    AccentPalette(
        name = "Lagon",               // alternative sarcelle / rouge / gris
        direct = Color(0xFF1E8A8A),   // sarcelle (clarté)
        question = Color(0xFFD5442D), // rouge de marque (esquive)
        unknown = Color(0xFF6B6B72),  // gris (indécision)
    ),
    AccentPalette(
        name = "Ciel",
        direct = Color(0xFF0288D1),   // bleu ciel
        question = Color(0xFFE53935), // rouge
        unknown = Color(0xFF64748B),  // gris ardoise
    ),
    AccentPalette(
        name = "Forêt",
        direct = Color(0xFF059669),   // émeraude
        question = Color(0xFFD97706), // ambre
        unknown = Color(0xFF475569),  // ardoise foncé
    ),
    AccentPalette(
        name = "Crépuscule",
        direct = Color(0xFF4F46E5),   // indigo
        question = Color(0xFFDB2777), // magenta
        unknown = Color(0xFF0D9488),  // sarcelle
    ),
)

/** Palette d'accent courante, fournie au niveau racine selon les réglages. */
val LocalAccentPalette = compositionLocalOf { AppPalettes[0] }
