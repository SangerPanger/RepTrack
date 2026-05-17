package com.example.fitnessapp.domain.progress

import com.example.fitnessapp.domain.model.*
import kotlin.math.max
import kotlin.math.min

class ProgressPredictionCalculator {

    fun calculateEstimated1RM(weight: Double, reps: Int): Double {
        if (reps == 0) return 0.0
        return weight * (1 + reps / 30.0)
    }

    fun determineTrainingStatus(profile: UserTrainingProfile): TrainingStatus {
        if (profile.isReturningLifter && (profile.detrainingWeeks ?: 0) >= 4) {
            return TrainingStatus.RETURNING
        }
        return when (profile.trainingExperienceMonths) {
            in 0..6 -> TrainingStatus.NEWBIE
            in 7..18 -> TrainingStatus.NOVICE
            in 19..48 -> TrainingStatus.INTERMEDIATE
            else -> TrainingStatus.ADVANCED
        }
    }

    fun calculateNutritionStatus(profile: UserTrainingProfile, warnings: MutableList<String>): NutritionStatus {
        val tdee = profile.estimatedTdee ?: 2500.0 // Default or handle missing
        val calories = profile.averageCaloriesPerDay
        val bodyWeight = profile.bodyWeightKg ?: 70.0 // Default or handle missing

        val calorieBalancePercent = if (calories != null && tdee > 0) {
            (calories - tdee) / tdee
        } else {
            null
        }

        val calorieBalanceKcal = if (calories != null && tdee > 0) {
            calories - tdee
        } else {
            null
        }

        val proteinGPerKg = if (profile.averageProteinGramsPerDay != null && bodyWeight > 0) {
            profile.averageProteinGramsPerDay / bodyWeight
        } else {
            null
        }

        val fatGPerKg = if (profile.fatGramsPerDay != null && bodyWeight > 0) {
            profile.fatGramsPerDay / bodyWeight
        } else if (profile.fatPercentCalories != null && calories != null) {
            (calories * (profile.fatPercentCalories / 100.0)) / 9.0 / bodyWeight
        } else {
            null
        }

        val calorieStrengthMult = when {
            calorieBalancePercent == null -> 1.0
            calorieBalancePercent < -0.20 -> 0.85
            calorieBalancePercent < -0.10 -> 0.95
            calorieBalancePercent < 0.05 -> 1.00
            calorieBalancePercent <= 0.15 -> 1.05
            else -> 1.03
        }

        val calorieHypertrophyMult = when {
            calorieBalancePercent == null -> 1.0
            calorieBalancePercent < -0.20 -> 0.60
            calorieBalancePercent < -0.10 -> 0.75
            calorieBalancePercent < 0.05 -> 1.00
            calorieBalancePercent <= 0.15 -> 1.10
            else -> 1.12
        }

        if (calorieBalancePercent != null && calorieBalancePercent < -0.20) {
            warnings.add("Aggressive calorie deficit may limit progress.")
        }

        val age = profile.age ?: 30
        
        // Dynamic protein requirement based on calorie balance
        // Requirement: 
        // if 250 calorie suprlus than 1.6g protein per kg needed,
        // if at maintain than 2.2g per kg needed,
        // if 250 calorie deficit than 2.6g protein per kg needed,
        val requiredProtein = when {
            calorieBalanceKcal == null -> if (age >= 50) 1.8 else 1.6
            calorieBalanceKcal >= 250 -> 1.6
            calorieBalanceKcal <= -250 -> 2.6
            calorieBalanceKcal >= 0 -> {
                // Interpolate between 0 (2.2) and 250 (1.6)
                2.2 - (calorieBalanceKcal / 250.0) * (2.2 - 1.6)
            }
            else -> {
                // Interpolate between -250 (2.6) and 0 (2.2)
                2.2 + (Math.abs(calorieBalanceKcal) / 250.0) * (2.6 - 2.2)
            }
        }

        val proteinMult = if (proteinGPerKg == null) {
            warnings.add("Protein intake missing; prediction confidence reduced.")
            0.90
        } else {
            if (proteinGPerKg < requiredProtein * 0.6) {
                warnings.add("Low protein intake; progress may be limited.")
            }
            
            // Adjust multiplier based on how close user is to the required protein
            val proteinRatio = proteinGPerKg / requiredProtein
            when {
                proteinRatio >= 1.0 -> 1.00
                proteinRatio >= 0.8 -> 0.85
                proteinRatio >= 0.6 -> 0.65
                else -> 0.45
            }
        }

        val fatMult = if (fatGPerKg == null) {
            warnings.add("Fat intake missing; hormonal support estimate uncertain.")
            0.97
        } else {
            // fat >= 20% calories OR fat >= 0.6 g/kg/day
            // Note: I only have fatGPerKg here, but I can check that.
            // If I had fatPercentCalories, I'd check that too.
            val fatPct = if (calories != null && calories > 0 && profile.fatGramsPerDay != null) (profile.fatGramsPerDay * 9 / calories) else (profile.fatPercentCalories ?: 0.0) / 100.0
            
            if (fatPct >= 0.20 || fatGPerKg >= 0.6) 1.00
            else if (fatPct >= 0.15 || fatGPerKg >= 0.45) 0.95
            else 0.85
        }

        return NutritionStatus(
            calorieBalancePercent = calorieBalancePercent,
            proteinGPerKg = proteinGPerKg,
            fatGPerKg = fatGPerKg,
            calorieStrengthMultiplier = calorieStrengthMult,
            calorieHypertrophyMultiplier = calorieHypertrophyMult,
            proteinMultiplier = proteinMult,
            fatHormoneSupportMultiplier = fatMult
        )
    }

