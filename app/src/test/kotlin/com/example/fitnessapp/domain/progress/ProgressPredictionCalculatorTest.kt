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
        assertTrue("Expected HIGH or GOOD for newbie in surplus, got ${result.hypertrophyPotentialLabel}", result.hypertrophyPotentialLabel == PotentialLabel.HIGH || result.hypertrophyPotentialLabel == PotentialLabel.GOOD)
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
        // Calorie deficit (18%, -400kcal): 0.95
        // Required protein at -400kcal: 2.6 + (150/250)*0.4? No, -400 is <= -250 so it is 2.6.
        // User protein: 120/60 = 2.0. Ratio: 2.0/2.6 = 0.77. Mult: 0.65
        // Fat (50/60=0.83 >= 0.6): 1.00
        // Age (30): 1.00
        // Adherence (4.0 / 3.0 = 1.33 -> capped at 1.1): 1.10
        // Expected gain: 0.012 * 0.95 * 0.65 * 1.10 = 0.008151 (0.815%)
        
        assertEquals(0.8, result.strengthGainPercent4Weeks, 0.1)
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
        // Calorie surplus (200kcal): 1.05
        // Required protein at 200kcal surplus: Interpolate 0(2.2) to 250(1.6). 2.2 - (200/250)*0.6 = 2.2 - 0.48 = 1.72
        // User protein: 150/90 = 1.666. Ratio: 1.666/1.72 = 0.96. Mult: 0.85
        // Adherence: 1.00
        // Regain amount: 20 * 0.35 * 1.05 * 0.85 * 1.00 = 6.2475 kg
        
        // Normal gain base (RETURNING): 0.025
        // Normal gain: 50 * 0.025 * 1.05 * 0.85 * 1.00 * 1.00 * 1.00 * 1.00 = 1.1156 kg
        // newGainAmount = normal gain * 0.25 = 0.2789 kg
        
        // Total predicted before doubling: 50 + 6.2475 + 0.2789 = 56.5264 kg
        // Gain percentage before doubling: 6.5264 / 50 = 13.0528%
        // Doubled gain percentage: 13.0528% * 2 = 26.1056% -> rounded 26.1%
        // Final predicted 1RM: 50 * (1 + 0.261056) = 63.0528 kg -> rounded 63.0
        
        assertEquals(63.0, result.predictedEstimated1RM4Weeks, 0.5)
        assertEquals(26.1, result.strengthGainPercent4Weeks, 0.1)
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

        // Sufficient:
        // Calories at maintenance (0kcal): required protein 2.2
        // User protein: 120/65 = 1.846. Ratio: 1.846/2.2 = 0.839. Mult: 0.85
        // Age 60 multiplier: 0.80
        // Bonus condition: nutritionStatus.proteinMultiplier >= 1.0 (False, it is 0.85)
        // Age mult stays 0.80.
        // ADVANCED base: 0.005
        // gain: 0.005 * 1.0 * 0.85 * 1.0 * 0.80 * 1.0 = 0.0034 (0.34%)
        
        // Insufficient:
        // User protein: 60/65 = 0.923. Ratio: 0.923/2.2 = 0.419. Mult: 0.45
        // gain: 0.005 * 1.0 * 0.45 * 1.0 * 0.80 * 1.0 = 0.0018 (0.18%)
        
        assertEquals(0.3, resultSufficient.strengthGainPercent4Weeks, 0.1)
        assertEquals(0.2, resultInsufficient.strengthGainPercent4Weeks, 0.1)
        assertTrue(resultSufficient.strengthGainPercent4Weeks > resultInsufficient.strengthGainPercent4Weeks)
    }

    @Test
    fun `prediction with historical progress bonus`() {
        val profile = UserTrainingProfile(
            age = 25,
            sex = Sex.MALE,
            heightCm = 180.0,
            bodyWeightKg = 80.0,
            trainingExperienceMonths = 24, // INTERMEDIATE
            detrainingWeeks = 0,
            isReturningLifter = false,
            averageProteinGramsPerDay = 160.0,
            averageCaloriesPerDay = 2500.0,
            estimatedTdee = 2500.0,
            fatGramsPerDay = 80.0,
            fatPercentCalories = null,
            goal = TrainingGoal.MAINTAIN,
            useSettingsForNutrition = true
        )

        // Without history
        val resultNoHistory = calculator.calculatePrediction(
            exerciseName = "Bench Press",
            exerciseId = 1L,
            current1RM = 100.0,
            previousBest1RM = 100.0,
            profile = profile,
            recentWorkoutsPerWeek = 3.0
        )

        // With history: 4 workouts, average increase of 10% total over 3 intervals? 
        // Let's say: 100, 102, 104.04, 106.12 (2% each step)
        val history = listOf(100.0, 102.0, 104.0, 106.0, 108.0) 
        // Increases: 2%, 1.96%, 1.92%, 1.85% -> avg approx 1.93%
        // Half of it (approx 0.96%) should be added to base
        
        val resultWithHistory = calculator.calculatePrediction(
            exerciseName = "Bench Press",
            exerciseId = 1L,
            current1RM = 108.0,
            previousBest1RM = 108.0,
            profile = profile,
            recentWorkoutsPerWeek = 3.0,
            recent1RMHistory = history
        )

        assertTrue("Predicted gain with history (${resultWithHistory.strengthGainPercent4Weeks}%) should be significantly higher than without history (${resultNoHistory.strengthGainPercent4Weeks}%)",
            resultWithHistory.strengthGainPercent4Weeks > resultNoHistory.strengthGainPercent4Weeks + 0.5)
    }
    @Test
    fun `advanced lifter with optimized nutrition should have high potential label`() {
        val profile = UserTrainingProfile(
            age = 25,
            sex = Sex.MALE,
            heightCm = 180.0,
            bodyWeightKg = 80.0,
            trainingExperienceMonths = 60,
            detrainingWeeks = 0,
            isReturningLifter = false,
            averageProteinGramsPerDay = 160.0,
            averageCaloriesPerDay = 3000.0,
            estimatedTdee = 2500.0,
            fatGramsPerDay = 80.0,
            fatPercentCalories = null,
            goal = TrainingGoal.BULK,
            useSettingsForNutrition = true
        )
        val result = calculator.calculatePrediction(
            exerciseName = "Squat",
            exerciseId = 1L,
            current1RM = 180.0,
            previousBest1RM = 180.0,
            profile = profile,
            recentWorkoutsPerWeek = 4.0
        )
        assertTrue("Expected GOOD or HIGH potential, got ${result.hypertrophyPotentialLabel}", result.hypertrophyPotentialLabel == PotentialLabel.HIGH)
    }
    @Test
    fun `user specific scenario from issue`() {
        val profile = UserTrainingProfile(
            age = 33,
            sex = Sex.MALE,
            heightCm = 180.0,
            bodyWeightKg = 72.0,
            trainingExperienceMonths = 12,
            detrainingWeeks = 0,
            isReturningLifter = false,
            averageProteinGramsPerDay = 170.0,
            averageCaloriesPerDay = 2900.0,
            estimatedTdee = 2300.0,
            fatGramsPerDay = 178.0,
            fatPercentCalories = null,
            goal = TrainingGoal.BULK,
            useSettingsForNutrition = true
        )
        val result = calculator.calculatePrediction(
            exerciseName = "Bench Press",
            exerciseId = 1L,
            current1RM = 100.0,
            previousBest1RM = 100.0,
            profile = profile,
            recentWorkoutsPerWeek = 3.5
        )
        assertEquals(PotentialLabel.HIGH, result.hypertrophyPotentialLabel)
    }

    @Test
    fun `verify optimized and missing factors identification`() {
        val profile = UserTrainingProfile(
            age = 30,
            sex = Sex.MALE,
            heightCm = 180.0,
            bodyWeightKg = 72.0,
            trainingExperienceMonths = 12,
            detrainingWeeks = 0,
            isReturningLifter = false,
            averageProteinGramsPerDay = 170.0, // High protein
            averageCaloriesPerDay = 2900.0, // Surplus
            estimatedTdee = 2300.0,
            fatGramsPerDay = 80.0,
            fatPercentCalories = null,
            goal = TrainingGoal.BULK,
            useSettingsForNutrition = true
        )

        val result = calculator.calculatePrediction(
            exerciseName = "Bench Press",
            exerciseId = 1L,
            current1RM = 100.0,
            previousBest1RM = 100.0,
            profile = profile,
            recentWorkoutsPerWeek = 4.0 // High adherence
        )

        assertTrue("Protein should be optimized", result.optimizedFactors.contains("Protein"))
        assertTrue("Calories should be optimized", result.optimizedFactors.contains("Calories"))
        assertTrue("Consistency should be optimized", result.optimizedFactors.contains("Consistency"))
        
        // Test with missing factors
        val lowAdherenceProfile = profile.copy(averageProteinGramsPerDay = 50.0) // Low protein
        val result2 = calculator.calculatePrediction(
            exerciseName = "Bench Press",
            exerciseId = 1L,
            current1RM = 100.0,
            previousBest1RM = 100.0,
            profile = lowAdherenceProfile,
            recentWorkoutsPerWeek = 1.0 // Low adherence
        )
        
        assertTrue("Protein should be missing", result2.missingFactors.contains("Protein"))
        assertTrue("Consistency should be missing", result2.missingFactors.contains("Consistency"))
        assertTrue("Calories should still be optimized", result2.optimizedFactors.contains("Calories"))
    }
    @Test
    fun `standard optimized user should have high potential label`() {
        val profile = UserTrainingProfile(
            age = 30,
            sex = Sex.MALE,
            heightCm = 180.0,
            bodyWeightKg = 80.0,
            trainingExperienceMonths = 24,
            detrainingWeeks = 0,
            isReturningLifter = false,
            averageProteinGramsPerDay = 176.0, // 2.2 g/kg
            averageCaloriesPerDay = 2750.0, // 10% surplus
            estimatedTdee = 2500.0,
            fatGramsPerDay = 80.0,
            fatPercentCalories = null,
            goal = TrainingGoal.BULK,
            useSettingsForNutrition = true
        )
        val result = calculator.calculatePrediction(
            exerciseName = "Bench Press",
            exerciseId = 1L,
current1RM = 100.0,
previousBest1RM = 100.0,
profile = profile,
recentWorkoutsPerWeek = 3.0, // Standard optimized consistency
weeklySets = 10.0 // Standard optimized volume
)
assertTrue("All factors should be optimized", result.optimizedFactors.contains("Protein") && result.optimizedFactors.contains("Calories") && result.optimizedFactors.contains("Consistency") && result.optimizedFactors.contains("Volume"))
assertEquals(PotentialLabel.HIGH, result.hypertrophyPotentialLabel)
    }
}
