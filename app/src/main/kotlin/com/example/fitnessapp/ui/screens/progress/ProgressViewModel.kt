package com.example.fitnessapp.ui.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.local.entity.ExerciseEntity
import com.example.fitnessapp.data.local.entity.FoodLogEntity
import com.example.fitnessapp.data.local.entity.SetEntity
import com.example.fitnessapp.data.local.entity.UserProfileEntity
import com.example.fitnessapp.data.repository.ExerciseRepository
import com.example.fitnessapp.data.repository.FoodRepository
import com.example.fitnessapp.data.repository.ProgressRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

data class ProgressPoint(val date: Long, val value: Double)

enum class ProjectionConfidence {
    LOW,
    MEDIUM,
    HIGH
}

data class ProgressProjection(
    val exerciseId: Long,
    val currentEstimated1RM: Double,
    val predictedEstimated1RMIn4Weeks: Double,
    val estimated1RMChangePercent: Double,
    val currentWeeklyVolume: Double,
    val predictedWeeklyVolumeIn4Weeks: Double,
    val volumeChangePercent: Double,
    val confidence: ProjectionConfidence,
    val reason: String
)

data class ExerciseProgress(
    val exercise: ExerciseEntity,
    val sets: List<SetEntity>,
    val totalVolume: Double,
    val estimated1RM: Double,
    val historyPoints: List<ProgressPoint> = emptyList(),
    val projection: ProgressProjection? = null
)

data class FoodProgress(
    val estimatedWeightChange: Double, // in kg
    val averageDailyCalories: Double,
    val tdee: Double,
    val totalDaysTracked: Int
)

