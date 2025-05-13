package com.health.heartratemonitor.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.health.heartratemonitor.domain.history.GetAllActivitySessionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityHistoryScreenViewModel @Inject constructor(
    private val getAllActivitySessionsUseCase: GetAllActivitySessionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        onEvent(HistoryUiEvent.LoadSessions)
    }

    fun onEvent(event: HistoryUiEvent) {
        when (event) {
            HistoryUiEvent.LoadSessions -> {
                viewModelScope.launch {
                    getAllActivitySessionsUseCase().collect { list ->
                        _uiState.value = _uiState.value.copy(sessions = list)
                    }
                }
            }
        }
    }
}
