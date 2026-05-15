package com.example.fitnessapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.local.entity.WorkoutEntity
import com.example.fitnessapp.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(private val workoutRepository: WorkoutRepository) : ViewModel() {
    val latestWorkout: StateFlow<WorkoutEntity?> = workoutRepository.latestWorkout
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val workoutCount: StateFlow<Int> = workoutRepository.workoutCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    
    val uniqueWorkoutTitles: StateFlow<List<String>> = workoutRepository.uniqueWorkoutTitles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun startNewWorkout(title: String): Long {
        return workoutRepository.startWorkout(title)
    }
}
