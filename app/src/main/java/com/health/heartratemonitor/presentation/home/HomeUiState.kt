package com.health.heartratemonitor.presentation.home

import com.health.heartratemonitor.domain.utils.HeartRateZone

data class HomeUiState(
    val heartRate: Int? = null,
    val deviceName: String = "Disconnected",
    val isSheetOpen: Boolean = false,
    val heartRateZone: HeartRateZone = HeartRateZone.ZONE1,
    val isTracking: Boolean = false,
    val connectedAddress: String? = null
)

sealed interface HomeUiEvent {
    data object ToggleSheet : HomeUiEvent
    data object StartTracking : HomeUiEvent
    data object StopTracking : HomeUiEvent
    data class UpdateHeartRate(val bpm: Int) : HomeUiEvent
    data class UpdateConnectedAddress(val address: String) : HomeUiEvent
}
