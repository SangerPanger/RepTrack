package com.example.fitnessapp.ui.screens.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.local.entity.SetEntity
import com.example.fitnessapp.data.local.entity.WorkoutExerciseWithSets
import com.example.fitnessapp.data.repository.ExerciseRepository
import com.example.fitnessapp.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class WorkoutSessionViewModel(
    private val workoutId: Long,
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _workoutExercises = MutableStateFlow<List<WorkoutExerciseWithSets>>(emptyList())
    val workoutExercises: StateFlow<List<WorkoutExerciseWithSets>> = _workoutExercises.asStateFlow()

    init {
        viewModelScope.launch {
            workoutRepository.getWorkoutExercisesWithSets(workoutId).collectLatest {
                _workoutExercises.value = it
            }
        }
    }

    fun addExercise(name: String) {
        viewModelScope.launch {
            val exercise = exerciseRepository.getOrCreateExercise(name)
            workoutRepository.addExerciseToWorkout(
                workoutId = workoutId,
                exerciseId = exercise.id,
                orderIndex = _workoutExercises.value.size
            )
        }
    }

    fun addSet(workoutExerciseId: Long) {
        viewModelScope.launch {
            val currentSets = _workoutExercises.value.find { it.workoutExercise.id == workoutExerciseId }?.sets ?: emptyList()
            val nextSetNumber = currentSets.size + 1
            val lastSet = currentSets.lastOrNull()
            
            workoutRepository.addSet(
                workoutExerciseId = workoutExerciseId,
                setNumber = nextSetNumber,
                reps = lastSet?.reps ?: 10,
                weight = lastSet?.weight ?: 0.0
            )
        }
    }

    fun updateSet(set: SetEntity) {
        viewModelScope.launch {
            workoutRepository.updateSet(set)
        }
    }

    fun finishWorkout(notes: String?) {
        viewModelScope.launch {
            workoutRepository.finishWorkout(workoutId, notes)
        }
    }
}
