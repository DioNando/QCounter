package ma.wave.qcounter.ui.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ma.wave.qcounter.R
import ma.wave.qcounter.data.model.AnswerType
import ma.wave.qcounter.ui.components.CardHeader
import ma.wave.qcounter.ui.components.answerTypeVisual
import ma.wave.qcounter.ui.components.Heatmap
import ma.wave.qcounter.ui.components.HeatmapLegend
import ma.wave.qcounter.ui.components.HourlyBarChart
import ma.wave.qcounter.ui.components.WeekdayBarChart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartsScreen(
    viewModel: ChartsViewModel,
    onBack: () -> Unit,
) {
    val heatmap by viewModel.heatmap.collectAsStateWithLifecycle()
    val records by viewModel.records.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.charts_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
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
                .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (records.isNotEmpty()) {
                ChartCard(
                    icon = Icons.Rounded.EmojiEvents,
                    title = stringResource(R.string.records_title),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        records.forEach { (type, length) ->
                            RecordRow(type = type, length = length)
                        }
                    }
                }
            }

            ChartCard(
                icon = Icons.Rounded.CalendarMonth,
                title = stringResource(R.string.heatmap_title),
            ) {
                if (heatmap.total == 0) {
                    Text(
                        text = stringResource(R.string.charts_empty),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                } else {
                    Text(
                        text = stringResource(R.string.heatmap_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Heatmap(data = heatmap)
                    HeatmapLegend(modifier = Modifier.align(Alignment.End))
                }
            }

            if (heatmap.total > 0) {
                ChartCard(
                    icon = Icons.Rounded.BarChart,
                    title = stringResource(R.string.weekday_chart_title),
                ) {
                    WeekdayBarChart(perDay = heatmap.perDay)
                }
                ChartCard(
                    icon = Icons.Rounded.Schedule,
                    title = stringResource(R.string.hourly_chart_title),
                ) {
                    HourlyBarChart(perHour = heatmap.perHour)
                }
            }
        }
    }
}

/** Une ligne « record » : badge d'icône du type + libellé + plus longue série. */
@Composable
private fun RecordRow(type: AnswerType, length: Int) {
    val visual = answerTypeVisual(type)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(visual.accent.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = visual.icon,
                contentDescription = null,
                tint = visual.accent,
                modifier = Modifier.size(20.dp),
            )
        }
        Text(
            text = visual.label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "×$length",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = visual.accent,
        )
    }
}

/** Carte titrée réutilisable (en-tête à badge d'icône) pour un graphique de la page. */
@Composable
private fun ChartCard(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CardHeader(icon = icon, title = title)
            content()
        }
    }
}
