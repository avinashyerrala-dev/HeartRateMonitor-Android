package com.health.heartratemonitor.domain.usecases.profile

import com.health.heartratemonitor.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CheckUserProfileExistUseCase @Inject constructor(
    private val repository: UserProfileRepository
) {
    suspend operator fun invoke(): Boolean {
        return repository.getProfile().first() != null
    }
}
