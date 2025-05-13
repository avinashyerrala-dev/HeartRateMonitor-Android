package com.health.heartratemonitor.domain.usecases.activity

import com.health.heartratemonitor.domain.actvity.ActivityTrackerManager
import javax.inject.Inject


class StartActivitySessionUseCase @Inject constructor(
    private val activityTrackerManager: ActivityTrackerManager
) {
    operator fun invoke() {
        activityTrackerManager.startTracking()
    }
}