    fun calculatePrediction(
        exerciseName: String,
        exerciseId: Long?,
        current1RM: Double,
        previousBest1RM: Double?,
        profile: UserTrainingProfile,
        recentWorkoutsPerWeek: Double?, weeklySets: Double? = null,
        recent1RMHistory: List<Double> = emptyList()
    ): ProgressPredictionResult {
        val warnings = mutableListOf<String>()
        val nutritionStatus = calculateNutritionStatus(profile, warnings)
        val status = determineTrainingStatus(profile)

        val baseStrengthPct = when (status) {
            TrainingStatus.NEWBIE -> 0.04
            TrainingStatus.NOVICE -> 0.025
            TrainingStatus.INTERMEDIATE -> 0.012
            TrainingStatus.ADVANCED -> 0.005
            TrainingStatus.RETURNING -> 0.025 // Default for returning if not using specific formula
        }

        var ageMultiplier = when (profile.age) {
            null -> 0.95
            in 18..35 -> 1.00
            in 36..49 -> 0.92
            in 50..64 -> 0.80
            else -> 0.65
        }

        // Adherence
        // User requirement: "utilized the avarage workout per week based on last 4 weeks"
        // We use 3.0 as a baseline for "standard" adherence.
        val baselineWorkouts = 3.0
        val actualWorkouts = recentWorkoutsPerWeek ?: 1.0 // If unknown, assume low for caution
        val adherenceMultiplier = (actualWorkouts / baselineWorkouts).coerceIn(0.4, 1.1)
        
        if (recentWorkoutsPerWeek == null) {
            // Confidence will be reduced later
        }

        // Age bonus
        if ((profile.age ?: 0) >= 50 && nutritionStatus.proteinMultiplier >= 1.0 && adherenceMultiplier >= 0.9) {
            ageMultiplier = Math.min(1.0, ageMultiplier + 0.05)
        }

        var normalStrengthGainPct4w = baseStrengthPct *
                nutritionStatus.calorieStrengthMultiplier *
                nutritionStatus.proteinMultiplier *
                nutritionStatus.fatHormoneSupportMultiplier *
                ageMultiplier *
                adherenceMultiplier

        // Adjust based on recent performance history
        if (recent1RMHistory.size >= 2) {
            val last4 = recent1RMHistory.takeLast(5) // Need up to 5 points to get 4 intervals
            if (last4.size >= 2) {
                val increases = mutableListOf<Double>()
                for (i in 1 until last4.size) {
                    val prev = last4[i-1]
                    if (prev > 0) {
                        increases.add((last4[i] - prev) / prev)
                    }
                }
                if (increases.isNotEmpty()) {
                    val avgRecentIncrease = increases.average()
                    if (avgRecentIncrease > 0) {
                        // User requirement: "divide average increase by half and add it untop of the current system"
                        normalStrengthGainPct4w += (avgRecentIncrease / 2.0)
                    }
                }
            }
        }

        var predicted1RM4Weeks: Double
        if (status == TrainingStatus.RETURNING && previousBest1RM != null && previousBest1RM > current1RM) {
            val gapToPreviousPeak = previousBest1RM - current1RM
            val detrainingWeeks = profile.detrainingWeeks ?: 4
            val regainPctOfGap4w = when {
                detrainingWeeks < 4 -> 0.15
                detrainingWeeks <= 12 -> 0.35
                detrainingWeeks <= 52 -> 0.45
                else -> 0.30
            }

            val regainAmount = gapToPreviousPeak *
                    regainPctOfGap4w *
                    nutritionStatus.calorieStrengthMultiplier *
                    nutritionStatus.proteinMultiplier *
                    adherenceMultiplier

            val newGainAmount = current1RM * normalStrengthGainPct4w * 0.25
            
            predicted1RM4Weeks = current1RM + regainAmount + newGainAmount
            
            // Cap
            val cap = previousBest1RM * 1.02
            if (predicted1RM4Weeks > cap) {
                predicted1RM4Weeks = cap
            }
        } else {
            predicted1RM4Weeks = current1RM * (1.0 + normalStrengthGainPct4w)
        }

        // Hypertrophy
        val trainingStatusHypertrophyMult = when (status) {
            TrainingStatus.NEWBIE -> 1.30
            TrainingStatus.NOVICE -> 1.00
            TrainingStatus.INTERMEDIATE -> 0.55
            TrainingStatus.ADVANCED -> 0.25
            TrainingStatus.RETURNING -> 1.10
        }

        val volumeMultiplier = if (weeklySets != null) { when { weeklySets >= 10.0 -> 1.05; weeklySets >= 7.0 -> 1.00; else -> 0.80 } } else 1.0; val hypertrophyScore = trainingStatusHypertrophyMult * volumeMultiplier *
                nutritionStatus.calorieHypertrophyMultiplier *
                nutritionStatus.proteinMultiplier *
                nutritionStatus.fatHormoneSupportMultiplier *
                ageMultiplier *
                adherenceMultiplier

                val optimizedNutritionMult = 1.10 * 1.0 * 1.0 * 1.0 * 1.0 * 1.05
        val normalizedScore = hypertrophyScore / (trainingStatusHypertrophyMult * optimizedNutritionMult)
        val hypertrophyLabel = when {
            normalizedScore < 0.50 -> PotentialLabel.LOW
            normalizedScore < 0.80 -> PotentialLabel.MODERATE
            normalizedScore < 0.95 -> PotentialLabel.GOOD
            else -> PotentialLabel.HIGH
        }

        // Confidence
        var confidenceScore = 0
        if (profile.bodyWeightKg != null) confidenceScore++
        if (profile.averageProteinGramsPerDay != null) confidenceScore++
        if (profile.averageCaloriesPerDay != null && profile.estimatedTdee != null) confidenceScore++
        if (recentWorkoutsPerWeek != null) confidenceScore++
        // We'll check "4 weeks of data" in the ViewModel and pass it or adjust here
        
        val confidence = when {
            confidenceScore >= 4 -> PredictionConfidence.HIGH
            confidenceScore >= 2 -> PredictionConfidence.MEDIUM
            else -> PredictionConfidence.LOW
        }

        if (recentWorkoutsPerWeek == null) {
            warnings.add("Low training history; using default adherence baseline.")
        }

        var strengthGainPercent = if (current1RM > 0) (predicted1RM4Weeks - current1RM) / current1RM * 100 else 0.0

        if (profile.isReturningLifter) {
            strengthGainPercent *= 2.0
            // Also update the predicted 1RM if we doubled the gain percentage
            predicted1RM4Weeks = current1RM * (1.0 + strengthGainPercent / 100.0)
        }

        val roundedPredicted1RM = Math.round(predicted1RM4Weeks * 2) / 2.0
        val roundedPercent = Math.round(strengthGainPercent * 10) / 10.0

        val optimizedFactors = mutableListOf<String>()
        val missingFactors = mutableListOf<String>()

        if (nutritionStatus.proteinMultiplier >= 0.95) {
            optimizedFactors.add("Protein")
        } else {
            missingFactors.add("Protein")
        }

        if (nutritionStatus.calorieHypertrophyMultiplier >= 1.05) {
            optimizedFactors.add("Calories")
        } else {
            missingFactors.add("Calories")
        }

        if (adherenceMultiplier >= 0.95) {
            optimizedFactors.add("Consistency")
        } else {
            missingFactors.add("Consistency")
        }

        // For now, we don't have enough data for Volume and Sleep in the calculator, 
        // so we won't add them to optimizedFactors yet to be honest.
        if (weeklySets != null) { if (weeklySets >= 10.0) optimizedFactors.add("Volume") else if (weeklySets < 7.0) missingFactors.add("Volume") }

        val explanation = buildExplanation(status, nutritionStatus, hypertrophyLabel)

        return ProgressPredictionResult(
            exerciseId = exerciseId,
            exerciseName = exerciseName,
            currentEstimated1RM = current1RM,
            predictedEstimated1RM4Weeks = roundedPredicted1RM,
            strengthGainPercent4Weeks = roundedPercent,
            hypertrophyPotentialScore = hypertrophyScore,
            hypertrophyPotentialLabel = hypertrophyLabel,
            predictionConfidence = confidence,
            explanation = explanation,
            warnings = warnings,
            optimizedFactors = optimizedFactors,
            missingFactors = missingFactors, isVolumeOptimized = if (weeklySets != null) weeklySets >= 10.0 else null
        )
    }

    private fun buildExplanation(status: TrainingStatus, nutrition: NutritionStatus, hypertrophyLabel: PotentialLabel): String {
        val sb = StringBuilder()
        
        if (nutrition.calorieBalancePercent != null) {
            if (nutrition.calorieBalancePercent > 0.05) {
                sb.append("Because you are in a surplus with ")
                if (nutrition.proteinMultiplier >= 1.0) {
                    sb.append("sufficient protein, your hypertrophy potential is higher.")
                } else {
                    sb.append("insufficient protein, your hypertrophy potential is limited.")
                }
            } else if (nutrition.calorieBalancePercent < -0.05) {
                sb.append("You are in a calorie deficit. Maintaining strength can still be a strong result.")
            } else {
                sb.append("You are at maintenance calories.")
            }
        }
        
        if (status == TrainingStatus.NEWBIE) {
            sb.append(" As a new lifter, you have high potential for rapid gains.")
        } else if (status == TrainingStatus.RETURNING) {
            sb.append(" You are in a 'regain' phase, which often allows for faster progress.")
        }
        
        return sb.toString().trim()
    }
}
