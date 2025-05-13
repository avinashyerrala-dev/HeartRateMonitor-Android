package com.health.heartratemonitor.presentation.utils

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PermissionDeniedScreen(
    permanentlyDenied: List<String>,
    needRationale: List<String>,
    onRequestAgain: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val pretty = mapOf(
        Manifest.permission.BLUETOOTH_SCAN      to "Bluetooth Scan",
        Manifest.permission.BLUETOOTH_CONNECT   to "Bluetooth Connect",
        Manifest.permission.ACCESS_FINE_LOCATION to "Location",
        Manifest.permission.POST_NOTIFICATIONS   to "Notifications"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Permissions required", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        if (needRationale.isNotEmpty()) {
            Text("Please grant:", fontWeight = FontWeight.SemiBold)
            needRationale.forEach { Text("• ${pretty[it] ?: it}") }
            Spacer(Modifier.height(12.dp))
            Button(onClick = onRequestAgain) {
                Icon(Icons.Filled.Refresh, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Grant again")
            }
        }

        if (permanentlyDenied.isNotEmpty()) {
            Spacer(Modifier.height(18.dp))
            Text("Enable in App Settings:", fontWeight = FontWeight.SemiBold)
            permanentlyDenied.forEach { Text("• ${pretty[it] ?: it}") }
            Spacer(Modifier.height(12.dp))
            Button(onClick = onOpenSettings) {
                Icon(Icons.Filled.Settings, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Open Settings")
            }
        }
    }
}

