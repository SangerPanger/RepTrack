package com.example.fitnessapp.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.local.entity.FoodLogEntity
import com.example.fitnessapp.data.local.entity.WorkoutWithExercises
import com.example.fitnessapp.data.repository.FoodRepository
import com.example.fitnessapp.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class WorkoutHistoryViewModel(
    private val workoutRepository: WorkoutRepository,
    private val foodRepository: FoodRepository
) : ViewModel() {
    val allWorkouts: StateFlow<List<WorkoutWithExercises>> = workoutRepository.allWorkoutsWithExercises
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFoodLogs: StateFlow<List<FoodLogEntity>> = foodRepository.getAllFoodLogs()
        .map { logs ->
            logs.groupBy { 
                val cal = Calendar.getInstance().apply { 
                    timeInMillis = it.date
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                cal.timeInMillis
            }.map { (date, dailyLogs) ->
                FoodLogEntity(
                    id = dailyLogs.first().id, // Use one of the IDs or a dummy
                    date = date,
                    carbs = dailyLogs.sumOf { it.carbs },
                    fats = dailyLogs.sumOf { it.fats },
                    protein = dailyLogs.sumOf { it.protein },
                    calories = dailyLogs.sumOf { it.calories }
                )
            }.sortedByDescending { it.date }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateFoodLog(foodLog: FoodLogEntity) {
        viewModelScope.launch {
            val startOfDay = foodLog.date
            val endOfDay = startOfDay + 24 * 60 * 60 * 1000 - 1
            
            foodRepository.deleteFoodLogsInRange(startOfDay, endOfDay)
            foodRepository.addFoodLog(foodLog)
        }
    }

    fun deleteFoodLog(foodLog: FoodLogEntity) {
        viewModelScope.launch {
            val startOfDay = foodLog.date
            val endOfDay = startOfDay + 24 * 60 * 60 * 1000 - 1
            
            foodRepository.deleteFoodLogsInRange(startOfDay, endOfDay)
        }
    }
}
