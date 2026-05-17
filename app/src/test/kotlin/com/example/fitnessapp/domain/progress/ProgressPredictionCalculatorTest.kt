package com.example.fitnessapp.domain.progress

import com.example.fitnessapp.domain.model.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgressPredictionCalculatorTest {

    private val calculator = ProgressPredictionCalculator()

    @Test
    fun `newbie in surplus with sufficient protein`() {
        val profile = UserTrainingProfile(
            age = 25,
            sex = Sex.MALE,
            heightCm = 180.0,
            bodyWeightKg = 80.0,
            trainingExperienceMonths = 2,
            detrainingWeeks = 0,
            isReturningLifter = false,
            plannedWeeklyWorkouts = 3,
            averageProteinGramsPerDay = 160.0, // 2.0 g/kg
            averageCaloriesPerDay = 3000.0,
            estimatedTdee = 2500.0, // 20% surplus
            fatGramsPerDay = 80.0,
            fatPercentCalories = null,
            goal = TrainingGoal.BULK,
            useSettingsForNutrition = true
        )

        val result = calculator.calculatePrediction(
            exerciseName = "Bench Press",
            exerciseId = 1L,
            current1RM = 60.0,
            previousBest1RM = 60.0,
            profile = profile,
            recentWorkoutsPerWeek = 3.0
        )

        // NEWBIE base: 0.04
        // Calorie surplus (20%): 1.03 (based on formula: above 15% is 1.03, between 5-15% is 1.05)
        // Protein (2.0 >= 1.6): 1.00
        // Fat (80/80=1.0 >= 0.6): 1.00
        // Age (25): 1.00
        // Adherence (3/3): 1.00
        // Expected gain: 0.04 * 1.03 = 0.0412 (4.12%)
        
        assertEquals(4.1, result.strengthGainPercent4Weeks, 0.1)
        assertEquals(PotentialLabel.HIGH, result.hypertrophyPotentialLabel)
        assertEquals(PredictionConfidence.HIGH, result.predictionConfidence)
    }

    @Test
    fun `intermediate in deficit with sufficient protein`() {
        val profile = UserTrainingProfile(
            age = 30,
            sex = Sex.FEMALE,
            heightCm = 165.0,
            bodyWeightKg = 60.0,
            trainingExperienceMonths = 30,
            detrainingWeeks = 0,
            isReturningLifter = false,
            plannedWeeklyWorkouts = 4,
            averageProteinGramsPerDay = 120.0, // 2.0 g/kg
            averageCaloriesPerDay = 1800.0,
            estimatedTdee = 2200.0, // ~18% deficit
            fatGramsPerDay = 50.0,
            fatPercentCalories = null,
            goal = TrainingGoal.CUT,
            useSettingsForNutrition = true
        )

        val result = calculator.calculatePrediction(
            exerciseName = "Squat",
            exerciseId = 2L,
            current1RM = 80.0,
            previousBest1RM = 85.0,
            profile = profile,
            recentWorkoutsPerWeek = 4.0
        )

        // INTERMEDIATE base: 0.012
        // Calorie deficit (18%): 0.95
        // Protein (2.0 >= 1.6): 1.00
        // Fat (50/60=0.83 >= 0.6): 1.00
        // Age (30): 1.00
        // Adherence (4/4): 1.00
        // Expected gain: 0.012 * 0.95 = 0.0114 (1.14%)
        
        assertEquals(1.1, result.strengthGainPercent4Weeks, 0.1)
        assertTrue(result.explanation.contains("calorie deficit"))
    }

    @Test
    fun `low protein case`() {
        val profile = UserTrainingProfile(
            age = 20,
            sex = Sex.FEMALE,
            heightCm = 170.0,
            bodyWeightKg = 70.0,
            trainingExperienceMonths = 12, // NOVICE
            detrainingWeeks = 0,
            isReturningLifter = false,
            plannedWeeklyWorkouts = 3,
            averageProteinGramsPerDay = 40.0, // 0.57 g/kg (< 0.8)
            averageCaloriesPerDay = 2500.0,
            estimatedTdee = 2500.0,
            fatGramsPerDay = 60.0,
            fatPercentCalories = null,
            goal = TrainingGoal.MAINTAIN,
            useSettingsForNutrition = true
        )

        val result = calculator.calculatePrediction(
            exerciseName = "Deadlift",
            exerciseId = 3L,
            current1RM = 100.0,
            previousBest1RM = 100.0,
            profile = profile,
            recentWorkoutsPerWeek = 3.0
        )

        // NOVICE base: 0.025
        // Calories (0%): 1.00
        // Protein (0.57 < 0.8): 0.45
        // Fat: 1.00
        // Age: 1.00
        // Adherence: 1.00
        // Expected gain: 0.025 * 0.45 = 0.01125 (1.125%)
        
        assertEquals(1.1, result.strengthGainPercent4Weeks, 0.1)
        assertTrue(result.warnings.any { it.contains("Protein intake missing") || it.contains("protein") })
    }

    @Test
    fun `returning lifter below previous peak`() {
        val profile = UserTrainingProfile(
            age = 28,
            sex = Sex.MALE,
            heightCm = 185.0,
            bodyWeightKg = 90.0,
            trainingExperienceMonths = 36, // INTERMEDIATE
            detrainingWeeks = 12,
            isReturningLifter = true,
            plannedWeeklyWorkouts = 3,
            averageProteinGramsPerDay = 150.0,
            averageCaloriesPerDay = 3000.0,
            estimatedTdee = 2800.0,
            fatGramsPerDay = 80.0,
            fatPercentCalories = null,
            goal = TrainingGoal.LEAN_BULK,
            useSettingsForNutrition = true
        )

        val result = calculator.calculatePrediction(
            exerciseName = "Overhead Press",
            exerciseId = 4L,
            current1RM = 50.0,
            previousBest1RM = 70.0,
            profile = profile,
            recentWorkoutsPerWeek = 3.0
        )

        // Gap: 20kg
        // Regain Pct (12 weeks): 0.35
        // Calorie surplus (7%): 1.05
        // Protein (1.66 >= 1.6): 1.00
        // Adherence: 1.00
        // Regain amount: 20 * 0.35 * 1.05 * 1.00 * 1.00 = 7.35 kg
        
        // Normal gain base (RETURNING): 0.025
        // Normal gain: 50 * 0.025 * 1.05 * 1.00 * 1.00 * 1.00 * 1.00 = 1.3125 kg
        // newGainAmount = normal gain * 0.25 = 0.328125 kg
        
        // Total predicted: 50 + 7.35 + 0.328 = 57.678 kg
        // Gain: 7.678 / 50 = 15.356%
        
        assertEquals(57.5, result.predictedEstimated1RM4Weeks, 0.5)
        assertEquals(15.4, result.strengthGainPercent4Weeks, 0.1)
    }

    @Test
    fun `older user with sufficient vs insufficient protein`() {
        val baseProfile = UserTrainingProfile(
            age = 60,
            sex = Sex.FEMALE,
            heightCm = 160.0,
            bodyWeightKg = 65.0,
            trainingExperienceMonths = 120, // ADVANCED
            detrainingWeeks = 0,
            isReturningLifter = false,
            plannedWeeklyWorkouts = 3,
            averageProteinGramsPerDay = 120.0, // 1.84 g/kg (Sufficient for 50+)
            averageCaloriesPerDay = 2000.0,
            estimatedTdee = 2000.0,
            fatGramsPerDay = 50.0,
            fatPercentCalories = null,
            goal = TrainingGoal.MAINTAIN,
            useSettingsForNutrition = true
        )

        val resultSufficient = calculator.calculatePrediction(
            exerciseName = "Leg Press",
            exerciseId = 5L,
            current1RM = 120.0,
            previousBest1RM = 120.0,
            profile = baseProfile,
            recentWorkoutsPerWeek = 3.0
        )

        val profileInsufficient = baseProfile.copy(averageProteinGramsPerDay = 60.0) // 0.92 g/kg (< 1.0)
        val resultInsufficient = calculator.calculatePrediction(
            exerciseName = "Leg Press",
            exerciseId = 5L,
            current1RM = 120.0,
            previousBest1RM = 120.0,
            profile = profileInsufficient,
            recentWorkoutsPerWeek = 3.0
        )

        // Age 60 multiplier: 0.80
        // If protein sufficient and adherence high: 0.80 + 0.05 = 0.85
        // ADVANCED base: 0.005
        
        // Sufficient: 0.005 * 1.0 * 1.0 * 1.0 * 0.85 * 1.0 = 0.00425 (0.425%)
        // Insufficient: 0.005 * 1.0 * 0.45 * 1.0 * 0.80 * 1.0 = 0.0018 (0.18%)
        
        assertEquals(0.4, resultSufficient.strengthGainPercent4Weeks, 0.1)
        assertEquals(0.2, resultInsufficient.strengthGainPercent4Weeks, 0.1)
        assertTrue(resultSufficient.strengthGainPercent4Weeks > resultInsufficient.strengthGainPercent4Weeks)
    }
}
