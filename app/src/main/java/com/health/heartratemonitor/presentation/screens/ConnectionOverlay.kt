package com.health.heartratemonitor.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ConnectionOverlay(
    visible: Boolean,
    onDismiss: () -> Unit,
    previouslyConnected: List<Pair<String, String>> = listOf(),
    availableDevices: List<Pair<String, String>> = listOf(),
    connectingAddress: String? = null,
    connectedAddress: String? = null,
    availableDeviceAddresses: List<String> = listOf(),
    onDeviceClick: (String) -> Unit = {}
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it })
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Collapse",
                    modifier = Modifier.clickable { onDismiss() }
                )
            }

            Text("Previously Connected", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn {
                items(previouslyConnected) { (name, mac) ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDeviceClick(mac) }
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(name, fontWeight = FontWeight.Bold)
                                Text(mac, style = MaterialTheme.typography.bodySmall)
                            }
                            if (mac in availableDeviceAddresses) {
                                Text(
                                    when {
                                        mac == connectingAddress -> "Connecting"
                                        mac == connectedAddress -> "Connected"
                                        else -> "Ready"
                                    }
                                )
                            }
                        }
                    }
                    Divider()
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Available Devices", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn {
                items(availableDevices) { (name, mac) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDeviceClick(mac) }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(name, fontWeight = FontWeight.Bold)
                            Text(mac, style = MaterialTheme.typography.bodySmall)
                        }
                        Text(
                            when {
                                mac == connectingAddress -> "Connecting"
                                mac == connectedAddress -> "Connected"
                                else -> "Ready"
                            }
                        )
                    }
                    Divider()
                }
            }
        }
    }
}
