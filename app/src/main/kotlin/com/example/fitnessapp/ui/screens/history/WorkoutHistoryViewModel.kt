package com.example.fitnessapp.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.local.entity.WorkoutWithExercises
import com.example.fitnessapp.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class WorkoutHistoryViewModel(private val workoutRepository: WorkoutRepository) : ViewModel() {
    val allWorkouts: StateFlow<List<WorkoutWithExercises>> = workoutRepository.allWorkoutsWithExercises
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
