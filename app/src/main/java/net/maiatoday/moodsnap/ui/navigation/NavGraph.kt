package net.maiatoday.moodsnap.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import net.maiatoday.moodsnap.ui.addedit.AddEditScreen
import net.maiatoday.moodsnap.ui.home.HomeScreen
import net.maiatoday.moodsnap.ui.history.MoodHistoryScreen
import net.maiatoday.moodsnap.ui.settings.SettingsScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                onAddEntry = { navController.navigate(Screen.AddEdit.createRoute(null)) },
                onHistoryClick = { navController.navigate(Screen.History.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) }
            )
        }
        composable(Screen.History.route) {
            MoodHistoryScreen(
                onEntryClick = { entryId -> navController.navigate(Screen.AddEdit.createRoute(entryId)) }
            )
        }
        composable(
            route = Screen.AddEdit.route,
            arguments = listOf(navArgument("entryId") {
                type = NavType.IntType
                defaultValue = -1
            }),
            deepLinks = listOf(navDeepLink {
                uriPattern = "moodsnap://${Screen.AddEdit.route}"
            })
        ) {
            AddEditScreen(
                onBackClick = { navController.popBackStack() },
                onEntrySaved = { navController.popBackStack() }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
