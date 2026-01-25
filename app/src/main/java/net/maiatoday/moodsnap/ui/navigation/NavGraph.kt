package net.maiatoday.moodsnap.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import net.maiatoday.moodsnap.ui.addedit.AddEditScreen
import net.maiatoday.moodsnap.ui.home.HomeScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                onAddEntry = { navController.navigate(Screen.AddEdit.createRoute(null)) },
                onEntryClick = { entryId -> navController.navigate(Screen.AddEdit.createRoute(entryId)) }
            )
        }
        composable(
            route = Screen.AddEdit.route,
            arguments = listOf(navArgument("entryId") {
                type = NavType.IntType
                defaultValue = -1
            })
        ) {
            AddEditScreen(onEntrySaved = { navController.popBackStack() })
        }
    }
}
