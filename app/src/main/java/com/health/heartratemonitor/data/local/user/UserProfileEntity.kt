package com.health.heartratemonitor.data.local.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserProfileEntity(
    @PrimaryKey val id: Int = 0,
    val firstName: String,
    val lastName: String,
    val dob: String, // Stored as ISO date (yyyy-MM-dd)
    val weight: Double,
    val weightUnit: String, // "kg" or "lb"
    val heightFeet: Int?,
    val heightInches: Int?,
    val heightMeters: Double?,
    val heightCentimeters: Double?
)
