package com.health.heartratemonitor.domain.model

data class ActivitySession(
    val id: Long,
    val activityType: String,
    val startTimestamp: Long,
    val endTimestamp: Long,
    val minHeartRate: Int,
    val maxHeartRate: Int,
    val zone1TimeMillis: Long,
    val zone2TimeMillis: Long,
    val zone3TimeMillis: Long,
    val zone4TimeMillis: Long,
    val zone5TimeMillis: Long,
    val caloriesBurned: Double,
    val heartRateGraphJson: String
)
