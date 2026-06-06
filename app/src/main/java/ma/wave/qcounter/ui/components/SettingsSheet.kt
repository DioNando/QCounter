package ma.wave.qcounter.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DonutLarge
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Mood
import androidx.compose.material.icons.rounded.TrackChanges
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ma.wave.qcounter.R
import ma.wave.qcounter.data.model.AppSettings
import ma.wave.qcounter.data.model.HomeChart
import ma.wave.qcounter.ui.theme.AppPalettes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    settings: AppSettings,
    onSetShowEmoji: (Boolean) -> Unit,
    onSetPalette: (Int) -> Unit,
    onSetHomeChart: (HomeChart) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(start = 20.dp, end = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.titleLarge,
            )

            // Toggle emoji
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Mood,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = stringResource(R.string.settings_show_emoji),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f),
                )
                Switch(
                    checked = settings.showEmoji,
                    onCheckedChange = onSetShowEmoji,
                )
            }

            Text(
                text = stringResource(R.string.settings_home_chart),
                style = MaterialTheme.typography.titleMedium,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                ChartOption(
                    icon = Icons.Rounded.GridView,
                    label = stringResource(R.string.chart_waffle),
                    selected = settings.homeChart == HomeChart.WAFFLE,
                    onClick = { onSetHomeChart(HomeChart.WAFFLE) },
                    modifier = Modifier.weight(1f),
                )
                ChartOption(
                    icon = Icons.Rounded.DonutLarge,
                    label = stringResource(R.string.chart_donut),
                    selected = settings.homeChart == HomeChart.DONUT,
                    onClick = { onSetHomeChart(HomeChart.DONUT) },
                    modifier = Modifier.weight(1f),
                )
                ChartOption(
                    icon = Icons.Rounded.TrackChanges,
                    label = stringResource(R.string.chart_rings),
                    selected = settings.homeChart == HomeChart.RINGS,
                    onClick = { onSetHomeChart(HomeChart.RINGS) },
                    modifier = Modifier.weight(1f),
                )
            }

            Text(
                text = stringResource(R.string.settings_palette),
                style = MaterialTheme.typography.titleMedium,
            )

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                AppPalettes.forEachIndexed { index, palette ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .selectable(
                                selected = index == settings.paletteId,
                                onClick = { onSetPalette(index) },
                            )
                            .padding(vertical = 4.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        RadioButton(
                            selected = index == settings.paletteId,
                            onClick = { onSetPalette(index) },
                        )
                        Text(
                            text = palette.name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f),
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            ColorDot(palette.direct)
                            ColorDot(palette.question)
                            ColorDot(palette.unknown)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorDot(color: androidx.compose.ui.graphics.Color) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(color)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
    )
}

@Composable
private fun ChartOption(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(14.dp)
    val border = if (selected) {
        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    } else {
        BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    }
    Column(
        modifier = modifier
            .clip(shape)
            .background(
                if (selected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surface,
            )
            .border(border, shape)
            .selectable(selected = selected, onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
    }
}
