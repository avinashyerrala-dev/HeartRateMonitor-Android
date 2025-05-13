package com.health.heartratemonitor.domain.history

import com.health.heartratemonitor.domain.model.ActivitySession
import com.health.heartratemonitor.domain.repository.ActivityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetActivityDetailsUseCase @Inject constructor(
    private val repo: ActivityRepository
) {
    suspend operator fun invoke(id: Long): Flow<ActivitySession?> = flow{
     emit(repo.getSessionById(id))
    }.flowOn(Dispatchers.IO)
}
