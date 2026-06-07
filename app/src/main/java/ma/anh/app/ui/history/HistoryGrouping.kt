package ma.anh.app.ui.history

import ma.anh.app.data.local.InteractionEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/** Granularité de regroupement de l'historique. */
enum class HistoryGrouping { DAY, WEEK, MONTH }

/** Une section de l'historique : début de période (clé stable) + interactions de la période. */
data class HistorySection(
    val startMillis: Long,
    val items: List<InteractionEntity>,
)

/** Libellés localisés des sections « relatives » (résolus côté UI, injectés ici). */
data class HistoryGroupLabels(
    val today: String,
    val yesterday: String,
    val thisWeek: String,
    val lastWeek: String,
    val thisMonth: String,
    val weekOfTemplate: String,
)

/**
 * Regroupe [items] (triés du plus récent au plus ancien) par [grouping].
 * L'ordre des sections — et des éléments au sein de chaque section — reste décroissant.
 */
fun groupHistory(items: List<InteractionEntity>, grouping: HistoryGrouping): List<HistorySection> {
    if (items.isEmpty()) return emptyList()
    val cal = Calendar.getInstance()
    return items
        .groupBy { periodStartMillis(it.timestamp, grouping, cal) }
        .map { (start, list) -> HistorySection(start, list) }
}

/** Début de la période (00:00) contenant [timestamp], pour la granularité demandée. */
private fun periodStartMillis(timestamp: Long, grouping: HistoryGrouping, cal: Calendar): Long {
    cal.timeInMillis = timestamp
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    when (grouping) {
        HistoryGrouping.DAY -> Unit
        HistoryGrouping.WEEK -> {
            // Recule jusqu'au lundi de la semaine (robuste vis-à-vis des réglages de Calendar).
            val daysFromMonday = (cal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY + 7) % 7
            cal.add(Calendar.DAY_OF_MONTH, -daysFromMonday)
        }
        HistoryGrouping.MONTH -> cal.set(Calendar.DAY_OF_MONTH, 1)
    }
    return cal.timeInMillis
}

/** Libellé d'une section : relatif (« Aujourd'hui », « Cette semaine »…) ou daté. */
fun historySectionLabel(
    startMillis: Long,
    grouping: HistoryGrouping,
    labels: HistoryGroupLabels,
    now: Long = System.currentTimeMillis(),
): String {
    val cal = Calendar.getInstance()
    val locale = Locale.getDefault()
    return when (grouping) {
        HistoryGrouping.DAY -> {
            val todayStart = periodStartMillis(now, HistoryGrouping.DAY, cal)
            cal.timeInMillis = todayStart
            cal.add(Calendar.DAY_OF_MONTH, -1)
            val yesterdayStart = cal.timeInMillis
            when (startMillis) {
                todayStart -> labels.today
                yesterdayStart -> labels.yesterday
                else -> SimpleDateFormat("EEEE d MMMM", locale).format(Date(startMillis)).capitalizeFirst()
            }
        }
        HistoryGrouping.WEEK -> {
            val thisWeekStart = periodStartMillis(now, HistoryGrouping.WEEK, cal)
            cal.timeInMillis = thisWeekStart
            cal.add(Calendar.DAY_OF_MONTH, -7)
            val lastWeekStart = cal.timeInMillis
            when (startMillis) {
                thisWeekStart -> labels.thisWeek
                lastWeekStart -> labels.lastWeek
                else -> labels.weekOfTemplate.format(SimpleDateFormat("d MMM", locale).format(Date(startMillis)))
            }
        }
        HistoryGrouping.MONTH -> {
            val thisMonthStart = periodStartMillis(now, HistoryGrouping.MONTH, cal)
            if (startMillis == thisMonthStart) {
                labels.thisMonth
            } else {
                SimpleDateFormat("MMMM yyyy", locale).format(Date(startMillis)).capitalizeFirst()
            }
        }
    }
}

private fun String.capitalizeFirst(): String =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
