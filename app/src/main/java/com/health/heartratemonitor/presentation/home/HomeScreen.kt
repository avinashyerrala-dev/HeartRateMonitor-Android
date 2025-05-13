package com.health.heartratemonitor.presentation.home

import android.content.Intent
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.health.heartratemonitor.domain.utils.HeartRateZone
import com.health.heartratemonitor.presentation.navigation.Screen
import com.health.heartratemonitor.presentation.theme.Zone1Color
import com.health.heartratemonitor.presentation.theme.Zone2Color
import com.health.heartratemonitor.presentation.theme.Zone3Color
import com.health.heartratemonitor.presentation.theme.Zone4Color
import com.health.heartratemonitor.presentation.theme.Zone5Color
import com.health.heartratemonitor.presentation.viewmodel.BleViewModel
import com.health.heartratemonitor.service.ActivityTrackingService
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen( bleViewModel: BleViewModel) {
    val homeViewModel: HomeViewModel = hiltViewModel()
    val context = LocalContext.current

    val uiState by homeViewModel.uiState.collectAsState()

    // Update HomeViewModel with live heart rate
    bleViewModel.heartRate.collectAsState().value?.let { hr ->
        homeViewModel.onEvent(HomeUiEvent.UpdateHeartRate(hr))
    }

    // Update connected address
    LaunchedEffect(Unit) {
        bleViewModel.connectedAddress.collect { address ->
            address?.let {
                homeViewModel.onEvent(HomeUiEvent.UpdateConnectedAddress(address))
            }
        }
    }

    // Listen for service events
    LaunchedEffect(Unit) {
        homeViewModel.startServiceEvent.collectLatest {
            uiState.connectedAddress?.takeIf { it.isNotEmpty() }?.let { address ->
                val intent = Intent(context, ActivityTrackingService::class.java).apply {
                    putExtra("address", address)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // Start the service in the foreground for Android Oreo and above
                    ContextCompat.startForegroundService(context, intent)
                } else {
                    // Start the service in the background for older versions
                    context.startService(intent)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        homeViewModel.finishServiceEvent.collect {
            val intent = Intent(context, ActivityTrackingService::class.java).apply {
                action = ActivityTrackingService.ACTION_FINISH_ACTIVITY
            }
            context.startService(intent)
        }
    }

    val backgroundColor = uiState.heartRate?.takeIf { it > 0 }?.let {
        when (uiState.heartRateZone) {
            HeartRateZone.ZONE1 -> Zone1Color
            HeartRateZone.ZONE2 -> Zone2Color
            HeartRateZone.ZONE3 -> Zone3Color
            HeartRateZone.ZONE4 -> Zone4Color
            HeartRateZone.ZONE5 -> Zone5Color
        }
    } ?: MaterialTheme.colorScheme.background

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (uiState.heartRate != null && uiState.heartRate!! > 0) {
                Text(
                    text = "\u2764\uFE0F ${uiState.heartRate} bpm\nZone: ${uiState.heartRateZone.name}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = "Waiting for heart rate...",
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            if (!uiState.connectedAddress.isNullOrEmpty()) {
                Button(onClick = {
                    homeViewModel.onEvent(
                        if (uiState.isTracking) HomeUiEvent.StopTracking
                        else HomeUiEvent.StartTracking
                    )
                }) {
                    Text(if (uiState.isTracking) "Finish Activity" else "Start Activity")
                }
            }
        }
    }
}
