package net.maiatoday.moodsnap.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddEdit : Screen("add_edit?entryId={entryId}") {
        fun createRoute(entryId: Int?) = "add_edit?entryId=$entryId"
    }
}