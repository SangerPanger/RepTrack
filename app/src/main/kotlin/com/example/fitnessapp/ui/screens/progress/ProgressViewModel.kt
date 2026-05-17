package com.example.fitnessapp.ui.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.local.entity.*
import com.example.fitnessapp.data.repository.ExerciseRepository
import com.example.fitnessapp.data.repository.FoodRepository
import com.example.fitnessapp.data.repository.ProgressRepository
import com.example.fitnessapp.data.repository.WorkoutRepository
import com.example.fitnessapp.domain.model.*
import com.example.fitnessapp.domain.progress.ProgressPredictionCalculator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

data class ProgressPoint(val date: Long, val value: Double)

data class ExerciseProgress(
    val exercise: ExerciseEntity,
    val sets: List<SetEntity>,
    val totalVolume: Double,
    val estimated1RM: Double,
    val historyPoints: List<ProgressPoint> = emptyList(),
    val prediction: ProgressPredictionResult? = null
)

data class FoodProgress(
    val estimatedWeightChange: Double, // in kg
    val averageDailyCalories: Double,
    val averageDailyProtein: Double,
    val averageDailyFat: Double,
    val tdee: Double,
    val totalDaysTracked: Int
)

class ProgressViewModel(
    private val progressRepository: ProgressRepository,
    private val exerciseRepository: ExerciseRepository,
    private val foodRepository: FoodRepository,
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val calculator = ProgressPredictionCalculator()

    private val _uiState = MutableStateFlow<ProgressUiState>(ProgressUiState.Loading)
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    val userProfile: StateFlow<UserProfileEntity?> = foodRepository.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val foodLogs: StateFlow<List<FoodLogEntity>> = foodRepository.getAllFoodLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allWorkouts: StateFlow<List<WorkoutEntity>> = workoutRepository.allWorkouts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val last4WeeksAdherence: StateFlow<Double?> = allWorkouts.map { workouts ->
        if (workouts.isEmpty()) return@map null
        
        val now = System.currentTimeMillis()
        val twoWeeksAgo = now - (2L * 7 * 24 * 60 * 60 * 1000)
        
        val uniqueDays = workouts
            .filter { it.startedAt > twoWeeksAgo }
            .groupBy { 
                val cal = Calendar.getInstance()
                cal.timeInMillis = it.startedAt
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                cal.timeInMillis
            }.size
        
        if (uniqueDays > 0) uniqueDays.toDouble() / 2.0 else null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val foodProgress: StateFlow<FoodProgress?> = combine(userProfile, foodLogs) { profile, logs ->
        if (profile == null || logs.isEmpty()) return@combine null

        val tdee = if (profile != null && profile.estimatedTdee > 0) profile.estimatedTdee else {
            val p = profile ?: UserProfileEntity(age = 30, sex = Sex.MALE, heightCm = 175.0, bodyWeightKg = 70.0)
            val bmr = if (p.sex == Sex.MALE) {
                10 * p.bodyWeightKg + 6.25 * p.heightCm - 5 * p.age + 5
            } else {
                10 * p.bodyWeightKg + 6.25 * p.heightCm - 5 * p.age - 161
            }
            bmr * 1.2 // Assume sedentary
        }

        val dailyTotals = logs.groupBy {
            val cal = Calendar.getInstance()
            cal.timeInMillis = it.date
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            cal.timeInMillis
        }.mapValues { (_, dayLogs) ->
            Triple(
                dayLogs.sumOf { it.calories },
                dayLogs.sumOf { it.protein },
                dayLogs.sumOf { it.fats }
            )
        }

        val totalCalories = dailyTotals.values.sumOf { it.first }
        val totalProtein = dailyTotals.values.sumOf { it.second }
        val totalFat = dailyTotals.values.sumOf { it.third }
        val daysTracked = dailyTotals.size
        val avgCalories = if (daysTracked > 0) totalCalories / daysTracked else 0.0
        val avgProtein = if (daysTracked > 0) totalProtein / daysTracked else 0.0
        val avgFat = if (daysTracked > 0) totalFat / daysTracked else 0.0
        
        // Estimated weight change (kg) = (Total Calories - (TDEE * Days)) / 7700
        val weightChange = if (daysTracked > 0) (totalCalories - (tdee * daysTracked)) / 7700.0 else 0.0

        FoodProgress(
            estimatedWeightChange = weightChange,
            averageDailyCalories = avgCalories,
            averageDailyProtein = avgProtein,
            averageDailyFat = avgFat,
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

                // Create a list of flows, one for each exercise, all combining global context
                val exerciseFlows = exercises.map { exercise ->
                    combine(
                        progressRepository.getExerciseProgressWithDate(exercise.id),
                        userProfile,
                        foodProgress,
                        last4WeeksAdherence
                    ) { setsWithDate, profile, fProgress, adherence ->
                        if (setsWithDate.isEmpty()) return@combine null
                        if (profile == null) return@combine null // Wait for profile

                        calculateExerciseProgress(exercise, setsWithDate, profile, fProgress, adherence)
                    }
                }

                combine(exerciseFlows) { results ->
                    results.filterNotNull().sortedBy { it.exercise.name }
                }.collect { progressList ->
                    if (progressList.isNotEmpty()) {
                        _uiState.value = ProgressUiState.Success(progressList)
                    } else if (userProfile.value != null) {
                        // Only show Empty state if we have a profile but no exercises have history
                        _uiState.value = ProgressUiState.Empty
                    }
                }
            }
        }
    }

    private fun calculateWeeklySets(setsWithDate: List<SetWithDate>): Double { val now = System.currentTimeMillis(); val twoWeeksAgo = now - (14L * 24 * 60 * 60 * 1000); val recentSets = setsWithDate.filter { it.startedAt > twoWeeksAgo }; return recentSets.size / 2.0; } private fun calculateExerciseProgress(
        exercise: ExerciseEntity,
        setsWithDate: List<SetWithDate>,
        profile: UserProfileEntity,
        fProgress: FoodProgress?,
        adherence: Double?
    ): ExerciseProgress {
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
        val previousBest1RM = if (points.isNotEmpty()) points.maxOf { it.value } else 0.0
        
        var currentProfile = profile.toDomainModel()

        if (!currentProfile.useSettingsForNutrition && fProgress != null && fProgress.totalDaysTracked > 0) {
            currentProfile = currentProfile.copy(
                averageCaloriesPerDay = fProgress.averageDailyCalories,
                averageProteinGramsPerDay = fProgress.averageDailyProtein,
                fatGramsPerDay = fProgress.averageDailyFat
            )
        }

        val prediction = calculator.calculatePrediction(
            exerciseName = exercise.name,
            exerciseId = exercise.id,
            current1RM = currentMax1RM,
            previousBest1RM = previousBest1RM,
            profile = currentProfile,
            recentWorkoutsPerWeek = adherence, weeklySets = calculateWeeklySets(setsWithDate),
            recent1RMHistory = points.map { it.value }.takeLast(5)
        )
        
        return ExerciseProgress(
            exercise = exercise,
            sets = setsWithDate.map { it.setEntity },
            totalVolume = volume,
            estimated1RM = currentMax1RM,
            historyPoints = points,
            prediction = prediction
        )
    }
}

sealed class ProgressUiState {
    object Loading : ProgressUiState()
    object Empty : ProgressUiState()
    data class Success(val exercises: List<ExerciseProgress>) : ProgressUiState()
}
