package com.example.fitnessapp.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.local.entity.UserProfileEntity
import com.example.fitnessapp.data.repository.FoodRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: FoodRepository) : ViewModel() {
    val userProfile: StateFlow<UserProfileEntity?> = repository.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun saveProfile(age: Int, gender: String, height: Double, currentWeight: Double, targetWeight: Double) {
        viewModelScope.launch {
            repository.saveUserProfile(
                UserProfileEntity(
                    age = age,
                    gender = gender,
                    height = height,
                    currentWeight = currentWeight,
                    targetWeight = targetWeight
                )
            )
        }
    }
}
