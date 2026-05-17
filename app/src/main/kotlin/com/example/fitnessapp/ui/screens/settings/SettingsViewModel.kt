package com.example.fitnessapp.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.local.entity.UserProfileEntity
import com.example.fitnessapp.data.repository.FoodRepository
import com.example.fitnessapp.domain.model.Sex
import com.example.fitnessapp.domain.model.TrainingGoal
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: FoodRepository) : ViewModel() {
    val userProfile: StateFlow<UserProfileEntity?> = repository.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun calculateNutrition(
        age: Int,
        sex: Sex,
        heightCm: Double,
        weightKg: Double,
        goal: TrainingGoal,
        activityLevel: Double = 1.375 // Default to Lightly Active
    ): NutritionResult {
        // Mifflin-St Jeor Equation
        val bmr = if (sex == Sex.MALE) {
            (10 * weightKg) + (6.25 * heightCm) - (5 * age) + 5
        } else {
            (10 * weightKg) + (6.25 * heightCm) - (5 * age) - 161
        }

        val tdee = bmr * activityLevel

        val targetCalories = when (goal) {
            TrainingGoal.CUT -> tdee - 500
            TrainingGoal.MAINTAIN -> tdee
            TrainingGoal.LEAN_BULK -> tdee + 250
            TrainingGoal.BULK -> tdee + 500
            TrainingGoal.UNKNOWN -> tdee
        }

        val proteinPerKg = if (age >= 50) 1.8 else 1.6
        val proteinGrams = weightKg * proteinPerKg
        
        // Fat recommendation: 25% of target calories (9 kcal/g) or at least 0.6g/kg
        val fatFromCalories = (targetCalories * 0.25) / 9.0
        val fatMinimum = weightKg * 0.6
        val fatGrams = maxOf(fatFromCalories, fatMinimum)

        return NutritionResult(
            calories = targetCalories,
            tdee = tdee,
            protein = proteinGrams,
            fat = fatGrams
        )
    }

    fun saveProfile(profile: UserProfileEntity) {
        viewModelScope.launch {
            repository.saveUserProfile(profile)
        }
    }
}

data class NutritionResult(
    val calories: Double,
    val tdee: Double,
    val protein: Double,
    val fat: Double
)
