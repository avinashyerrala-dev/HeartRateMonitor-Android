package com.health.heartratemonitor.data.local.activity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "activity_summary")
data class ActivitySummaryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
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
    val caloriesBurned: Double
)

@Entity(
    tableName = "activity_graph",
    foreignKeys = [
        ForeignKey(
            entity = ActivitySummaryEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["sessionId"])]
)
data class ActivityGraphEntity(
    @PrimaryKey val sessionId: Long,
    val heartRateGraphJson: String
)