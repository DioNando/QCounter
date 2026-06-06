package ma.wave.qcounter.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ma.wave.qcounter.data.model.AppSettings
import ma.wave.qcounter.data.model.EmojiIntensity
import ma.wave.qcounter.data.model.HomeChart
import ma.wave.qcounter.ui.ViewModelFactory
import ma.wave.qcounter.ui.history.HistoryScreen
import ma.wave.qcounter.ui.history.HistoryViewModel
import ma.wave.qcounter.ui.home.HomeScreen
import ma.wave.qcounter.ui.home.HomeViewModel

private object Routes {
    const val HOME = "home"
    const val HISTORY = "history"
}

@Composable
fun QCounterNavHost(
    factory: ViewModelFactory,
    settings: AppSettings,
    onSetShowEmoji: (Boolean) -> Unit,
    onSetPalette: (Int) -> Unit,
    onSetHomeChart: (HomeChart) -> Unit,
    onSetDynamicColor: (Boolean) -> Unit,
    onSetEmojiSet: (Int) -> Unit,
    onSetEmojiIntensity: (EmojiIntensity) -> Unit,
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
                settings = settings,
                onSetShowEmoji = onSetShowEmoji,
                onSetPalette = onSetPalette,
                onSetHomeChart = onSetHomeChart,
                onSetDynamicColor = onSetDynamicColor,
                onSetEmojiSet = onSetEmojiSet,
                onSetEmojiIntensity = onSetEmojiIntensity,
            )
        }
        composable(Routes.HISTORY) {
            val vm: HistoryViewModel = viewModel(factory = factory)
            HistoryScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
