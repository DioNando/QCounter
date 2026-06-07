package ma.anh.app.ui.history

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Inbox
import androidx.compose.material.icons.rounded.SelectAll
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ma.anh.app.R
import ma.anh.app.data.local.InteractionEntity
import ma.anh.app.data.model.AnswerType
import ma.anh.app.ui.components.ResetConfirmationDialog
import ma.anh.app.ui.components.answerTypeShortLabel
import ma.anh.app.ui.components.answerTypeVisual
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onBack: () -> Unit,
) {
    val history by viewModel.history.collectAsStateWithLifecycle()
    val selectedIds by viewModel.selectedIds.collectAsStateWithLifecycle()
    val grouping by viewModel.grouping.collectAsStateWithLifecycle()
    val hiddenTypes by viewModel.hiddenTypes.collectAsStateWithLifecycle()
    val pageSize by viewModel.pageSize.collectAsStateWithLifecycle()
    val inSelection = selectedIds.isNotEmpty()

    var showResetDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showExportMenu by remember { mutableStateOf(false) }
    // Choix retenu au moment d'ouvrir le sélecteur de fichier, appliqué au retour.
    var exportOnlyVisible by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Import / export via le Storage Access Framework (sélecteur de fichiers système).
    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json"),
    ) { uri ->
        if (uri != null) viewModel.exportTo(context.contentResolver, uri, exportOnlyVisible) { ok ->
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

    val undoLabel = stringResource(R.string.action_undo)
    val deletedTemplate = stringResource(R.string.snackbar_deleted)

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            snackbarHostState.currentSnackbarData?.dismiss()
            val result = snackbarHostState.showSnackbar(
                message = deletedTemplate.format(event.items.size),
                actionLabel = undoLabel,
                duration = SnackbarDuration.Short,
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.undoDelete(event.items)
            }
        }
    }

    // En mode sélection, le bouton retour système quitte d'abord la sélection.
    BackHandler(enabled = inSelection) { viewModel.clearSelection() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (inSelection) {
                TopAppBar(
                    title = {
                        Text(stringResource(R.string.selection_count, selectedIds.size))
                    },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.clearSelection() }) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = stringResource(R.string.cd_close_selection),
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.selectAll() }) {
                            Icon(
                                imageVector = Icons.Rounded.SelectAll,
                                contentDescription = stringResource(R.string.cd_select_all),
                            )
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Rounded.Delete,
                                contentDescription = stringResource(R.string.delete_selected),
                            )
                        }
                    },
                )
            } else {
                TopAppBar(
                    title = { Text(stringResource(R.string.history_title)) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = stringResource(R.string.cancel),
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { importLauncher.launch(
                            arrayOf("application/json", "text/plain", "application/octet-stream"),
                        ) }) {
                            Icon(
                                imageVector = Icons.Rounded.Download,
                                contentDescription = stringResource(R.string.settings_import),
                            )
                        }
                        Box {
                            IconButton(
                                onClick = { showExportMenu = true },
                                enabled = history.isNotEmpty(),
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Upload,
                                    contentDescription = stringResource(R.string.settings_export),
                                )
                            }
                            DropdownMenu(
                                expanded = showExportMenu,
                                onDismissRequest = { showExportMenu = false },
                            ) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.export_all)) },
                                    onClick = {
                                        showExportMenu = false
                                        exportOnlyVisible = false
                                        exportLauncher.launch("anh-export.json")
                                    },
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.export_filtered)) },
                                    enabled = hiddenTypes.isNotEmpty(),
                                    onClick = {
                                        showExportMenu = false
                                        exportOnlyVisible = true
                                        exportLauncher.launch("anh-export-filtre.json")
                                    },
                                )
                            }
                        }
                        IconButton(
                            onClick = { showResetDialog = true },
                            enabled = history.isNotEmpty(),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.DeleteSweep,
                                contentDescription = stringResource(R.string.action_reset),
                            )
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        if (history.isEmpty()) {
            EmptyState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
        } else {
            val groupLabels = HistoryGroupLabels(
                today = stringResource(R.string.history_group_today),
                yesterday = stringResource(R.string.history_group_yesterday),
                thisWeek = stringResource(R.string.history_group_this_week),
                lastWeek = stringResource(R.string.history_group_last_week),
                thisMonth = stringResource(R.string.history_group_this_month),
                weekOfTemplate = stringResource(R.string.history_group_week_of),
            )
            val presentTypes = remember(history) {
                history.map { it.type }.distinct().sortedBy { it.ordinal }
            }
            val filtered = remember(history, hiddenTypes) {
                history.filterNot { it.type in hiddenTypes }
            }
            val shown = filtered.take(pageSize)
            val sections = remember(shown, grouping) { groupHistory(shown, grouping) }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                HistoryControls(
                    grouping = grouping,
                    onSelectGrouping = viewModel::setGrouping,
                    presentTypes = presentTypes,
                    hiddenTypes = hiddenTypes,
                    onToggleType = viewModel::toggleType,
                    pageSize = pageSize,
                    onSelectPageSize = viewModel::setPageSize,
                )
                if (filtered.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(R.string.history_filter_empty),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(24.dp),
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        sections.forEach { section ->
                            item(key = "h-${section.startMillis}") {
                                SectionHeader(
                                    label = historySectionLabel(section.startMillis, grouping, groupLabels),
                                )
                            }
                            items(section.items, key = { it.id }) { interaction ->
                                InteractionRow(
                                    interaction = interaction,
                                    selected = selectedIds.contains(interaction.id),
                                    inSelection = inSelection,
                                    onClick = {
                                        if (inSelection) viewModel.toggleSelection(interaction.id)
                                    },
                                    onLongClick = { viewModel.toggleSelection(interaction.id) },
                                )
                            }
                        }
                        if (filtered.size > pageSize) {
                            item(key = "more_hidden") {
                                Text(
                                    text = stringResource(R.string.history_more_hidden, filtered.size - pageSize),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp),
                                )
                            }
                        }
                    }
                }
            }
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

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_dialog_title)) },
            text = { Text(stringResource(R.string.delete_dialog_message, selectedIds.size)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteSelected()
                    showDeleteDialog = false
                }) { Text(stringResource(R.string.confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun InteractionRow(
    interaction: InteractionEntity,
    selected: Boolean,
    inSelection: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    val visual = answerTypeVisual(interaction.type)
    val container =
        if (selected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceContainerHigh

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = container),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .combinedClickable(onClick = onClick, onLongClick = onLongClick)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(visual.accent.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = visual.icon,
                    contentDescription = null,
                    tint = visual.accent,
                    modifier = Modifier.size(22.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = visual.label,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = formatTimestamp(interaction.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (inSelection) {
                Checkbox(checked = selected, onCheckedChange = { onClick() })
            }
        }
    }
}

/** Barre de contrôles : choix du regroupement + filtres de catégories. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryControls(
    grouping: HistoryGrouping,
    onSelectGrouping: (HistoryGrouping) -> Unit,
    presentTypes: List<AnswerType>,
    hiddenTypes: Set<AnswerType>,
    onToggleType: (AnswerType) -> Unit,
    pageSize: Int,
    onSelectPageSize: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            GroupingChip(stringResource(R.string.history_group_day), grouping == HistoryGrouping.DAY) {
                onSelectGrouping(HistoryGrouping.DAY)
            }
            GroupingChip(stringResource(R.string.history_group_week), grouping == HistoryGrouping.WEEK) {
                onSelectGrouping(HistoryGrouping.WEEK)
            }
            GroupingChip(stringResource(R.string.history_group_month), grouping == HistoryGrouping.MONTH) {
                onSelectGrouping(HistoryGrouping.MONTH)
            }
        }
        // Nombre d'éléments affichés.
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.history_page_size),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            listOf(50, 100, 250).forEach { n ->
                GroupingChip(n.toString(), pageSize == n) { onSelectPageSize(n) }
            }
        }
        // Filtres de catégories : une puce par type présent (toucher pour afficher/masquer).
        if (presentTypes.size > 1) {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                presentTypes.forEach { type ->
                    val visual = answerTypeVisual(type)
                    val visible = type !in hiddenTypes
                    FilterChip(
                        selected = visible,
                        onClick = { onToggleType(type) },
                        label = { Text(answerTypeShortLabel(type)) },
                        leadingIcon = {
                            Icon(
                                imageVector = visual.icon,
                                contentDescription = null,
                                tint = if (visible) visual.accent else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp),
                            )
                        },
                        colors = tonalChipColors(),
                        border = null,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GroupingChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        colors = tonalChipColors(),
        border = null,
    )
}

/**
 * Couleurs de puce **tonales** (sans bordure) cohérentes avec l'UI : surface tonale au repos,
 * jaune de marque **plus saturé** (`secondaryContainer`) sélectionné — réservé aux boutons,
 * tandis que les cartes d'historique utilisent le jaune **moins saturé** (`primaryContainer`).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun tonalChipColors() = FilterChipDefaults.filterChipColors(
    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
    selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
    selectedLeadingIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
)

/** En-tête de section (Aujourd'hui / Cette semaine / Juin 2026…). */
@Composable
private fun SectionHeader(label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 2.dp),
    )
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Rounded.Inbox,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(72.dp),
        )
        Text(
            text = stringResource(R.string.history_empty),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}

private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

private fun formatTimestamp(millis: Long): String = dateFormat.format(Date(millis))
