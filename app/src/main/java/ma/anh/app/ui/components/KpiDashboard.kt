package ma.anh.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ma.anh.app.R
import ma.anh.app.data.model.InteractionStats
import ma.anh.app.ui.theme.AccentDirect
import ma.anh.app.ui.theme.AccentQuestion
import ma.anh.app.ui.theme.AccentUnknown

/** Carte des KPI : chaque indicateur est une barre de progression animée. */
@Composable
fun KpiDashboard(
    stats: InteractionStats,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(R.string.kpi_title),
                style = MaterialTheme.typography.titleMedium,
            )
            StatBar(
                label = stringResource(R.string.kpi_tel),
                percent = stats.questionRatio,
                accent = AccentQuestion,
            )
            StatBar(
                label = stringResource(R.string.kpi_rc),
                percent = stats.directRatio,
                accent = AccentDirect,
            )
            StatBar(
                label = stringResource(R.string.kpi_unknown),
                percent = stats.unknownRatio,
                accent = AccentUnknown,
            )
        }
    }
}