class ProgressViewModel(
    private val progressRepository: ProgressRepository,
    private val exerciseRepository: ExerciseRepository,
    private val foodRepository: FoodRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProgressUiState>(ProgressUiState.Loading)
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    val userProfile: StateFlow<UserProfileEntity?> = foodRepository.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val foodLogs: StateFlow<List<FoodLogEntity>> = foodRepository.getAllFoodLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val foodProgress: StateFlow<FoodProgress?> = combine(userProfile, foodLogs) { profile, logs ->
        if (profile == null || logs.isEmpty()) return@combine null

        val bmr = if (profile.gender.lowercase().startsWith("m")) {
            10 * profile.currentWeight + 6.25 * profile.height - 5 * profile.age + 5
        } else {
            10 * profile.currentWeight + 6.25 * profile.height - 5 * profile.age - 161
        }
        val tdee = bmr * 1.2 // Assume sedentary

        val dailyTotals = logs.groupBy {
            val cal = Calendar.getInstance()
            cal.timeInMillis = it.date
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            cal.timeInMillis
        }.mapValues { (_, dayLogs) ->
            dayLogs.sumOf { it.calories }
        }

        val totalCalories = dailyTotals.values.sum()
        val daysTracked = dailyTotals.size
        val avgCalories = if (daysTracked > 0) totalCalories / daysTracked else 0.0
        
        // Estimated weight change (kg) = (Total Calories - (TDEE * Days)) / 7700
        val weightChange = (totalCalories - (tdee * daysTracked)) / 7700.0

        FoodProgress(
            estimatedWeightChange = weightChange,
            averageDailyCalories = avgCalories,
            tdee = tdee,
            totalDaysTracked = daysTracked
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        loadProgress()
    }

    private fun loadProgress() {
        viewModelScope.launch {
            exerciseRepository.allExercises.collectLatest { exercises ->
                if (exercises.isEmpty()) {
                    _uiState.value = ProgressUiState.Empty
                    return@collectLatest
                }

                val progressMap = mutableMapOf<Long, ExerciseProgress>()

                exercises.forEach { exercise ->
                    launch {
                        progressRepository.getExerciseProgressWithDate(exercise.id).collect { setsWithDate ->
                            if (setsWithDate.isNotEmpty()) {
                                val points = setsWithDate.groupBy { it.startedAt }
                                    .map { (date, sets) ->
                                        ProgressPoint(
                                            date = date,
                                            value = sets.maxOf { 
                                                progressRepository.calculateEstimated1RM(
                                                    it.setEntity.reps, 
                                                    it.setEntity.weight, 
                                                    it.setEntity.isDrop, 
                                                    it.startingWeight
                                                ) 
                                            }
                                        )
                                    }
                                    .sortedBy { it.date }
                                
                                val volume = setsWithDate.sumOf { 
                                    progressRepository.calculateVolume(
                                        it.setEntity.reps, 
                                        it.setEntity.weight, 
                                        it.setEntity.isDrop, 
                                        it.startingWeight
                                    ) 
                                }
                                val currentMax1RM = if (points.isNotEmpty()) points.last().value else 0.0
                                val projection = calculateProgressProjection(exercise.id, setsWithDate)
                                
                                val exerciseProgress = ExerciseProgress(
                                    exercise = exercise,
                                    sets = setsWithDate.map { it.setEntity },
                                    totalVolume = volume,
                                    estimated1RM = currentMax1RM,
                                    historyPoints = points,
                                    projection = projection
                                )

                                synchronized(progressMap) {
                                    progressMap[exercise.id] = exerciseProgress
                                    _uiState.value = ProgressUiState.Success(progressMap.values.toList().sortedBy { it.exercise.name })
                                }
                            } else {
                                synchronized(progressMap) {
                                    if (progressMap.isEmpty() && _uiState.value is ProgressUiState.Loading) {
                                         // Keep loading or set empty if all are checked and empty
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun calculateProgressProjection(
        exerciseId: Long,
        allSets: List<com.example.fitnessapp.data.local.entity.SetWithDate>
    ): ProgressProjection? {
        val completedSets = allSets.filter { it.setEntity.completed && it.setEntity.reps > 0 && it.setEntity.weight > 0 }
        if (completedSets.isEmpty()) return null

        // 1. Calculate Daily Stats
        val dailyStats = completedSets.groupBy { 
            val cal = Calendar.getInstance()
            cal.timeInMillis = it.startedAt
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            cal.timeInMillis
        }.mapValues { (_, sets) ->
            val dailyBest1RM = sets.filter { it.setEntity.reps <= 12 }
                .map { 
                    progressRepository.calculateEstimated1RM(
                        it.setEntity.reps, 
                        it.setEntity.weight, 
                        it.setEntity.isDrop, 
                        it.startingWeight
                    ) 
                }
                .maxOrNull() ?: 0.0
            val dailyVolume = sets.sumOf { 
                progressRepository.calculateVolume(
                    it.setEntity.reps, 
                    it.setEntity.weight, 
                    it.setEntity.isDrop, 
                    it.startingWeight
                )
            }
            Pair(dailyBest1RM, dailyVolume)
        }.toSortedMap()

        if (dailyStats.size < 3) return null

        // 2. Weekly Aggregation
        val weeklyStats = dailyStats.entries.groupBy { (date, _) ->
            val cal = Calendar.getInstance()
            cal.timeInMillis = date
            cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
            cal.timeInMillis
        }.mapValues { (_, entries) ->
            val best1RM = entries.maxOf { it.value.first }
            val totalVolume = entries.sumOf { it.value.second }
            val count = entries.size
            Triple(best1RM, totalVolume, count)
        }.toSortedMap()

        // 3. Use recent data window (last 8 weeks)
        val recentWeeks = weeklyStats.values.toList().takeLast(8)
        if (recentWeeks.size < 3) return null

        // 4. Outlier handling for 1RM
        val filtered1RMData = if (recentWeeks.size >= 5) {
            val valid1RMs = recentWeeks.map { it.first }.filter { it > 0 }
            if (valid1RMs.isNotEmpty()) {
                val median = calculateMedian(valid1RMs)
                recentWeeks.filter { it.first == 0.0 || (it.first >= median * 0.75 && it.first <= median * 1.25) }
            } else recentWeeks
        } else recentWeeks

        if (filtered1RMData.size < 3) return null

        // 5. Regression for 1RM and Volume
        val latestWeekIndex = recentWeeks.size - 1
        val consistency = weeklyStats.size.toDouble() / 8.0 // Simplified consistency over last 8 possible weeks
        // Actually, requirement says: number of weeks with at least one workout / number of weeks in recent window
        val actualConsistency = recentWeeks.count { it.third > 0 }.toDouble() / recentWeeks.size

        val proj1RM = calculateWeightedRegression(filtered1RMData.map { it.first }, actualConsistency)
        val projVolume = calculateWeightedRegression(recentWeeks.map { it.second }, actualConsistency)

        val current1RM = recentWeeks.last { it.first > 0 }.first
        val currentVolume = recentWeeks.last().second

        val pred1RM = proj1RM.coerceIn(current1RM * 0.85, current1RM * 1.12)
        val predVolume = projVolume.coerceIn(currentVolume * 0.70, currentVolume * 1.25)

        val confidence = when {
            recentWeeks.size >= 6 && actualConsistency >= 0.75 -> ProjectionConfidence.HIGH
            recentWeeks.size >= 4 && actualConsistency >= 0.5 -> ProjectionConfidence.MEDIUM
            else -> ProjectionConfidence.LOW
        }

        return ProgressProjection(
            exerciseId = exerciseId,
            currentEstimated1RM = current1RM,
            predictedEstimated1RMIn4Weeks = pred1RM,
            estimated1RMChangePercent = if (current1RM > 0) ((pred1RM - current1RM) / current1RM) * 100.0 else 0.0,
            currentWeeklyVolume = currentVolume,
            predictedWeeklyVolumeIn4Weeks = predVolume,
            volumeChangePercent = if (currentVolume > 0) ((predVolume - currentVolume) / currentVolume) * 100.0 else 0.0,
            confidence = confidence,
            reason = "Based on ${recentWeeks.size} weeks of data."
        )
    }

    private fun calculateMedian(values: List<Double>): Double {
        if (values.isEmpty()) return 0.0
        val sorted = values.sorted()
        return if (sorted.size % 2 == 0) {
            (sorted[sorted.size / 2 - 1] + sorted[sorted.size / 2]) / 2.0
        } else {
            sorted[sorted.size / 2]
        }
    }

    private fun calculateWeightedRegression(yValues: List<Double>, consistency: Double): Double {
        val n = yValues.size
        if (n < 3) return yValues.lastOrNull() ?: 0.0

        val weights = List(n) { i -> 1.0 + (i.toDouble() / (n - 1)) }
        val xValues = List(n) { it.toDouble() }

        val sumW = weights.sum()
        val weightedMeanX = weights.zip(xValues).sumOf { it.first * it.second } / sumW
        val weightedMeanY = weights.zip(yValues).sumOf { it.first * it.second } / sumW

        val numerator = weights.indices.sumOf { i ->
            weights[i] * (xValues[i] - weightedMeanX) * (yValues[i] - weightedMeanY)
        }
        val denominator = weights.indices.sumOf { i ->
            weights[i] * (xValues[i] - weightedMeanX).pow(2)
        }

        if (denominator == 0.0) return yValues.last()

        var slope = numerator / denominator
        if (slope > 0) {
            slope *= consistency
        } else {
            slope *= 0.8
        }

        val intercept = weightedMeanY - slope * weightedMeanX
        val prediction = intercept + slope * (n - 1 + 4)
        
        return kotlin.math.max(prediction, yValues.last() * 0.8)
    }
}

sealed class ProgressUiState {
    object Loading : ProgressUiState()
    object Empty : ProgressUiState()
    data class Success(val exercises: List<ExerciseProgress>) : ProgressUiState()
}
