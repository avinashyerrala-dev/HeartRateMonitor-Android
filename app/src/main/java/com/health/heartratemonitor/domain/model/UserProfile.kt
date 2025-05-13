package com.health.heartratemonitor.domain.model

data class UserProfile(
    val firstName: String,
    val lastName: String,
    val dob: String,
    val weight: Double,
    val weightUnit: String,
    val heightFeet: Int?,
    val heightInches: Int?,
    val heightMeters: Double?,
    val heightCentimeters: Double?
)