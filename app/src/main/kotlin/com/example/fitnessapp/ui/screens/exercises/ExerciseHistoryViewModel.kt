package com.example.fitnessapp.ui.screens.exercises

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.local.entity.ExerciseEntity
import com.example.fitnessapp.data.repository.ExerciseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ExerciseHistoryViewModel(private val exerciseRepository: ExerciseRepository) : ViewModel() {
    val allExercises: StateFlow<List<ExerciseEntity>> = exerciseRepository.allExercises
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
