package ma.wave.qcounter.ui.theme

import androidx.compose.ui.graphics.Color

// Charte de marque (voir logo) : dégradé bleu clair #6dd5ed → bleu #2193b0.
val BrandBlueLight = Color(0xFF6DD5ED)
val BrandBlueDeep = Color(0xFF2193B0)
val BrandSlate = Color(0xFF46627E)   // ardoise neutre
val BrandRed = Color(0xFFE53935)     // accent secondaire (rouge)

// Thème clair + conteneurs doux.
val PrimaryLight = Color(0xFF1C82A0)      // bleu de marque assombri pour un bon contraste sur blanc
val ContainerLight = Color(0xFFBDE7F2)
val OnContainerLight = Color(0xFF05323F)

// Thème sombre.
val PrimaryDark = Color(0xFF6DD5ED)
val OnPrimaryDark = Color(0xFF003544)
val ContainerDark = Color(0xFF134B5C)
val OnContainerDark = Color(0xFFBDE7F2)

// Accents legacy (composants non utilisés : DistributionBar / KpiDashboard) — alignés sur la marque.
val AccentDirect = BrandBlueDeep       // Réponse Directe
val AccentQuestion = BrandRed          // Question par une Question
val AccentUnknown = BrandSlate         // Je ne sais pas / esquive
