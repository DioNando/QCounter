package ma.anh.app.ui.theme

import androidx.compose.ui.graphics.Color

// Charte de marque (logo) : jaune #ffe167 / #fdd05e, rouge #d5442d.
val BrandYellow = Color(0xFFFFE167)
val BrandYellowDeep = Color(0xFFFDD05E)
val BrandRed = Color(0xFFD5442D)

// Accents d'indicateurs de la palette par défaut « Lagon ».
val IndicatorDirect = Color(0xFF1E8A8A)   // sarcelle (clarté)
val IndicatorQuestion = BrandRed          // rouge (esquive en miroir)
val IndicatorUnknown = Color(0xFF6B6B72)  // gris (indécision)

// Thème clair : surfaces jaunes, accents de premier plan en encre foncée chaude.
val InkLight = Color(0xFF2E2A22)
val OnYellowLight = Color(0xFF2A2410)
val SecondaryLight = Color(0xFF5E5326)

// Thème sombre : accents en jaune (clairs sur fond sombre), conteneurs sombres chauds.
val YellowDark = Color(0xFFFFE167)
val OnYellowDark = Color(0xFF322B00)
val ContainerDark = Color(0xFF4A3D10)
val OnContainerDark = Color(0xFFFFE6A0)

// Accents legacy (composants non utilisés : DistributionBar / KpiDashboard) — alignés sur la marque.
val AccentDirect = IndicatorDirect
val AccentQuestion = IndicatorQuestion
val AccentUnknown = IndicatorUnknown
