package com.health.heartratemonitor.domain.usecases.activity

import com.health.heartratemonitor.domain.actvity.ActivityTrackerManager
import com.health.heartratemonitor.domain.actvity.ActivityTrackingResult
import com.health.heartratemonitor.domain.model.ActivitySession
import com.health.heartratemonitor.domain.model.UserProfile
import com.health.heartratemonitor.domain.repository.ActivityRepository
import com.health.heartratemonitor.domain.repository.UserProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FinishActivitySessionUseCase @Inject constructor(
    private val activityTrackerManager: ActivityTrackerManager,
    private val activityRepository: ActivityRepository,
    private val userProfileRepository: UserProfileRepository
) {
    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        val result = activityTrackerManager.stopTracking()
        val profile = userProfileRepository.getProfile().firstOrNull() ?: return@withContext

        val session = ActivitySession(
            id = 0L,
            activityType = "Unknown",
            startTimestamp = result.startTimestamp,
            endTimestamp = result.endTimestamp,
            minHeartRate = result.minHeartRate,
            maxHeartRate = result.maxHeartRate,
            zone1TimeMillis = result.zone1TimeMillis,
            zone2TimeMillis = result.zone2TimeMillis,
            zone3TimeMillis = result.zone3TimeMillis,
            zone4TimeMillis = result.zone4TimeMillis,
            zone5TimeMillis = result.zone5TimeMillis,
            caloriesBurned = result.calculateCaloriesBurned(profile),
            heartRateGraphJson = result.heartRateGraph.toJson()
        )

        activityRepository.insertActivity(session)
    }

    fun trackingState(): StateFlow<Boolean> = activityTrackerManager.isTracking

    private fun ActivityTrackingResult.calculateCaloriesBurned(profile: UserProfile): Double {
        val durationMinutes = (endTimestamp - startTimestamp) / 1000.0 / 60.0
        println("Duration in minutes: $durationMinutes")
        val avgHr = if (heartRateGraph.isNotEmpty()) heartRateGraph.map { it.second }.average() else 0.0
        println("Average HR: $avgHr")
        val age = calculateAge(profile.dob)
        println("Age: $age")
        val weightKg = if (profile.weightUnit.lowercase() == "lb") profile.weight * 0.453592 else profile.weight
        println("Weight in kg: $weightKg")

        // Default gender-neutral formula
        val rawCalories = ((age * 0.2017) - (weightKg * 0.09036) + (avgHr * 0.6309) - 55.0969) * durationMinutes / 4.184

        println("Calories burned: $rawCalories")

        /*
        // If gender is available in profile (e.g., profile.gender == "male" or "female"), use:
        return if (profile.gender == "male") {
            ((age * 0.2017) - (weightKg * 0.09036) + (avgHr * 0.6309) - 55.0969) * durationMinutes / 4.184
        } else {
            ((age * 0.074) - (weightKg * 0.05741) + (avgHr * 0.4472) - 20.4022) * durationMinutes / 4.184
        }
        */

        return rawCalories.coerceAtLeast(0.0)
    }

    private fun calculateAge(dob: String): Int {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val birthDate = sdf.parse(dob)
            val now = Calendar.getInstance()
            val dobCal = Calendar.getInstance().apply { time = birthDate!! }
            var age = now.get(Calendar.YEAR) - dobCal.get(Calendar.YEAR)
            if (now.get(Calendar.DAY_OF_YEAR) < dobCal.get(Calendar.DAY_OF_YEAR)) {
                age--
            }
            age
        } catch (e: Exception) {
            30 // default fallback
        }
    }

    private fun List<Pair<Long, Int>>.toJson(): String {
        return JSONArray(this.map {
            JSONObject().apply {
                put("timestamp", it.first)
                put("bpm", it.second)
            }
        }).toString()
    }
}
