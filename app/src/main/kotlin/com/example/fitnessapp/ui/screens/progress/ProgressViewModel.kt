package com.example.fitnessapp.ui.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.local.entity.ExerciseEntity
import com.example.fitnessapp.data.local.entity.SetEntity
import com.example.fitnessapp.data.repository.ExerciseRepository
import com.example.fitnessapp.data.repository.ProgressRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProgressPoint(val date: Long, val value: Double)

data class ExerciseProgress(
    val exercise: ExerciseEntity,
    val sets: List<SetEntity>,
    val totalVolume: Double,
    val estimated1RM: Double,
    val historyPoints: List<ProgressPoint> = emptyList(),
    val projectedGain: Double = 0.0
)

class ProgressViewModel(
    private val progressRepository: ProgressRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProgressUiState>(ProgressUiState.Loading)
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

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
                                            value = sets.maxOf { progressRepository.calculateEstimated1RM(it.setEntity.reps, it.setEntity.weight) }
                                        )
                                    }
                                    .sortedBy { it.date }
                                
                                val volume = setsWithDate.sumOf { progressRepository.calculateVolume(it.setEntity.reps, it.setEntity.weight) }
                                val currentMax1RM = points.last().value
                                val projection = calculateFutureProjection(points)
                                
                                val exerciseProgress = ExerciseProgress(
                                    exercise = exercise,
                                    sets = setsWithDate.map { it.setEntity },
                                    totalVolume = volume,
                                    estimated1RM = currentMax1RM,
                                    historyPoints = points,
                                    projectedGain = projection
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

    private fun calculateFutureProjection(points: List<ProgressPoint>): Double {
        if (points.size < 2) return 0.0
        
        val n = points.size
        val firstDate = points.first().date
        val x = points.map { (it.date - firstDate).toDouble() / (1000 * 60 * 60 * 24) }
        val y = points.map { it.value }
        
        val sumX = x.sum()
        val sumY = y.sum()
        val sumXY = x.zip(y).sumOf { it.first * it.second }
        val sumX2 = x.sumOf { it * it }
        
        val denominator = n * sumX2 - sumX * sumX
        if (denominator == 0.0) return 0.0
        
        val m = (n * sumXY - sumX * sumY) / denominator
        return m * 28.0
    }
}

sealed class ProgressUiState {
    object Loading : ProgressUiState()
    object Empty : ProgressUiState()
    data class Success(val exercises: List<ExerciseProgress>) : ProgressUiState()
}
