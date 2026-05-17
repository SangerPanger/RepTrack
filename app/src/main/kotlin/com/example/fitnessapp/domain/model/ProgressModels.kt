package com.example.fitnessapp.domain.model

data class UserTrainingProfile(
    val age: Int?,
    val sex: Sex?,
    val heightCm: Double?,
    val bodyWeightKg: Double?,
    val trainingExperienceMonths: Int,
    val detrainingWeeks: Int?,
    val isReturningLifter: Boolean,
    val averageProteinGramsPerDay: Double?,
    val averageCaloriesPerDay: Double?,
    val estimatedTdee: Double?,
    val fatGramsPerDay: Double?,
    val fatPercentCalories: Double?,
    val goal: TrainingGoal,
    val useSettingsForNutrition: Boolean
)

data class ProgressPredictionResult(
    val exerciseId: Long?,
    val exerciseName: String,
    val currentEstimated1RM: Double,
    val predictedEstimated1RM4Weeks: Double,
    val strengthGainPercent4Weeks: Double,
    val hypertrophyPotentialScore: Double,
    val hypertrophyPotentialLabel: PotentialLabel,
    val predictionConfidence: PredictionConfidence,
    val explanation: String,
    val warnings: List<String>,
    val optimizedFactors: List<String> = emptyList(),
    val missingFactors: List<String> = emptyList(), val isVolumeOptimized: Boolean? = null
)

data class NutritionStatus(
    val calorieBalancePercent: Double?,
    val proteinGPerKg: Double?,
    val fatGPerKg: Double?,
    val calorieStrengthMultiplier: Double,
    val calorieHypertrophyMultiplier: Double,
    val proteinMultiplier: Double,
    val fatHormoneSupportMultiplier: Double
)
