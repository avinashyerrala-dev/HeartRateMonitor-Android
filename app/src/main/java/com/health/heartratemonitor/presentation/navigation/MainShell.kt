package com.health.heartratemonitor.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.health.heartratemonitor.presentation.components.HRMScaffold
import com.health.heartratemonitor.presentation.history.ActivityHistoryScreen
import com.health.heartratemonitor.presentation.history.details.ActivityDetailsScreen
import com.health.heartratemonitor.presentation.home.HomeScreen
import com.health.heartratemonitor.presentation.screens.BottomNavBar
import com.health.heartratemonitor.presentation.screens.ConnectionOverlay
import com.health.heartratemonitor.presentation.settings.SettingsScreen
import com.health.heartratemonitor.presentation.viewmodel.BleViewModel

@Composable
fun MainShell(rootNavController: NavHostController) {

    /* ---------- BLE‑related state lifted to this level ---------- */
    val bleVm: BleViewModel = hiltViewModel()
    val deviceName          by bleVm.connectedDeviceName.collectAsState()
    val availableDevices    by bleVm.availableDevices.collectAsState()
    val prevDevices         by bleVm.previouslyConnectedDevices.collectAsState()
    val connectingAddress   by bleVm.connectingAddress.collectAsState()
    val connectedAddress    by bleVm.connectedAddress.collectAsState()

    var showOverlay by remember { mutableStateOf(false) }

    /* ---------- *child* NavHostController for the three graphs -- */
    val tabNavController = rememberNavController()

    LaunchedEffect(showOverlay) {
        if (showOverlay) {
            bleVm.startScan()
        } else {
            bleVm.stopScan()
        }
    }

    HRMScaffold(
        title           = deviceName,
        showOverlay     = showOverlay,
        onToggleOverlay = { showOverlay = !showOverlay },
        bottomBar       = { BottomNavBar(tabNavController) },
        overlayContent  = {
            ConnectionOverlay(
                visible              = showOverlay,
                onDismiss            = { showOverlay = false },
                previouslyConnected  = prevDevices.map { it.name.orEmpty() to it.address },
                availableDevices     = availableDevices.map { it.name.orEmpty() to it.address },
                connectingAddress    = connectingAddress,
                connectedAddress     = connectedAddress,
                availableDeviceAddresses = availableDevices.map { it.address },
                onDeviceClick = { addr ->
                    (availableDevices + prevDevices)
                        .firstOrNull { it.address == addr }
                        ?.let { bleVm.connectToDevice(it.address, it.name.orEmpty()) }
                    showOverlay = false
                }
            )
        }
    ) { innerMod ->

        /* --- NavHost visible *only inside* HRMScaffold -------------- */
        NavHost(
            navController    = tabNavController,
            startDestination = Screen.HomeGraph.route,    // ◄── graph route!
            modifier         = innerMod.fillMaxSize()
        ) {
            /* ─ Home tab graph ───────────────────────────────────────────── */
            navigation(
                route            = Screen.HomeGraph.route,    // "home_graph"
                startDestination = Screen.Home.route          // "home"
            ) {
                addHomeGraph(rootNavController, bleVm)                                // "home", details …
            }

            /* ─ Activity tab graph ───────────────────────────────────────── */
            navigation(
                route            = Screen.ActivityGraph.route,  // "activity_graph"
                startDestination = Screen.Activity.route        // "activity"
            ) {
                addActivityGraph(tabNavController)
                composable(
                    route = "${Screen.ActivityDetails.route}/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.LongType })
                ) { backStackEntry ->
                    ActivityDetailsScreen()
                }
            }

            /* ─ Settings tab graph ───────────────────────────────────────── */
            navigation(
                route            = Screen.SettingsGraph.route,  // "settings_graph"
                startDestination = Screen.Settings.route        // "settings"
            ) {
                addSettingsGraph(tabNavController)
            }
        }

    }
}
fun NavGraphBuilder.addHomeGraph(rootNavController: NavHostController, bleViewModel: BleViewModel) {
    composable(Screen.Home.route) {
        HomeScreen(bleViewModel = bleViewModel)             // pass vm or state down
    }

    // Example detail screen reachable from Home
    /* composable("home/detail/{id}") { … } */
}

fun NavGraphBuilder.addActivityGraph(nav: NavHostController) {
    composable(Screen.Activity.route) { ActivityHistoryScreen(nav) }
}

fun NavGraphBuilder.addSettingsGraph(nav: NavHostController) {
    composable(Screen.Settings.route) { SettingsScreen() }
}
