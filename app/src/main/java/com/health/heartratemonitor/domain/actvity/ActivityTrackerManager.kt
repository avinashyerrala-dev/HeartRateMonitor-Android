package com.health.heartratemonitor.domain.actvity

import com.health.heartratemonitor.domain.utils.HeartRateZone
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityTrackerManager @Inject constructor(){

    private var startTimeMillis: Long = 0L
    private var endTimeMillis: Long = 0L

    private var minHeartRate = Int.MAX_VALUE
    private var maxHeartRate = Int.MIN_VALUE

    private val zoneTimeCounters = mutableMapOf<HeartRateZone, Long>()

    private val heartRateGraph = mutableListOf<Pair<Long, Int>>()

    private var currentZone: HeartRateZone? = null
    private var lastZoneChangeTime: Long = 0L

    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

    fun startTracking() {
        startTimeMillis = System.currentTimeMillis()
        lastZoneChangeTime = startTimeMillis
        _isTracking.value = true
    }

    fun updateHeartRate(currentHeartRate: Int, zone: HeartRateZone) {
        if (!_isTracking.value) return

        val now = System.currentTimeMillis()

        // Update min/max
        if (currentHeartRate < minHeartRate) minHeartRate = currentHeartRate
        if (currentHeartRate > maxHeartRate) maxHeartRate = currentHeartRate

        // Update zone timers
        if (currentZone == null) {
            currentZone = zone
            lastZoneChangeTime = now
        } else if (currentZone != zone) {
            val timeSpent = now - lastZoneChangeTime
            zoneTimeCounters[currentZone!!] = zoneTimeCounters.getOrDefault(currentZone!!, 0L) + timeSpent
            currentZone = zone
            lastZoneChangeTime = now
        }

        // Save HR point for graph
        heartRateGraph.add(Pair(now, currentHeartRate))
    }

    fun stopTracking(): ActivityTrackingResult {
        if (!_isTracking.value) throw IllegalStateException("Tracking not started")

        endTimeMillis = System.currentTimeMillis()
        _isTracking.value = false

        // Final zone update
        val now = endTimeMillis
        val timeSpent = now - lastZoneChangeTime
        zoneTimeCounters[currentZone!!] = zoneTimeCounters.getOrDefault(currentZone!!, 0L) + timeSpent

        return ActivityTrackingResult(
            startTimestamp = startTimeMillis,
            endTimestamp = endTimeMillis,
            minHeartRate = if (minHeartRate == Int.MAX_VALUE) 0 else minHeartRate,
            maxHeartRate = if (maxHeartRate == Int.MIN_VALUE) 0 else maxHeartRate,
            zone1TimeMillis = zoneTimeCounters[HeartRateZone.ZONE1] ?: 0L,
            zone2TimeMillis = zoneTimeCounters[HeartRateZone.ZONE2] ?: 0L,
            zone3TimeMillis = zoneTimeCounters[HeartRateZone.ZONE3] ?: 0L,
            zone4TimeMillis = zoneTimeCounters[HeartRateZone.ZONE4] ?: 0L,
            zone5TimeMillis = zoneTimeCounters[HeartRateZone.ZONE5] ?: 0L,
            heartRateGraph = heartRateGraph
        )
    }
}

data class ActivityTrackingResult(
    val startTimestamp: Long,
    val endTimestamp: Long,
    val minHeartRate: Int,
    val maxHeartRate: Int,
    val zone1TimeMillis: Long,
    val zone2TimeMillis: Long,
    val zone3TimeMillis: Long,
    val zone4TimeMillis: Long,
    val zone5TimeMillis: Long,
    val heartRateGraph: List<Pair<Long, Int>>
)
