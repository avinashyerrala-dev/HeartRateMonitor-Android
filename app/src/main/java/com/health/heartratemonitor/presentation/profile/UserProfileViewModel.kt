package com.health.heartratemonitor.presentation.profile

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.health.heartratemonitor.domain.model.UserProfile
import com.health.heartratemonitor.domain.usecases.profile.CheckUserProfileExistUseCase
import com.health.heartratemonitor.domain.usecases.profile.GetUserProfileUseCase
import com.health.heartratemonitor.domain.usecases.profile.SaveUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val checkUserProfileExistUseCase: CheckUserProfileExistUseCase,
    private val saveUserProfileUseCase: SaveUserProfileUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase
) : ViewModel() {

    val firstName = MutableStateFlow("")
    val lastName = MutableStateFlow("")
    val dob = MutableStateFlow(TextFieldValue(""))
    val weight = MutableStateFlow("")
    val weightUnit = MutableStateFlow("kg")
    val heightUnit = MutableStateFlow("Imperial")
    val heightFeet = MutableStateFlow<String?>(null)
    val heightInches = MutableStateFlow<String?>(null)
    val heightMeters = MutableStateFlow<String?>(null)
    val heightCentimeters = MutableStateFlow<String?>(null)

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess

    suspend fun checkUserProfileExists(): Boolean {
        return checkUserProfileExistUseCase()
    }

    fun saveProfile() {
        viewModelScope.launch {
            val profile = UserProfile(
                firstName = firstName.value,
                lastName = lastName.value,
                dob = dob.value.text,
                weight = weight.value.toDoubleOrNull() ?: 0.0,
                weightUnit = weightUnit.value,
                heightFeet = heightFeet.value?.toIntOrNull(),
                heightInches = heightInches.value?.toIntOrNull(),
                heightMeters = heightMeters.value?.toDoubleOrNull(),
                heightCentimeters = heightCentimeters.value?.toDoubleOrNull()
            )
            saveUserProfileUseCase(profile)
            _saveSuccess.value = true
        }
    }

    fun loadProfile() {
        viewModelScope.launch {
            getUserProfileUseCase().collect { profile ->
                profile?.let {
                    firstName.value = it.firstName
                    lastName.value = it.lastName
                    dob.value = TextFieldValue(it.dob)
                    weight.value = it.weight.toString()
                    weightUnit.value = it.weightUnit
                    heightFeet.value = it.heightFeet?.toString()
                    heightInches.value = it.heightInches?.toString()
                    heightMeters.value = it.heightMeters?.toString()
                    heightCentimeters.value = it.heightCentimeters?.toString()
                }
            }
        }
    }
}
