package com.health.heartratemonitor.presentation.history.details

import com.health.heartratemonitor.domain.model.ActivitySession

data class ActivityDetailsUiState(
    val session: ActivitySession? = null
)