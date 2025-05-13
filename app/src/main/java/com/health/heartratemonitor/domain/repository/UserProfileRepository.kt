package com.health.heartratemonitor.domain.repository

import com.health.heartratemonitor.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    suspend fun saveProfile(profile: UserProfile)
    fun getProfile(): Flow<UserProfile?>
}