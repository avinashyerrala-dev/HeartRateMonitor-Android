package com.health.heartratemonitor.domain.repository

import com.health.heartratemonitor.domain.model.ActivitySession
import kotlinx.coroutines.flow.Flow

interface ActivityRepository {
    suspend fun insertActivity(activity: ActivitySession)
    fun getAllSessions(): Flow<List<ActivitySession>>
    suspend fun getSessionById(id: Long): ActivitySession?
}
