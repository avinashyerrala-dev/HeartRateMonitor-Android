package com.health.heartratemonitor.presentation.navigation

sealed class Screen(val route: String) {
    // Root‑level (never shown as a screen)
    data object MainShell : Screen("main_shell")


    // Pre‑main flow
    data object Splash     : Screen("splash")
    data object UserProfile: Screen("user_profile")

    // Tabs (graph routes)
    data object HomeGraph      : Screen("home_graph")
    data object ActivityGraph  : Screen("activity_graph")
    data object SettingsGraph  : Screen("settings_graph")

    // Destinations **inside** those graphs
    data object Home       : Screen("home")
    data object Activity   : Screen("activity")
    data object ActivityDetails : Screen("activity_details")
    data object Settings   : Screen("settings")
}
