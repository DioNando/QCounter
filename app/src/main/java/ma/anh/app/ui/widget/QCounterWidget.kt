package ma.anh.app.ui.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
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
import ma.anh.app.QCounterApp
import ma.anh.app.data.model.AnswerType
import ma.anh.app.data.model.InteractionStats

/** Clé d'argument transportant le type de réponse à enregistrer depuis le widget. */
val RecordTypeKey = ActionParameters.Key<String>("qc_record_type")

/** État par widget 1×1 : type d'action choisi à la configuration. */
val CompactActionKey = stringPreferencesKey("compact_action")

// Boutons : palette de marque « Soleil » (le fond, lui, suit Material You via GlanceTheme).
private val DirectColor = Color(0xFFF2B705)   // doré
private val QuestionColor = Color(0xFFD5442D) // rouge
private val UnknownColor = Color(0xFFBFA94E)  // or grisé
private val InkOnLight = Color(0xFF2A2410)    // texte foncé pour les tuiles claires

private val SmallWidget = DpSize(150.dp, 100.dp)
private val LargeWidget = DpSize(220.dp, 150.dp)

/** Couleur de texte lisible selon la luminosité de la tuile. */
private fun onTile(tile: Color): Color =
    if (tile.luminance() > 0.4f) InkOnLight else Color.White

private val CustomColor = Color(0xFF7E57C2) // violet (4ᵉ catégorie)

private fun colorFor(type: AnswerType): Color = when (type) {
    AnswerType.DIRECT -> DirectColor
    AnswerType.QUESTION -> QuestionColor
    AnswerType.UNKNOWN -> UnknownColor
    AnswerType.CUSTOM -> CustomColor
    // Oui/Non sont des réponses directes → même couleur (jamais affichés seuls dans le widget).
    AnswerType.OUI, AnswerType.NON -> DirectColor
}

private fun shortFor(type: AnswerType): String = when (type) {
    AnswerType.DIRECT -> "D"
    AnswerType.QUESTION -> "Q"
    AnswerType.UNKNOWN -> "?"
    AnswerType.CUSTOM -> "+"
    AnswerType.OUI -> "O"
    AnswerType.NON -> "N"
}

private fun countFor(stats: InteractionStats, type: AnswerType): Int = when (type) {
    AnswerType.DIRECT -> stats.directAnswers
    AnswerType.QUESTION -> stats.questionAnswers
    AnswerType.UNKNOWN -> stats.unknownAnswers
    AnswerType.CUSTOM -> stats.customAnswers
    AnswerType.OUI -> stats.yesAnswers
    AnswerType.NON -> stats.noAnswers
}

/** Widget réactif : total + 3 boutons, le titre n'apparaît que si le widget est assez haut. */
class QCounterWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Responsive(setOf(SmallWidget, LargeWidget))

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = (context.applicationContext as QCounterApp).repository
        val stats = repository.stats.first()
        provideContent {
            GlanceTheme {
                WidgetContent(stats, showTitle = LocalSize.current.height >= 130.dp)
            }
        }
    }
}

@Composable
private fun WidgetContent(stats: InteractionStats, showTitle: Boolean) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.widgetBackground)
            .cornerRadius(16.dp)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (showTitle) {
            Text(
                text = "Anh",
                style = TextStyle(color = GlanceTheme.colors.primary, fontWeight = FontWeight.Bold),
            )
        }
        Text(
            text = "${stats.totalInteractions}",
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
            ),
        )
        Spacer(GlanceModifier.height(8.dp))
        // La rangée de boutons occupe la hauteur restante (pas d'espace vide).
        Row(modifier = GlanceModifier.fillMaxWidth().defaultWeight()) {
            WidgetButton("D", stats.directAnswers, AnswerType.DIRECT, DirectColor, GlanceModifier.defaultWeight())
            Spacer(GlanceModifier.width(6.dp))
            WidgetButton("Q", stats.questionAnswers, AnswerType.QUESTION, QuestionColor, GlanceModifier.defaultWeight())
            Spacer(GlanceModifier.width(6.dp))
            WidgetButton("?", stats.unknownAnswers, AnswerType.UNKNOWN, UnknownColor, GlanceModifier.defaultWeight())
        }
    }
}

/** Bouton : tuile pleine (palette de marque) remplissant la hauteur, texte à contraste automatique. */
@Composable
private fun WidgetButton(
    short: String,
    count: Int,
    type: AnswerType,
    tile: Color,
    modifier: GlanceModifier,
) {
    val content = ColorProvider(onTile(tile))
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(tile)
            .cornerRadius(12.dp)
            .clickable(
                actionRunCallback<RecordActionCallback>(
                    actionParametersOf(RecordTypeKey to type.name),
                ),
            )
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "$count",
            style = TextStyle(color = content, fontWeight = FontWeight.Bold),
        )
        Text(
            text = short,
            style = TextStyle(color = content, textAlign = TextAlign.Center),
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

/** Variante 1×1 configurable : le type enregistré est choisi à la configuration (défaut : Question). */
class QCounterCompactWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = (context.applicationContext as QCounterApp).repository
        val stats = repository.stats.first()
        provideContent {
            val prefs = currentState<Preferences>()
            val action = prefs[CompactActionKey]
                ?.let { runCatching { AnswerType.valueOf(it) }.getOrNull() }
                ?: AnswerType.QUESTION
            WidgetCompactContent(stats, action)
        }
    }
}

@Composable
private fun WidgetCompactContent(stats: InteractionStats, action: AnswerType) {
    val tile = colorFor(action)
    val content = ColorProvider(onTile(tile))
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(tile)
            .cornerRadius(16.dp)
            .clickable(
                actionRunCallback<RecordActionCallback>(
                    actionParametersOf(RecordTypeKey to action.name),
                ),
            )
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "${countFor(stats, action)}",
            style = TextStyle(color = content, fontWeight = FontWeight.Bold, fontSize = 26.sp),
        )
        Text(
            text = shortFor(action),
            style = TextStyle(color = content),
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
