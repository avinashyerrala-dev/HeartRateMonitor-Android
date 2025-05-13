package com.health.heartratemonitor.domain.usecases.profile

import com.health.heartratemonitor.domain.model.UserProfile
import com.health.heartratemonitor.domain.repository.UserProfileRepository
import javax.inject.Inject

class SaveUserProfileUseCase @Inject constructor(
    private val repository: UserProfileRepository
) {
    suspend operator fun invoke(profile: UserProfile) {
        repository.saveProfile(profile)
    }
}
