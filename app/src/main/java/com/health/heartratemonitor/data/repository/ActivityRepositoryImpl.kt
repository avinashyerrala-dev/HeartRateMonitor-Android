package com.health.heartratemonitor.data.repository

import com.health.heartratemonitor.data.local.activity.ActivityDao
import com.health.heartratemonitor.data.local.activity.ActivityGraphEntity
import com.health.heartratemonitor.data.local.activity.ActivitySessionEntityWithGraph
import com.health.heartratemonitor.data.local.activity.ActivitySummaryEntity
import com.health.heartratemonitor.domain.model.ActivitySession
import com.health.heartratemonitor.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ActivityRepositoryImpl @Inject constructor(
    private val activityDao: ActivityDao
) : ActivityRepository {

    override suspend fun insertActivity(activity: ActivitySession) {
        val summaryId = activityDao.insertActivity(
            ActivitySummaryEntity(
                activityType = activity.activityType,
                startTimestamp = activity.startTimestamp,
                endTimestamp = activity.endTimestamp,
                minHeartRate = activity.minHeartRate,
                maxHeartRate = activity.maxHeartRate,
                zone1TimeMillis = activity.zone1TimeMillis,
                zone2TimeMillis = activity.zone2TimeMillis,
                zone3TimeMillis = activity.zone3TimeMillis,
                zone4TimeMillis = activity.zone4TimeMillis,
                zone5TimeMillis = activity.zone5TimeMillis,
                caloriesBurned = activity.caloriesBurned
            )
        )

        activityDao.insertGraph(
            ActivityGraphEntity(
                sessionId = summaryId,
                heartRateGraphJson = activity.heartRateGraphJson
            )
        )
    }

    override fun getAllSessions(): Flow<List<ActivitySession>> {
        return activityDao.getAllActivities()
            .map { summaries ->
                summaries.map { summary ->
                    ActivitySession(
                        id = summary.id,
                        activityType = summary.activityType,
                        startTimestamp = summary.startTimestamp,
                        endTimestamp = summary.endTimestamp,
                        minHeartRate = summary.minHeartRate,
                        maxHeartRate = summary.maxHeartRate,
                        zone1TimeMillis = summary.zone1TimeMillis,
                        zone2TimeMillis = summary.zone2TimeMillis,
                        zone3TimeMillis = summary.zone3TimeMillis,
                        zone4TimeMillis = summary.zone4TimeMillis,
                        zone5TimeMillis = summary.zone5TimeMillis,
                        caloriesBurned = summary.caloriesBurned,
                        heartRateGraphJson = "" // We will get it from ActivityGraphEntity
                    )
                }
            }
    }

    override suspend fun getSessionById(id: Long): ActivitySession {
        return activityDao.getActivityDetails(id).toDomain()          // map to ActivitySession
    }

    private fun ActivitySessionEntityWithGraph.toDomain(): ActivitySession =
        ActivitySession(
            id                = summary.id,
            activityType      = summary.activityType,
            startTimestamp    = summary.startTimestamp,
            endTimestamp      = summary.endTimestamp,
            minHeartRate      = summary.minHeartRate,
            maxHeartRate      = summary.maxHeartRate,
            zone1TimeMillis   = summary.zone1TimeMillis,
            zone2TimeMillis   = summary.zone2TimeMillis,
            zone3TimeMillis   = summary.zone3TimeMillis,
            zone4TimeMillis   = summary.zone4TimeMillis,
            zone5TimeMillis   = summary.zone5TimeMillis,
            caloriesBurned    = summary.caloriesBurned,
            heartRateGraphJson = graph.heartRateGraphJson
        )
}
