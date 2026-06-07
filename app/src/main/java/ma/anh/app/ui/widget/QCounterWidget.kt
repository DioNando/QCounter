package ma.anh.app.ui.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
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
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import kotlinx.coroutines.flow.first
import ma.anh.app.QCounterApp
import ma.anh.app.R
import ma.anh.app.data.model.AnswerType
import ma.anh.app.ui.components.compactCount
import ma.anh.app.data.model.InteractionStats

/** Clé d'argument transportant le type de réponse à enregistrer depuis le widget. */
val RecordTypeKey = ActionParameters.Key<String>("qc_record_type")

/** État par widget 1×1 : type d'action choisi à la configuration. */
val CompactActionKey = stringPreferencesKey("compact_action")

/** État par widget 2×1 : les deux types affichés, choisis à la configuration. */
val ButtonsFirstKey = stringPreferencesKey("buttons_action_1")
val ButtonsSecondKey = stringPreferencesKey("buttons_action_2")

// Boutons : palette de marque « Soleil » (le fond, lui, suit Material You via GlanceTheme).
private val DirectColor = Color(0xFFF2B705)   // doré
private val QuestionColor = Color(0xFFD5442D) // rouge
private val UnknownColor = Color(0xFFBFA94E)  // or grisé
private val CustomColor = Color(0xFF7E57C2)   // violet (4ᵉ catégorie)

private val SmallWidget = DpSize(150.dp, 100.dp)
private val LargeWidget = DpSize(220.dp, 150.dp)

/** Accent (couleur de marque) d'un type, utilisé en premier plan sur une tuile tonale. */
private fun colorFor(type: AnswerType): Color = when (type) {
    AnswerType.DIRECT -> DirectColor
    AnswerType.QUESTION -> QuestionColor
    AnswerType.UNKNOWN -> UnknownColor
    AnswerType.CUSTOM -> CustomColor
    // Oui/Non sont des réponses directes → même couleur (jamais affichés seuls dans le widget).
    AnswerType.OUI, AnswerType.NON -> DirectColor
}

/** Icône (drawable) d'un type, alignée sur les icônes de l'app. */
private fun iconResFor(type: AnswerType): Int = when (type) {
    AnswerType.DIRECT, AnswerType.OUI, AnswerType.NON -> R.drawable.ic_widget_direct
    AnswerType.QUESTION -> R.drawable.ic_widget_question
    AnswerType.UNKNOWN -> R.drawable.ic_widget_unknown
    AnswerType.CUSTOM -> R.drawable.ic_widget_unknown
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
            text = compactCount(stats.totalInteractions),
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
            ),
        )
        Spacer(GlanceModifier.height(8.dp))
        // La rangée de boutons occupe la hauteur restante (pas d'espace vide).
        Row(modifier = GlanceModifier.fillMaxWidth().defaultWeight()) {
            WidgetButton(AnswerType.DIRECT, stats.directAnswers, DirectColor, GlanceModifier.defaultWeight())
            Spacer(GlanceModifier.width(6.dp))
            WidgetButton(AnswerType.QUESTION, stats.questionAnswers, QuestionColor, GlanceModifier.defaultWeight())
            Spacer(GlanceModifier.width(6.dp))
            WidgetButton(AnswerType.UNKNOWN, stats.unknownAnswers, UnknownColor, GlanceModifier.defaultWeight())
        }
    }
}

/** Bouton : tuile **tonale** (comme les cartes de l'app) + icône et compteur à la couleur d'accent. */
@Composable
private fun WidgetButton(
    type: AnswerType,
    count: Int,
    accent: Color,
    modifier: GlanceModifier,
) {
    val content = ColorProvider(accent)
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(GlanceTheme.colors.surfaceVariant)
            .cornerRadius(16.dp)
            .clickable(
                actionRunCallback<RecordActionCallback>(
                    actionParametersOf(RecordTypeKey to type.name),
                ),
            )
            .padding(vertical = 6.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            provider = ImageProvider(iconResFor(type)),
            contentDescription = null,
            colorFilter = ColorFilter.tint(content),
            modifier = GlanceModifier.size(22.dp),
        )
        Spacer(GlanceModifier.height(2.dp))
        Text(
            text = compactCount(count),
            style = TextStyle(color = content, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
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
            text = compactCount(stats.totalInteractions),
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
            ),
        )
        Spacer(GlanceModifier.width(10.dp))
        WidgetButton(AnswerType.DIRECT, stats.directAnswers, DirectColor, GlanceModifier.defaultWeight())
        Spacer(GlanceModifier.width(6.dp))
        WidgetButton(AnswerType.QUESTION, stats.questionAnswers, QuestionColor, GlanceModifier.defaultWeight())
        Spacer(GlanceModifier.width(6.dp))
        WidgetButton(AnswerType.UNKNOWN, stats.unknownAnswers, UnknownColor, GlanceModifier.defaultWeight())
    }
}

/** Variante 1×1 configurable : le type enregistré est choisi à la configuration (défaut : Question). */
class QCounterCompactWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = (context.applicationContext as QCounterApp).repository
        val stats = repository.stats.first()
        provideContent {
            GlanceTheme {
                val prefs = currentState<Preferences>()
                val action = prefs[CompactActionKey]
                    ?.let { runCatching { AnswerType.valueOf(it) }.getOrNull() }
                    ?: AnswerType.QUESTION
                WidgetCompactContent(stats, action)
            }
        }
    }
}

@Composable
private fun WidgetCompactContent(stats: InteractionStats, action: AnswerType) {
    val content = ColorProvider(colorFor(action))
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.surfaceVariant)
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
        Image(
            provider = ImageProvider(iconResFor(action)),
            contentDescription = null,
            colorFilter = ColorFilter.tint(content),
            modifier = GlanceModifier.size(24.dp),
        )
        Text(
            text = compactCount(countFor(stats, action)),
            style = TextStyle(color = content, fontWeight = FontWeight.Bold, fontSize = 22.sp),
        )
    }
}

/** Variante 2×1 : total + 2 boutons, les types étant choisis à la configuration. */
class QCounterButtonsWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = (context.applicationContext as QCounterApp).repository
        val stats = repository.stats.first()
        provideContent {
            GlanceTheme {
                val prefs = currentState<Preferences>()
                fun read(key: androidx.datastore.preferences.core.Preferences.Key<String>, default: AnswerType) =
                    prefs[key]?.let { runCatching { AnswerType.valueOf(it) }.getOrNull() } ?: default
                WidgetButtonsContent(
                    stats = stats,
                    first = read(ButtonsFirstKey, AnswerType.DIRECT),
                    second = read(ButtonsSecondKey, AnswerType.QUESTION),
                )
            }
        }
    }
}

@Composable
private fun WidgetButtonsContent(stats: InteractionStats, first: AnswerType, second: AnswerType) {
    Row(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.widgetBackground)
            .cornerRadius(16.dp)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = compactCount(stats.totalInteractions),
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
            ),
        )
        Spacer(GlanceModifier.width(8.dp))
        WidgetButton(first, countFor(stats, first), colorFor(first), GlanceModifier.defaultWeight())
        Spacer(GlanceModifier.width(6.dp))
        WidgetButton(second, countFor(stats, second), colorFor(second), GlanceModifier.defaultWeight())
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
