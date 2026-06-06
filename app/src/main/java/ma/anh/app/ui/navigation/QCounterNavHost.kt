package ma.anh.app.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ma.anh.app.data.model.AnswerType
import ma.anh.app.data.model.AppSettings
import ma.anh.app.data.model.EmojiIntensity
import ma.anh.app.data.model.HomeChart
import ma.anh.app.ui.ViewModelFactory
import ma.anh.app.ui.charts.ChartsScreen
import ma.anh.app.ui.charts.ChartsViewModel
import ma.anh.app.ui.history.HistoryScreen
import ma.anh.app.ui.history.HistoryViewModel
import ma.anh.app.ui.home.HomeScreen
import ma.anh.app.ui.home.HomeViewModel

private object Routes {
    const val HOME = "home"
    const val HISTORY = "history"
    const val CHARTS = "charts"
}

@Composable
fun QCounterNavHost(
    factory: ViewModelFactory,
    settings: AppSettings,
    onSetDiscreet: (Boolean) -> Unit,
    onSetShowEmoji: (Boolean) -> Unit,
    onSetPalette: (Int) -> Unit,
    onSetHomeChart: (HomeChart) -> Unit,
    onSetDynamicColor: (Boolean) -> Unit,
    onSetEmojiSet: (Int) -> Unit,
    onSetEmojiIntensity: (EmojiIntensity) -> Unit,
    onSetLongLabel: (AnswerType, String) -> Unit,
    onSetShortLabel: (AnswerType, String) -> Unit,
    onSetCustomEnabled: (Boolean) -> Unit,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(300)) + fadeIn(tween(300))
        },
        exitTransition = { fadeOut(tween(200)) },
        popEnterTransition = { fadeIn(tween(300)) },
        popExitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(300)) + fadeOut(tween(300))
        },
    ) {
        composable(Routes.HOME) {
            val vm: HomeViewModel = viewModel(factory = factory)
            HomeScreen(
                viewModel = vm,
                onOpenHistory = { navController.navigate(Routes.HISTORY) },
                onOpenCharts = { navController.navigate(Routes.CHARTS) },
                settings = settings,
                onSetDiscreet = onSetDiscreet,
                onSetShowEmoji = onSetShowEmoji,
                onSetPalette = onSetPalette,
                onSetHomeChart = onSetHomeChart,
                onSetDynamicColor = onSetDynamicColor,
                onSetEmojiSet = onSetEmojiSet,
                onSetEmojiIntensity = onSetEmojiIntensity,
                onSetLongLabel = onSetLongLabel,
                onSetShortLabel = onSetShortLabel,
                onSetCustomEnabled = onSetCustomEnabled,
            )
        }
        composable(Routes.HISTORY) {
            val vm: HistoryViewModel = viewModel(factory = factory)
            HistoryScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() },
            )
        }
        composable(Routes.CHARTS) {
            val vm: ChartsViewModel = viewModel(factory = factory)
            ChartsScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
