package com.health.heartratemonitor.presentation.history

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.health.heartratemonitor.R
import com.health.heartratemonitor.presentation.navigation.Screen
import com.health.heartratemonitor.presentation.utils.DateUtils.formatDate
import com.health.heartratemonitor.presentation.utils.DateUtils.formatDuration
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ActivityHistoryScreen(
    navController: NavController,
    viewModel: ActivityHistoryScreenViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    if (state.sessions.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No activity sessions found")
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.sessions) { session ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("${Screen.ActivityDetails.route}/${session.id}") },
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "Activity",
                            modifier = Modifier
                                .size(64.dp)
                                .padding(end = 16.dp)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = session.activityType,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = formatDate(session.startTimestamp),
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Duration: ${
                                    formatDuration(
                                        session.startTimestamp,
                                        session.endTimestamp
                                    )
                                }"
                            )
                            Text("HR: ${session.minHeartRate}–${session.maxHeartRate} bpm")
                            Text("Calories: ${session.caloriesBurned.toInt()} kcal")
                        }
                    }
                }
            }
        }
    }
}
