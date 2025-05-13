package com.health.heartratemonitor.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.health.heartratemonitor.presentation.profile.UserProfileScreen
import com.health.heartratemonitor.presentation.screens.SplashScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // -------- On‑boarding flow --------
        composable(Screen.Splash.route)       { SplashScreen(navController) }
        composable(Screen.UserProfile.route)  {
            UserProfileScreen {
                navController.navigate(Screen.MainShell.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }

            }
        }

        composable(Screen.MainShell.route) {
            MainShell(navController)                      // see next section
        }
    }
}
