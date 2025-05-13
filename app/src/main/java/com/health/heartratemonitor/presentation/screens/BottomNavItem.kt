package com.health.heartratemonitor.presentation.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

// ──────────────────────────────────────────────────────────────────────────────
// 1 ▸ Model: each tab knows the *graph* route   ────────────────────────────────
sealed class BottomNavItem(
    /** route of the navigation(graph) block, e.g. "home_graph"              */
    val graphRoute: String,
    /** route of the first screen in that graph, e.g. "home"                 */
    val startRoute: String,
    val icon: ImageVector,
    val label: String
) {
    data object Home     : BottomNavItem("home_graph",     "home",     Icons.Filled.Home,     "Home")
    data object Activity : BottomNavItem("activity_graph", "activity", Icons.Filled.Favorite, "Activity")
    data object Settings : BottomNavItem("settings_graph", "settings", Icons.Filled.Settings, "Settings")
}

// ──────────────────────────────────────────────────────────────────────────────
// 2 ▸ UI: NavigationBar that keeps each tab’s back‑stack intact  ──────────────
@Composable
fun BottomNavBar(navController: NavController) {

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Activity,
        BottomNavItem.Settings
    )

    // Destination that is currently visible
    val currentDestination =
        navController.currentBackStackEntryAsState().value?.destination

    NavigationBar {
        items.forEach { item ->

            val selected = currentDestination
                ?.hierarchy           // <‑‑ checks parents too (graph route)
                ?.any { it.route == item.graphRoute } == true

            NavigationBarItem(
                selected = selected,
                icon      = { Icon(item.icon, contentDescription = item.label) },
                label     = { Text(item.label) },

                // ─── click handler ───────────────────────────────────────────
                onClick = {
                    navController.navigate(item.graphRoute) {
                        // pop to the *root* of the host, not to the tab itself
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }

                        launchSingleTop = true   // avoid duplicate copies
                        restoreState     = true   // restore tab state if re‑selected
                    }
                }
            )
        }
    }
}
