package com.health.heartratemonitor.domain.usecases.profile

import com.health.heartratemonitor.domain.model.UserProfile
import com.health.heartratemonitor.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val repository: UserProfileRepository
) {
    operator fun invoke(): Flow<UserProfile?> {
        return repository.getProfile()
    }
}
