package ma.wave.qcounter.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ma.wave.qcounter.R
import ma.wave.qcounter.data.model.AnswerType
import ma.wave.qcounter.data.model.InteractionStats
import ma.wave.qcounter.ui.components.ActionCard
import ma.wave.qcounter.ui.components.AnimatedCount
import ma.wave.qcounter.ui.components.DistributionBar
import ma.wave.qcounter.ui.components.KpiDashboard
import ma.wave.qcounter.ui.components.LegendItem
import ma.wave.qcounter.ui.components.ResetConfirmationDialog
import ma.wave.qcounter.ui.components.answerTypeVisual
import ma.wave.qcounter.ui.theme.DirectGreen
import ma.wave.qcounter.ui.theme.QuestionIndigo
import ma.wave.qcounter.ui.theme.UnknownAmber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenHistory: () -> Unit,
) {
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(
                        onClick = { showResetDialog = true },
                        enabled = stats.totalInteractions > 0,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.RestartAlt,
                            contentDescription = stringResource(R.string.action_reset),
                        )
                    }
                    IconButton(onClick = onOpenHistory) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.List,
                            contentDescription = stringResource(R.string.nav_history),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            HeroCard(stats = stats)

            ActionCard(
                label = answerTypeVisual(AnswerType.DIRECT).label,
                count = stats.directAnswers,
                icon = answerTypeVisual(AnswerType.DIRECT).icon,
                accent = answerTypeVisual(AnswerType.DIRECT).accent,
                onClick = { viewModel.record(AnswerType.DIRECT) },
            )
            ActionCard(
                label = answerTypeVisual(AnswerType.QUESTION).label,
                count = stats.questionAnswers,
                icon = answerTypeVisual(AnswerType.QUESTION).icon,
                accent = answerTypeVisual(AnswerType.QUESTION).accent,
                onClick = { viewModel.record(AnswerType.QUESTION) },
            )
            ActionCard(
                label = answerTypeVisual(AnswerType.UNKNOWN).label,
                count = stats.unknownAnswers,
                icon = answerTypeVisual(AnswerType.UNKNOWN).icon,
                accent = answerTypeVisual(AnswerType.UNKNOWN).accent,
                onClick = { viewModel.record(AnswerType.UNKNOWN) },
            )

            KpiDashboard(stats = stats)
        }
    }

    if (showResetDialog) {
        ResetConfirmationDialog(
            onConfirm = {
                viewModel.reset()
                showResetDialog = false
            },
            onDismiss = { showResetDialog = false },
        )
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
            AnimatedCount(
                count = stats.totalInteractions,
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )

            DistributionBar(stats = stats)

            AnimatedVisibility(visible = stats.totalInteractions > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    LegendItem(DirectGreen, stringResource(R.string.legend_direct), stats.directAnswers)
                    LegendItem(QuestionIndigo, stringResource(R.string.legend_question), stats.questionAnswers)
                    LegendItem(UnknownAmber, stringResource(R.string.legend_unknown), stats.unknownAnswers)
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
