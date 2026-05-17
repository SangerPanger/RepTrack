package com.example.fitnessapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fitnessapp.domain.model.Sex
import com.example.fitnessapp.domain.model.TrainingGoal

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1, // Only one profile
    val age: Int = 0,
    val sex: Sex = Sex.MALE,
    val heightCm: Double = 0.0,
    val bodyWeightKg: Double = 0.0,
    val trainingExperienceMonths: Int = 0,
    val detrainingWeeks: Int = 0,
    val isReturningLifter: Boolean = false,
    val plannedWeeklyWorkouts: Int = 3,
    val averageProteinGramsPerDay: Double = 0.0,
    val averageCaloriesPerDay: Double = 0.0,
    val estimatedTdee: Double = 0.0,
    val fatGramsPerDay: Double = 0.0,
    val fatPercentCalories: Double = 0.0,
    val goal: TrainingGoal = TrainingGoal.UNKNOWN,
    val useSettingsForNutrition: Boolean = true
)

fun UserProfileEntity.toDomainModel(): com.example.fitnessapp.domain.model.UserTrainingProfile {
    return com.example.fitnessapp.domain.model.UserTrainingProfile(
        age = this.age,
        sex = this.sex,
        heightCm = this.heightCm,
        bodyWeightKg = this.bodyWeightKg,
        trainingExperienceMonths = this.trainingExperienceMonths,
        detrainingWeeks = this.detrainingWeeks,
        isReturningLifter = this.isReturningLifter,
        plannedWeeklyWorkouts = this.plannedWeeklyWorkouts,
        averageProteinGramsPerDay = this.averageProteinGramsPerDay,
        averageCaloriesPerDay = this.averageCaloriesPerDay,
        estimatedTdee = this.estimatedTdee,
        fatGramsPerDay = this.fatGramsPerDay,
        fatPercentCalories = this.fatPercentCalories,
        goal = this.goal,
        useSettingsForNutrition = this.useSettingsForNutrition
    )
}

@Entity(tableName = "food_logs")
data class FoodLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long, // Timestamp
    val carbs: Double,
    val fats: Double,
    val protein: Double,
    val calories: Double
)
