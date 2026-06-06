package ma.wave.qcounter.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import java.util.Locale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ma.wave.qcounter.R
import ma.wave.qcounter.data.model.AnswerType
import ma.wave.qcounter.data.model.InteractionStats
import ma.wave.qcounter.ui.components.ActionCard
import ma.wave.qcounter.ui.components.AnimatedCount
import ma.wave.qcounter.ui.components.DonutChart
import ma.wave.qcounter.ui.components.LegendItem
import ma.wave.qcounter.ui.components.answerTypeVisual
import ma.wave.qcounter.ui.theme.AccentDirect
import ma.wave.qcounter.ui.theme.AccentQuestion
import ma.wave.qcounter.ui.theme.AccentUnknown

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenHistory: () -> Unit,
) {
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val recordedTemplate = stringResource(R.string.snackbar_recorded)
    val undoLabel = stringResource(R.string.action_undo)
    val labels = mapOf(
        AnswerType.DIRECT to stringResource(R.string.action_direct),
        AnswerType.QUESTION to stringResource(R.string.action_question),
        AnswerType.UNKNOWN to stringResource(R.string.action_unknown),
    )

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            snackbarHostState.currentSnackbarData?.dismiss()
            val result = snackbarHostState.showSnackbar(
                message = recordedTemplate.format(labels[event.type]),
                actionLabel = undoLabel,
                duration = SnackbarDuration.Short,
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.undo(event.id)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                    IconButton(onClick = onOpenHistory) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.List,
                            contentDescription = stringResource(R.string.nav_history),
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
        ) {
            HeroCard(
                stats = stats,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun HeroCard(
    stats: InteractionStats,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.hero_volume_label).uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )

            DonutChart(stats = stats) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AnimatedCount(
                        count = stats.totalInteractions,
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Text(
                        text = stringResource(R.string.hero_center_unit),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }

            AnimatedVisibility(visible = stats.totalInteractions > 0) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        LegendItem(AccentDirect, stringResource(R.string.legend_direct), stats.directAnswers)
                        LegendItem(AccentQuestion, stringResource(R.string.legend_question), stats.questionAnswers)
                        LegendItem(AccentUnknown, stringResource(R.string.legend_unknown), stats.unknownAnswers)
                    }
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f),
                    )
                    KpiSummary(stats = stats)
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

/** Ligne compacte des KPI (pourcentages) affichée dans le héro, sous la légende. */
@Composable
private fun KpiSummary(
    stats: InteractionStats,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        MiniStat(stringResource(R.string.kpi_rc_short), stats.directRatio, AccentDirect)
        MiniStat(stringResource(R.string.kpi_tel_short), stats.questionRatio, AccentQuestion)
        MiniStat(stringResource(R.string.kpi_unknown_short), stats.unknownRatio, AccentUnknown)
    }
}

@Composable
private fun MiniStat(
    label: String,
    percent: Double,
    accent: androidx.compose.ui.graphics.Color,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = String.format(Locale.getDefault(), "%.0f%%", percent),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = accent,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.TouchApp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = stringResource(R.string.action_panel_title).uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

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
