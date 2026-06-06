package ma.anh.app.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.ThumbDown
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.material.icons.rounded.Undo
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ma.anh.app.R
import ma.anh.app.data.model.AnswerType
import ma.anh.app.data.model.AppSettings
import ma.anh.app.data.model.EmojiIntensity
import ma.anh.app.data.model.HomeChart
import ma.anh.app.data.model.InteractionStats
import ma.anh.app.ui.components.ActionCard
import ma.anh.app.ui.components.ActivityRings
import ma.anh.app.ui.components.AnimatedCount
import ma.anh.app.ui.components.DonutChart
import ma.anh.app.ui.components.LegendItem
import ma.anh.app.ui.components.WaffleChart
import ma.anh.app.ui.components.SettingsSheet
import ma.anh.app.ui.components.ClarityScoreCard
import ma.anh.app.ui.components.YesNoCard
import ma.anh.app.ui.components.EmojiSet
import ma.anh.app.ui.components.StreakCard
import ma.anh.app.ui.components.answerTypeShortLabel
import ma.anh.app.ui.components.answerTypeVisual
import ma.anh.app.ui.components.darkerAccent
import ma.anh.app.ui.components.clarityEmoji
import ma.anh.app.ui.components.emojiSetOf
import ma.anh.app.ui.components.yesNoEmoji
import ma.anh.app.ui.components.moodEmoji
import ma.anh.app.ui.util.ShakeToAction
import ma.anh.app.ui.util.SecureFlagEffect
import ma.anh.app.ui.util.rememberBiometricReveal
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenHistory: () -> Unit,
    onOpenCharts: () -> Unit,
    settings: AppSettings,
    onSetDiscreet: (Boolean) -> Unit,
    onSetShowEmoji: (Boolean) -> Unit,
    onSetPalette: (Int) -> Unit,
    onSetHomeChart: (HomeChart) -> Unit,
    onSetDynamicColor: (Boolean) -> Unit,
    onSetEmojiSet: (Int) -> Unit,
    onSetEmojiIntensity: (EmojiIntensity) -> Unit,
    onSetLongLabel: (AnswerType, String) -> Unit,
    onSetShortLabel: (AnswerType, String) -> Unit,
    onSetCustomEnabled: (Boolean) -> Unit,
) {
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val streaks by viewModel.streaks.collectAsStateWithLifecycle()
    var showSettings by remember { mutableStateOf(false) }
    // Verrou discret persisté (DataStore) : reste verrouillé même après fermeture de l'app.
    val discreet = settings.discreet

    val haptics = LocalHapticFeedback.current

    // Annulation éphémère : un bouton flottant apparaît après chaque saisie puis disparaît,
    // et une secousse de l'appareil annule la dernière interaction (remplace le snackbar).
    var undoVisible by remember { mutableStateOf(false) }
    var eventToken by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        viewModel.events.collect { eventToken++ }
    }
    LaunchedEffect(eventToken) {
        if (eventToken == 0) return@LaunchedEffect
        undoVisible = true
        delay(4_000)
        undoVisible = false
    }

    fun undoLast() {
        if (stats.totalInteractions == 0) return
        viewModel.undoLast()
        undoVisible = false
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    // Secousse → annuler la dernière interaction (inactif en mode discret).
    ShakeToAction(enabled = !discreet) { undoLast() }

    // Mode discret : FLAG_SECURE (anti-capture / aperçu multitâche masqué) tant qu'il est actif…
    SecureFlagEffect(active = discreet)
    // …et déverrouillage biométrique exigé pour réafficher le contenu.
    val revealWithAuth = rememberBiometricReveal(onSuccess = { onSetDiscreet(false) })

    // Mode discret : on masque entièrement le contenu derrière un écran neutre.
    if (discreet) {
        DiscreetCover(onReveal = revealWithAuth)
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // Le logo contient déjà le titre « Anh » (titre adaptatif clair/sombre).
                    Image(
                        painter = painterResource(R.drawable.ic_homepage),
                        contentDescription = stringResource(R.string.logo_content_desc),
                        modifier = Modifier
                            .height(32.dp)
                            .width(73.dp),
                    )
                },
                actions = {
                    IconButton(onClick = { onSetDiscreet(true) }) {
                        Icon(
                            imageVector = Icons.Rounded.VisibilityOff,
                            contentDescription = stringResource(R.string.cd_discreet),
                            modifier = Modifier.size(24.dp),
                        )
                    }
                    IconButton(onClick = { showSettings = true }) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = stringResource(R.string.cd_settings),
                            modifier = Modifier.size(24.dp),
                        )
                    }
                },
            )
        },
        bottomBar = {
            ActionPanel(
                stats = stats,
                onRecord = viewModel::record,
                customEnabled = settings.customEnabled,
            )
        },
        floatingActionButton = {
            // Bouton flottant « Annuler » en bas à droite, qui s'efface après quelques secondes.
            AnimatedVisibility(
                visible = undoVisible,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                ExtendedFloatingActionButton(
                    onClick = { undoLast() },
                    icon = { Icon(Icons.Rounded.Undo, contentDescription = null) },
                    text = { Text(stringResource(R.string.action_undo)) },
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp,
                        focusedElevation = 0.dp,
                        hoveredElevation = 0.dp,
                    ),
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { innerPadding ->
        // Zone défilable : si tout tient (waffle), rien ne défile ; sinon
        // (anneau / anneaux, petits écrans) ça défile au lieu d'écraser la tuile.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            HeroCard(
                stats = stats,
                showEmoji = settings.showEmoji,
                homeChart = settings.homeChart,
                emojiSet = emojiSetOf(settings.emojiSetId),
                emojiIntensity = settings.emojiIntensity,
                modifier = Modifier.padding(top = 8.dp),
            )
            if (stats.totalInteractions > 0) {
                ClarityScoreCard(
                    stats = stats,
                    emoji = if (settings.showEmoji) {
                        clarityEmoji(stats.clarityBand, emojiSetOf(settings.emojiSetId))
                    } else {
                        null
                    },
                )
                StreakCard(streaks = streaks)
            }
            if (stats.yesNoTotal > 0) {
                YesNoCard(
                    yes = stats.yesAnswers,
                    no = stats.noAnswers,
                    emoji = if (settings.showEmoji) {
                        yesNoEmoji(stats.yesRatio, emojiSetOf(settings.emojiSetId))
                    } else {
                        null
                    },
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                NavTile(
                    label = stringResource(R.string.tile_history),
                    icon = Icons.Rounded.History,
                    onClick = onOpenHistory,
                    modifier = Modifier.weight(1f),
                )
                NavTile(
                    label = stringResource(R.string.tile_charts),
                    icon = Icons.Rounded.Insights,
                    onClick = onOpenCharts,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(4.dp))
        }
    }

    if (showSettings) {
        SettingsSheet(
            settings = settings,
            onSetShowEmoji = onSetShowEmoji,
            onSetPalette = onSetPalette,
            onSetHomeChart = onSetHomeChart,
            onSetDynamicColor = onSetDynamicColor,
            onSetEmojiSet = onSetEmojiSet,
            onSetEmojiIntensity = onSetEmojiIntensity,
            onSetLongLabel = onSetLongLabel,
            onSetShortLabel = onSetShortLabel,
            onSetCustomEnabled = onSetCustomEnabled,
            onDismiss = { showSettings = false },
        )
    }
}

/**
 * Écran neutre du mode discret : masque entièrement le contenu de l'app.
 * Un appui n'importe où réaffiche l'écran.
 */
@Composable
private fun DiscreetCover(onReveal: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onReveal() },
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(18.dp)),
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.discreet_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun HeroCard(
    stats: InteractionStats,
    showEmoji: Boolean,
    homeChart: HomeChart,
    emojiSet: EmojiSet,
    emojiIntensity: EmojiIntensity,
    modifier: Modifier = Modifier,
) {
    val direct = answerTypeVisual(AnswerType.DIRECT)
    val question = answerTypeVisual(AnswerType.QUESTION)
    val unknown = answerTypeVisual(AnswerType.UNKNOWN)

    // Légende interactive : type mis en avant (toucher pour (dé)sélectionner) → estompe les autres.
    var highlighted by remember { mutableStateOf<AnswerType?>(null) }
    fun toggle(type: AnswerType) { highlighted = if (highlighted == type) null else type }

    // Héro adouci : on éclaircit le conteneur de marque vers la surface pour un jaune moins agressif.
    val heroTop = lerp(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.surface, 0.45f)
    val heroBottom = lerp(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.surface, 0.25f)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(heroTop, heroBottom)))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.hero_volume_label).uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )

            // Pour le waffle, l'emoji est agrandi et placé en regard du total (voir branche WAFFLE).
            if (showEmoji && homeChart != HomeChart.WAFFLE) {
                Crossfade(targetState = moodEmoji(stats, emojiSet, emojiIntensity), label = "mood") { emoji ->
                    Text(text = emoji, fontSize = 34.sp)
                }
            }

            when (homeChart) {
                HomeChart.WAFFLE -> {
                    if (showEmoji) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Crossfade(
                                targetState = moodEmoji(stats, emojiSet, emojiIntensity),
                                label = "mood",
                            ) { emoji ->
                                Text(text = emoji, fontSize = 52.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                HeroVolume(stats.totalInteractions)
                            }
                        }
                    } else {
                        HeroVolume(stats.totalInteractions)
                    }
                    WaffleChart(
                        stats = stats,
                        modifier = Modifier.padding(vertical = 4.dp),
                        highlight = highlighted,
                    )
                }

                HomeChart.DONUT -> {
                    // L'anneau a un grand centre vide → on y place le total.
                    DonutChart(stats = stats, highlight = highlighted) {
                        HeroCenter(stats.totalInteractions)
                    }
                }

                HomeChart.RINGS -> {
                    // Les anneaux d'activité ont un petit centre → total au-dessus.
                    HeroVolume(stats.totalInteractions)
                    ActivityRings(stats = stats, highlight = highlighted)
                }
            }

            AnimatedVisibility(visible = stats.totalInteractions > 0) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        LegendItem(
                            color = direct.accent,
                            label = answerTypeShortLabel(AnswerType.DIRECT),
                            count = stats.directAnswers,
                            selected = highlighted == AnswerType.DIRECT,
                            dimmed = highlighted != null && highlighted != AnswerType.DIRECT,
                            onClick = { toggle(AnswerType.DIRECT) },
                        )
                        LegendItem(
                            color = question.accent,
                            label = answerTypeShortLabel(AnswerType.QUESTION),
                            count = stats.questionAnswers,
                            selected = highlighted == AnswerType.QUESTION,
                            dimmed = highlighted != null && highlighted != AnswerType.QUESTION,
                            onClick = { toggle(AnswerType.QUESTION) },
                        )
                        LegendItem(
                            color = unknown.accent,
                            label = answerTypeShortLabel(AnswerType.UNKNOWN),
                            count = stats.unknownAnswers,
                            selected = highlighted == AnswerType.UNKNOWN,
                            dimmed = highlighted != null && highlighted != AnswerType.UNKNOWN,
                            onClick = { toggle(AnswerType.UNKNOWN) },
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        MiniStat(stringResource(R.string.kpi_rc_short), stats.directRatio, direct.accent, Modifier.weight(1f))
                        MiniStat(stringResource(R.string.kpi_tel_short), stats.questionRatio, question.accent, Modifier.weight(1f))
                        MiniStat(stringResource(R.string.kpi_unknown_short), stats.unknownRatio, unknown.accent, Modifier.weight(1f))
                    }
                }
            }

            AnimatedVisibility(visible = stats.totalInteractions == 0) {
                Text(
                    text = stringResource(R.string.hero_empty_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

/** Total + unité affichés au-dessus du graphique (waffle / anneaux d'activité). */
@Composable
private fun HeroVolume(total: Int) {
    AnimatedCount(
        count = total,
        style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold),
        color = MaterialTheme.colorScheme.onPrimaryContainer,
    )
    Text(
        text = stringResource(R.string.hero_center_unit),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
    )
}

/** Contenu central (total + unité) pour l'anneau (grand centre vide). */
@Composable
private fun HeroCenter(total: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AnimatedCount(
            count = total,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        Text(
            text = stringResource(R.string.hero_center_unit),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}

/** Pilule KPI : pourcentage coloré sur fond clair (bon contraste) + libellé. */
@Composable
private fun MiniStat(
    label: String,
    percent: Double,
    accent: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = String.format(Locale.getDefault(), "%.0f%%", percent),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = accent,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/** Tuile d'accès : carte avec badge d'icône teinté + libellé (même langage que les cartes). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NavTile(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier.height(64.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 12.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(21.dp),
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

/** Bouton compact Oui / Non : carte tonale + badge d'icône (même langage que les cartes d'action). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun YesNoButton(
    label: String,
    count: Int,
    icon: ImageVector,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptic = LocalHapticFeedback.current
    Card(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(18.dp),
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
            )
            AnimatedCount(
                count = count,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = accent,
            )
        }
    }
}

/** Panneau ancré en bas : zone de saisie mise en avant et accessible au pouce. */
@Composable
private fun ActionPanel(
    stats: InteractionStats,
    onRecord: (AnswerType) -> Unit,
    customEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        shadowElevation = 8.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            ActionCard(
                label = answerTypeVisual(AnswerType.DIRECT).label,
                count = stats.directAnswers,
                icon = answerTypeVisual(AnswerType.DIRECT).icon,
                accent = answerTypeVisual(AnswerType.DIRECT).accent,
                onClick = { onRecord(AnswerType.DIRECT) },
            )
            // Oui / Non : réponses directes qualifiées, juste sous la carte Directe.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                YesNoButton(
                    label = stringResource(R.string.action_yes),
                    count = stats.yesAnswers,
                    icon = Icons.Rounded.ThumbUp,
                    accent = answerTypeVisual(AnswerType.DIRECT).accent,
                    onClick = { onRecord(AnswerType.OUI) },
                    modifier = Modifier.weight(1f),
                )
                YesNoButton(
                    label = stringResource(R.string.action_no),
                    count = stats.noAnswers,
                    icon = Icons.Rounded.ThumbDown,
                    accent = darkerAccent(answerTypeVisual(AnswerType.DIRECT).accent),
                    onClick = { onRecord(AnswerType.NON) },
                    modifier = Modifier.weight(1f),
                )
            }
            ActionCard(
                label = answerTypeVisual(AnswerType.QUESTION).label,
                count = stats.questionAnswers,
                icon = answerTypeVisual(AnswerType.QUESTION).icon,
                accent = answerTypeVisual(AnswerType.QUESTION).accent,
                onClick = { onRecord(AnswerType.QUESTION) },
            )
            ActionCard(
                label = answerTypeVisual(AnswerType.UNKNOWN).label,
                count = stats.unknownAnswers,
                icon = answerTypeVisual(AnswerType.UNKNOWN).icon,
                accent = answerTypeVisual(AnswerType.UNKNOWN).accent,
                onClick = { onRecord(AnswerType.UNKNOWN) },
            )
            if (customEnabled) {
                ActionCard(
                    label = answerTypeVisual(AnswerType.CUSTOM).label,
                    count = stats.customAnswers,
                    icon = answerTypeVisual(AnswerType.CUSTOM).icon,
                    accent = answerTypeVisual(AnswerType.CUSTOM).accent,
                    onClick = { onRecord(AnswerType.CUSTOM) },
                )
            }
        }
    }
}
