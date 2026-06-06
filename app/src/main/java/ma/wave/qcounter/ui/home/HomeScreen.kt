package ma.wave.qcounter.ui.home

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Undo
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ma.wave.qcounter.R
import ma.wave.qcounter.data.model.AnswerType
import ma.wave.qcounter.data.model.AppSettings
import ma.wave.qcounter.data.model.EmojiIntensity
import ma.wave.qcounter.data.model.HomeChart
import ma.wave.qcounter.data.model.InteractionStats
import ma.wave.qcounter.ui.components.ActionCard
import ma.wave.qcounter.ui.components.ActivityRings
import ma.wave.qcounter.ui.components.AnimatedCount
import ma.wave.qcounter.ui.components.DonutChart
import ma.wave.qcounter.ui.components.LegendItem
import ma.wave.qcounter.ui.components.WaffleChart
import ma.wave.qcounter.ui.components.SettingsSheet
import ma.wave.qcounter.ui.components.ClarityScoreCard
import ma.wave.qcounter.ui.components.EmojiSet
import ma.wave.qcounter.ui.components.StreakCard
import ma.wave.qcounter.ui.components.answerTypeShortLabel
import ma.wave.qcounter.ui.components.answerTypeVisual
import ma.wave.qcounter.ui.components.clarityEmoji
import ma.wave.qcounter.ui.components.emojiSetOf
import ma.wave.qcounter.ui.components.moodEmoji
import ma.wave.qcounter.ui.util.ShakeToAction
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenHistory: () -> Unit,
    onOpenCharts: () -> Unit,
    settings: AppSettings,
    onSetShowEmoji: (Boolean) -> Unit,
    onSetPalette: (Int) -> Unit,
    onSetHomeChart: (HomeChart) -> Unit,
    onSetDynamicColor: (Boolean) -> Unit,
    onSetEmojiSet: (Int) -> Unit,
    onSetEmojiIntensity: (EmojiIntensity) -> Unit,
    onSetLongLabel: (AnswerType, String) -> Unit,
    onSetShortLabel: (AnswerType, String) -> Unit,
) {
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val streaks by viewModel.streaks.collectAsStateWithLifecycle()
    var showSettings by remember { mutableStateOf(false) }
    var discreet by rememberSaveable { mutableStateOf(false) }

    val haptics = LocalHapticFeedback.current
    val context = LocalContext.current

    // Import / export via le Storage Access Framework (sélecteur de fichiers système).
    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json"),
    ) { uri ->
        if (uri != null) viewModel.exportTo(context.contentResolver, uri) { ok ->
            Toast.makeText(
                context,
                context.getString(if (ok) R.string.export_success else R.string.export_error),
                Toast.LENGTH_SHORT,
            ).show()
        }
    }
    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri != null) viewModel.importFrom(context.contentResolver, uri) { added ->
            val message = if (added == null) {
                context.getString(R.string.import_error)
            } else {
                context.getString(R.string.import_success, added)
            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

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

    // Mode discret : on masque entièrement le contenu derrière un écran neutre.
    if (discreet) {
        DiscreetCover(onReveal = { discreet = false })
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_logo),
                            contentDescription = stringResource(R.string.logo_content_desc),
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp)),
                        )
                        Text(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                                    append("Q")
                                }
                                withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                                    append("Counter")
                                }
                            },
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { discreet = true }) {
                        Icon(
                            imageVector = Icons.Rounded.VisibilityOff,
                            contentDescription = stringResource(R.string.cd_discreet),
                            modifier = Modifier.size(24.dp),
                        )
                    }
                    IconButton(onClick = { showSettings = true }) {
                        Icon(
                            imageVector = Icons.Rounded.Tune,
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
            onExport = { exportLauncher.launch("qcounter-export.json") },
            onImport = {
                importLauncher.launch(
                    arrayOf("application/json", "text/plain", "application/octet-stream"),
                )
            },
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

    val heroTop = MaterialTheme.colorScheme.primaryContainer
    val heroBottom = lerp(heroTop, MaterialTheme.colorScheme.primary, 0.22f)

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
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
                    WaffleChart(stats = stats, modifier = Modifier.padding(vertical = 4.dp))
                }

                HomeChart.DONUT -> {
                    // L'anneau a un grand centre vide → on y place le total.
                    DonutChart(stats = stats) { HeroCenter(stats.totalInteractions) }
                }

                HomeChart.RINGS -> {
                    // Les anneaux d'activité ont un petit centre → total au-dessus.
                    HeroVolume(stats.totalInteractions)
                    ActivityRings(stats = stats)
                }
            }

            AnimatedVisibility(visible = stats.totalInteractions > 0) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        LegendItem(direct.accent, answerTypeShortLabel(AnswerType.DIRECT), stats.directAnswers)
                        LegendItem(question.accent, answerTypeShortLabel(AnswerType.QUESTION), stats.questionAnswers)
                        LegendItem(unknown.accent, answerTypeShortLabel(AnswerType.UNKNOWN), stats.unknownAnswers)
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
        shadowElevation = 1.dp,
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

/** Tuile d'accès : pilule tonale plate (icône + libellé), sans ombre. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NavTile(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = modifier.height(48.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.size(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

/** Panneau ancré en bas : zone de saisie mise en avant et accessible au pouce. */
@Composable
private fun ActionPanel(
    stats: InteractionStats,
    onRecord: (AnswerType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 12.dp,
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
        }
    }
}
