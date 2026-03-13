package net.maiatoday.moodsnap.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object History : Screen("history")
    object AddEdit : Screen("add_edit?entryId={entryId}") {
        fun createRoute(entryId: Int?) = "add_edit?entryId=$entryId"
    }
    object Settings : Screen("settings")
}
