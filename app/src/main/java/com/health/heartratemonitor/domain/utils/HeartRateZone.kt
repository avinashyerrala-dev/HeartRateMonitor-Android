package com.health.heartratemonitor.domain.utils

enum class HeartRateZone {
    ZONE1, // 50–60% (Very Light)
    ZONE2, // 60–70% (Light)
    ZONE3, // 70–80% (Moderate)
    ZONE4, // 80–90% (Hard)
    ZONE5  // 90–100% (Maximum)
}

object HeartRateZoneCalculator {

    fun calculateAge(dob: String): Int {
        // Assume DOB format is yyyy-MM-dd
        val birthYear = dob.substring(0, 4).toIntOrNull() ?: return 30 // fallback age 30
        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        return currentYear - birthYear
    }

    fun getHeartRateZone(age: Int, currentHR: Int): HeartRateZone {
        val maxHR = 220 - age
        val percentage = (currentHR.toDouble() / maxHR) * 100

        return when {
            percentage < 60 -> HeartRateZone.ZONE1
            percentage < 70 -> HeartRateZone.ZONE2
            percentage < 80 -> HeartRateZone.ZONE3
            percentage < 90 -> HeartRateZone.ZONE4
            else -> HeartRateZone.ZONE5
        }
    }
}