package ma.wave.qcounter.ui.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import kotlinx.coroutines.flow.first
import ma.wave.qcounter.QCounterApp
import ma.wave.qcounter.data.model.AnswerType
import ma.wave.qcounter.data.model.InteractionStats

/** Clé d'argument transportant le type de réponse à enregistrer depuis le widget. */
val RecordTypeKey = ActionParameters.Key<String>("qc_record_type")

// Couleurs de marque (palette « Lagon ») pour les boutons d'action du widget.
// Le fond et le texte, eux, suivent Material You via GlanceTheme.
private val DirectColor = Color(0xFF2193B0)   // bleu de marque
private val QuestionColor = Color(0xFFE53935) // rouge
private val UnknownColor = Color(0xFF46627E)  // ardoise

/** Widget d'écran d'accueil : total + 3 boutons pour compter sans ouvrir l'app. */
class QCounterWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = (context.applicationContext as QCounterApp).repository
        val stats = repository.stats.first()
        provideContent {
            GlanceTheme { WidgetContent(stats) }
        }
    }
}

@Composable
private fun WidgetContent(stats: InteractionStats) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.widgetBackground)
            .cornerRadius(16.dp)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "QCounter",
            style = TextStyle(
                color = GlanceTheme.colors.primary,
                fontWeight = FontWeight.Bold,
            ),
        )
        Text(
            text = "${stats.totalInteractions}",
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
            ),
        )
        Spacer(GlanceModifier.height(8.dp))
        Row(modifier = GlanceModifier.fillMaxWidth()) {
            WidgetButton("D", stats.directAnswers, AnswerType.DIRECT, DirectColor, GlanceModifier.defaultWeight())
            Spacer(GlanceModifier.width(6.dp))
            WidgetButton("Q", stats.questionAnswers, AnswerType.QUESTION, QuestionColor, GlanceModifier.defaultWeight())
            Spacer(GlanceModifier.width(6.dp))
            WidgetButton("?", stats.unknownAnswers, AnswerType.UNKNOWN, UnknownColor, GlanceModifier.defaultWeight())
        }
    }
}

@Composable
private fun WidgetButton(
    short: String,
    count: Int,
    type: AnswerType,
    color: Color,
    modifier: GlanceModifier,
) {
    Column(
        modifier = modifier
            .background(color)
            .cornerRadius(12.dp)
            .clickable(
                actionRunCallback<RecordActionCallback>(
                    actionParametersOf(RecordTypeKey to type.name),
                ),
            )
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "$count",
            style = TextStyle(color = ColorProvider(Color.White), fontWeight = FontWeight.Bold),
        )
        Text(
            text = short,
            style = TextStyle(color = ColorProvider(Color.White), textAlign = TextAlign.Center),
        )
    }
}

/** Variante compacte sur une seule ligne : total à gauche, 3 boutons à droite. */
class QCounterRowWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = (context.applicationContext as QCounterApp).repository
        val stats = repository.stats.first()
        provideContent {
            GlanceTheme { WidgetRowContent(stats) }
        }
    }
}

@Composable
private fun WidgetRowContent(stats: InteractionStats) {
    Row(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.widgetBackground)
            .cornerRadius(16.dp)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "${stats.totalInteractions}",
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
            ),
        )
        Spacer(GlanceModifier.width(10.dp))
        WidgetButton("D", stats.directAnswers, AnswerType.DIRECT, DirectColor, GlanceModifier.defaultWeight())
        Spacer(GlanceModifier.width(6.dp))
        WidgetButton("Q", stats.questionAnswers, AnswerType.QUESTION, QuestionColor, GlanceModifier.defaultWeight())
        Spacer(GlanceModifier.width(6.dp))
        WidgetButton("?", stats.unknownAnswers, AnswerType.UNKNOWN, UnknownColor, GlanceModifier.defaultWeight())
    }
}

/** Variante 1×1 : un seul bouton « Question » (cas d'usage principal) avec son compteur. */
class QCounterCompactWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = (context.applicationContext as QCounterApp).repository
        val stats = repository.stats.first()
        provideContent {
            GlanceTheme { WidgetCompactContent(stats) }
        }
    }
}

@Composable
private fun WidgetCompactContent(stats: InteractionStats) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.primaryContainer)
            .cornerRadius(16.dp)
            .clickable(
                actionRunCallback<RecordActionCallback>(
                    actionParametersOf(RecordTypeKey to AnswerType.QUESTION.name),
                ),
            )
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "${stats.questionAnswers}",
            style = TextStyle(
                color = GlanceTheme.colors.onPrimaryContainer,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
            ),
        )
        Text(
            text = "Q",
            style = TextStyle(color = GlanceTheme.colors.onPrimaryContainer),
        )
    }
}

/** Variante 2×1 : total + boutons Directe et Question uniquement, pour une saisie compacte. */
class QCounterButtonsWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = (context.applicationContext as QCounterApp).repository
        val stats = repository.stats.first()
        provideContent {
            GlanceTheme { WidgetButtonsContent(stats) }
        }
    }
}

@Composable
private fun WidgetButtonsContent(stats: InteractionStats) {
    Row(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.widgetBackground)
            .cornerRadius(16.dp)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "${stats.totalInteractions}",
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
            ),
        )
        Spacer(GlanceModifier.width(8.dp))
        WidgetButton("D", stats.directAnswers, AnswerType.DIRECT, DirectColor, GlanceModifier.defaultWeight())
        Spacer(GlanceModifier.width(6.dp))
        WidgetButton("Q", stats.questionAnswers, AnswerType.QUESTION, QuestionColor, GlanceModifier.defaultWeight())
    }
}

/** Rafraîchit toutes les variantes de widget après un changement de données. */
suspend fun refreshAllWidgets(context: Context) {
    QCounterWidget().updateAll(context)
    QCounterRowWidget().updateAll(context)
    QCounterButtonsWidget().updateAll(context)
    QCounterCompactWidget().updateAll(context)
}

/** Enregistre l'interaction choisie depuis le widget, puis rafraîchit tous les widgets. */
class RecordActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        val typeName = parameters[RecordTypeKey] ?: return
        val type = runCatching { AnswerType.valueOf(typeName) }.getOrNull() ?: return
        val repository = (context.applicationContext as QCounterApp).repository
        repository.record(type)
        refreshAllWidgets(context)
    }
}
