package com.health.heartratemonitor.data.repository

import com.health.heartratemonitor.data.local.user.UserProfileDao
import com.health.heartratemonitor.data.local.user.UserProfileEntity
import com.health.heartratemonitor.domain.model.UserProfile
import com.health.heartratemonitor.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserProfileRepositoryImpl @Inject constructor(
    private val userProfileDao: UserProfileDao
) : UserProfileRepository {

    override suspend fun saveProfile(profile: UserProfile) {
        userProfileDao.insertProfile(
            UserProfileEntity(
                firstName = profile.firstName,
                lastName = profile.lastName,
                dob = profile.dob,
                weight = profile.weight,
                weightUnit = profile.weightUnit,
                heightFeet = profile.heightFeet,
                heightInches = profile.heightInches,
                heightMeters = profile.heightMeters,
                heightCentimeters = profile.heightCentimeters
            )
        )
    }

    override fun getProfile(): Flow<UserProfile?> {
        return userProfileDao.getProfile().map { entity ->
            entity?.let {
                UserProfile(
                    firstName = it.firstName,
                    lastName = it.lastName,
                    dob = it.dob,
                    weight = it.weight,
                    weightUnit = it.weightUnit,
                    heightFeet = it.heightFeet,
                    heightInches = it.heightInches,
                    heightMeters = it.heightMeters,
                    heightCentimeters = it.heightCentimeters
                )
            }
        }
    }
}
