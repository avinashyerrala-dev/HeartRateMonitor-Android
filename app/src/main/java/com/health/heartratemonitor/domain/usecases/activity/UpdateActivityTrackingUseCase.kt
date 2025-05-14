package com.health.heartratemonitor.domain.usecases.activity

import com.health.heartratemonitor.domain.service.ActivityTrackerManager
import com.health.heartratemonitor.domain.utils.HeartRateZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateActivityTrackingUseCase @Inject constructor(
    private val trackerManager: ActivityTrackerManager
) {
    operator fun invoke(currentHeartRate: Int, zone: HeartRateZone) {
        trackerManager.updateHeartRate(currentHeartRate, zone)
    }
}