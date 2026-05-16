package com.example.fitnessapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.local.entity.FoodLogEntity
import com.example.fitnessapp.data.local.entity.WorkoutEntity
import com.example.fitnessapp.data.repository.FoodRepository
import com.example.fitnessapp.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeViewModel(
    private val workoutRepository: WorkoutRepository,
    private val foodRepository: FoodRepository
) : ViewModel() {
    val latestWorkout: StateFlow<WorkoutEntity?> = workoutRepository.latestWorkout
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val workoutCount: StateFlow<Int> = workoutRepository.workoutCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    
    val uniqueWorkoutTitles: StateFlow<List<String>> = workoutRepository.uniqueWorkoutTitles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedDate = MutableStateFlow(System.currentTimeMillis())
    val selectedDate: StateFlow<Long> = _selectedDate.asStateFlow()

    private val _lastAddedFoodLogId = MutableStateFlow<Long?>(null)
    val lastAddedFoodLogId: StateFlow<Long?> = _lastAddedFoodLogId.asStateFlow()

    fun setSelectedDate(date: Long) {
        _selectedDate.value = date
        _lastAddedFoodLogId.value = null // Reset undo on date change
    }

    val foodLogForDate: StateFlow<FoodLogEntity?> = combine(foodRepository.getAllFoodLogs(), _selectedDate) { logs, date ->
        val cal = Calendar.getInstance().apply {
            timeInMillis = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val targetDate = cal.timeInMillis
        val targetLogs = logs.filter { 
            val logCal = Calendar.getInstance().apply { timeInMillis = it.date }
            logCal.set(Calendar.HOUR_OF_DAY, 0)
            logCal.set(Calendar.MINUTE, 0)
            logCal.set(Calendar.SECOND, 0)
            logCal.set(Calendar.MILLISECOND, 0)
            logCal.timeInMillis == targetDate
        }
        if (targetLogs.isEmpty()) null
        else {
            FoodLogEntity(
                date = targetDate,
                carbs = targetLogs.sumOf { it.carbs },
                fats = targetLogs.sumOf { it.fats },
                protein = targetLogs.sumOf { it.protein },
                calories = targetLogs.sumOf { it.calories }
            )
        }
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    suspend fun startNewWorkout(title: String): Long {
        return workoutRepository.startWorkout(title)
    }

    fun addFoodLog(carbs: Double, fats: Double, protein: Double, calories: Double, date: Long = System.currentTimeMillis()) {
        viewModelScope.launch {
            val id = foodRepository.addFoodLog(
                FoodLogEntity(
                    date = date,
                    carbs = carbs,
                    fats = fats,
                    protein = protein,
                    calories = calories
                )
            )
            _lastAddedFoodLogId.value = id
        }
    }

    fun undoLastFoodLog() {
        viewModelScope.launch {
            _lastAddedFoodLogId.value?.let { id ->
                foodRepository.deleteFoodLogById(id)
                _lastAddedFoodLogId.value = null
            }
        }
    }
}
