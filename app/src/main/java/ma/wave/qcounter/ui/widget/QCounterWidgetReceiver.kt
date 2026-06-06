package ma.wave.qcounter.ui.widget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

/** Receiver système reliant le widget Glance au framework App Widget. */
class QCounterWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = QCounterWidget()
}

/** Receiver de la variante compacte sur une seule ligne. */
class QCounterRowWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = QCounterRowWidget()
}

/** Receiver de la variante 2×1 (3 boutons seuls). */
class QCounterButtonsWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = QCounterButtonsWidget()
}

/** Receiver de la variante 1×1. */
class QCounterCompactWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = QCounterCompactWidget()
}
