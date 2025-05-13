package com.health.heartratemonitor.domain.history

import com.health.heartratemonitor.domain.model.ActivitySession
import com.health.heartratemonitor.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAllActivitySessionsUseCase @Inject constructor(
    private val repository: ActivityRepository
) {
    operator fun invoke(): Flow<List<ActivitySession>> {
        return repository.getAllSessions()
    }
}
