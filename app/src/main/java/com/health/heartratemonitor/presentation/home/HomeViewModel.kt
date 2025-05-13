package com.health.heartratemonitor.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.health.heartratemonitor.domain.usecases.activity.FinishActivitySessionUseCase
import com.health.heartratemonitor.domain.usecases.profile.GetUserProfileUseCase
import com.health.heartratemonitor.domain.utils.HeartRateZoneCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val finishActivitySessionUseCase: FinishActivitySessionUseCase
) : ViewModel() {

    private var age: Int = 30

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _startServiceEvent = MutableSharedFlow<Unit>(replay = 0)
    val startServiceEvent = _startServiceEvent

    private val _finishServiceEvent = MutableSharedFlow<Unit>(replay = 0)
    val finishServiceEvent = _finishServiceEvent

    init {
        viewModelScope.launch {
            getUserProfileUseCase().collectLatest { profile ->
                profile?.dob?.let { dob ->
                    age = HeartRateZoneCalculator.calculateAge(dob)
                }
            }
        }

        viewModelScope.launch {
            finishActivitySessionUseCase.trackingState().collectLatest { tracking ->
                _uiState.value = _uiState.value.copy(isTracking = tracking)
            }
        }
    }

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            HomeUiEvent.ToggleSheet -> {
                _uiState.value = _uiState.value.copy(isSheetOpen = !_uiState.value.isSheetOpen)
            }
            HomeUiEvent.StartTracking -> {
                viewModelScope.launch { _startServiceEvent.emit(Unit) }
            }
            HomeUiEvent.StopTracking -> {
                viewModelScope.launch { _finishServiceEvent.emit(Unit) }
            }
            is HomeUiEvent.UpdateHeartRate -> {
                val zone = HeartRateZoneCalculator.getHeartRateZone(age, event.bpm)
                _uiState.value = _uiState.value.copy(
                    heartRate = event.bpm,
                    heartRateZone = zone
                )
            }

            is HomeUiEvent.UpdateConnectedAddress -> _uiState.value = _uiState.value.copy(connectedAddress = event.address)
        }
    }

    fun updateConnectedAddress(address: String?) {
        _uiState.value = _uiState.value.copy(connectedAddress = address)
    }
}
