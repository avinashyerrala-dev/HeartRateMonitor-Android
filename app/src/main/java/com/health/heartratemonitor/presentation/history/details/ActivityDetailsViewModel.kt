package com.health.heartratemonitor.presentation.history.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.health.heartratemonitor.domain.usecases.history.GetActivityDetailsUseCase
import com.health.heartratemonitor.domain.model.ActivitySession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getDetails: GetActivityDetailsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActivityDetailsUiState())
    val uiState: StateFlow<ActivityDetailsUiState> = _uiState.asStateFlow()

    init {
        val id: Long = checkNotNull(savedStateHandle["id"]).toString().toLong()
        viewModelScope.launch {
            getDetails(id).collect { session ->
                session?.let {
                    _uiState.value = _uiState.value.copy(session = it)
                }
            }
        }
    }
}