package com.health.heartratemonitor.presentation.history

import com.health.heartratemonitor.domain.model.ActivitySession

data class HistoryUiState(
    val sessions: List<ActivitySession> = emptyList()
)

sealed interface HistoryUiEvent {
    data object LoadSessions : HistoryUiEvent
}
