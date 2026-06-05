package ma.wave.qcounter.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
fun QCounterNavHost(factory: ViewModelFactory) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            val vm: HomeViewModel = viewModel(factory = factory)
            HomeScreen(
                viewModel = vm,
                onOpenHistory = { navController.navigate(Routes.HISTORY) },
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
