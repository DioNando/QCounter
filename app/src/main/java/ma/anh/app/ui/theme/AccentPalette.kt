package ma.anh.app.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

/** Couleurs des indicateurs (Directe / Question / Esquive) + 4ᵉ catégorie optionnelle. */
data class AccentPalette(
    val name: String,
    val direct: Color,
    val question: Color,
    val unknown: Color,
    val custom: Color,
)

/** Presets fixes sélectionnables dans les réglages. */
val AppPalettes: List<AccentPalette> = listOf(
    AccentPalette(
        name = "Soleil",              // palette de marque (par défaut)
        direct = Color(0xFFF2B705),   // jaune doré (clarté) — assombri pour rester lisible
        question = Color(0xFFD5442D), // rouge de marque (esquive)
        unknown = Color(0xFFBFA94E),  // jaune-or grisé (indécision)
        custom = Color(0xFF7E57C2),   // violet (4ᵉ catégorie)
    ),
    AccentPalette(
        name = "Lagon",               // turquoise / corail / bleu-gris
        direct = Color(0xFF12A4A4),   // turquoise
        question = Color(0xFFFF5A5F), // corail
        unknown = Color(0xFF4E6E81),  // bleu-gris
        custom = Color(0xFF9B59B6),   // améthyste
    ),
    AccentPalette(
        name = "Ciel",                // bleu / rouge / ardoise
        direct = Color(0xFF2D7FF9),   // bleu vif
        question = Color(0xFFE8453C), // rouge
        unknown = Color(0xFF6B7A8F),  // gris ardoise
        custom = Color(0xFF8E44AD),   // violet
    ),
    AccentPalette(
        name = "Forêt",               // vert / orange brûlé / taupe
        direct = Color(0xFF2F9E44),   // vert feuille
        question = Color(0xFFE8590C), // orange brûlé
        unknown = Color(0xFF7A6C5D),  // taupe
        custom = Color(0xFF5C6BC0),   // indigo
    ),
    AccentPalette(
        name = "Crépuscule",          // coucher de soleil : orange / rose / violet
        direct = Color(0xFFF76707),   // orange
        question = Color(0xFFE64980), // rose magenta
        unknown = Color(0xFF7048E8),  // violet
        custom = Color(0xFF12A4A4),   // turquoise
    ),
)

/** Palette d'accent courante, fournie au niveau racine selon les réglages. */
val LocalAccentPalette = compositionLocalOf { AppPalettes[0] }
