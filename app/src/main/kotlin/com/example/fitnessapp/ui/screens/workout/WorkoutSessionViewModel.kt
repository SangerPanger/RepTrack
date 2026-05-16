package com.example.fitnessapp.ui.screens.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.local.entity.ExerciseEntity
import com.example.fitnessapp.data.local.entity.SetEntity
import com.example.fitnessapp.data.local.entity.WorkoutEntity
import com.example.fitnessapp.data.local.entity.WorkoutExerciseWithSets
import com.example.fitnessapp.data.repository.ExerciseRepository
import com.example.fitnessapp.data.repository.WorkoutRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

class WorkoutSessionViewModel(
    private val workoutId: Long,
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _workout = MutableStateFlow<WorkoutEntity?>(null)
    val workout: StateFlow<WorkoutEntity?> = _workout.asStateFlow()

    private val _hasChanges = MutableStateFlow(false)
    val hasChanges: StateFlow<Boolean> = _hasChanges.asStateFlow()

    private val _workoutExercises = MutableStateFlow<List<WorkoutExerciseWithSets>>(emptyList())
    val workoutExercises: StateFlow<List<WorkoutExerciseWithSets>> = _workoutExercises.asStateFlow()

    private val _availableExercises = MutableStateFlow<List<ExerciseEntity>>(emptyList())
    val availableExercises: StateFlow<List<ExerciseEntity>> = _availableExercises.asStateFlow()

    private val _elapsedTime = MutableStateFlow("00:00:00")
    val elapsedTime: StateFlow<String> = _elapsedTime.asStateFlow()

    private val _restTimers = MutableStateFlow<Map<Long, String>>(emptyMap())
    val restTimers: StateFlow<Map<Long, String>> = _restTimers.asStateFlow()

    init {
        viewModelScope.launch {
            workoutRepository.getWorkoutFlow(workoutId).collectLatest {
                _workout.value = it
            }
        }
        viewModelScope.launch {
            workoutRepository.getWorkoutExercisesWithSets(workoutId).collectLatest {
                _workoutExercises.value = it
            }
        }
        viewModelScope.launch {
            exerciseRepository.allExercises.collectLatest {
                _availableExercises.value = it
            }
        }
        
        viewModelScope.launch {
            while (true) {
                val currentWorkout = _workout.value
                if (currentWorkout != null && currentWorkout.finishedAt == null) {
                    val now = System.currentTimeMillis()
                    
                    // Workout timer
                    val elapsedMs = now - currentWorkout.startedAt
                    val totalSeconds = (elapsedMs / 1000).coerceAtLeast(0)
                    val seconds = totalSeconds % 60
                    val minutes = (totalSeconds / 60) % 60
                    val hours = totalSeconds / 3600
                    _elapsedTime.value = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                    
                    // Rest timers for each exercise
                    val newRestTimers = mutableMapOf<Long, String>()
                    _workoutExercises.value.forEach { exerciseWithSets ->
                        val allSetsCompleted = exerciseWithSets.sets.isNotEmpty() && exerciseWithSets.sets.all { it.completed }
                        
                        if (allSetsCompleted) {
                            // If all sets are finished, we don't need a rest timer for the next set
                            newRestTimers[exerciseWithSets.workoutExercise.id] = "00:00"
                        } else {
                            val lastCompletedSet = exerciseWithSets.sets
                                .filter { it.completed && it.completedAt != null }
                                .maxByOrNull { it.completedAt!! }
                            
                            val referenceTime = lastCompletedSet?.completedAt ?: currentWorkout.startedAt
                            
                            val restMs = now - referenceTime
                            val restSeconds = (restMs / 1000).coerceAtLeast(0)
                            val rSec = restSeconds % 60
                            val rMin = restSeconds / 60
                            newRestTimers[exerciseWithSets.workoutExercise.id] = String.format("%02d:%02d", rMin, rSec)
                        }
                    }
                    _restTimers.value = newRestTimers
                }
                delay(1000)
            }
        }
    }

    fun addExercise(name: String, isDropset: Boolean = false, initialWeight: Double = 0.0, dropDecrease: Double = 0.0) {
        viewModelScope.launch {
            val exercise = exerciseRepository.getOrCreateExercise(name)
            val workoutExerciseId = addExerciseToWorkout(exercise.id, isDropset, initialWeight, dropDecrease)
            if (initialWeight > 0 || isDropset) {
                workoutRepository.addSet(
                    workoutExerciseId = workoutExerciseId,
                    setNumber = 1,
                    reps = 10,
                    weight = initialWeight
                )
            }
            _hasChanges.value = true
        }
    }

    fun addExerciseById(exerciseId: Long, isDropset: Boolean = false, initialWeight: Double = 0.0, dropDecrease: Double = 0.0) {
        viewModelScope.launch {
            val workoutExerciseId = addExerciseToWorkout(exerciseId, isDropset, initialWeight, dropDecrease)
            if (initialWeight > 0 || isDropset) {
                workoutRepository.addSet(
                    workoutExerciseId = workoutExerciseId,
                    setNumber = 1,
                    reps = 10,
                    weight = initialWeight
                )
            }
            _hasChanges.value = true
        }
    }

    private suspend fun addExerciseToWorkout(exerciseId: Long, isDropset: Boolean = false, startingWeight: Double = 0.0, dropWeightDecrease: Double = 0.0): Long {
        return workoutRepository.addExerciseToWorkout(
            workoutId = workoutId,
            exerciseId = exerciseId,
            orderIndex = _workoutExercises.value.size,
            isDropset = isDropset,
            startingWeight = startingWeight,
            dropWeightDecrease = dropWeightDecrease
        )
    }

    fun addDrop(workoutExerciseId: Long) {
        viewModelScope.launch {
            val exerciseWithSets = _workoutExercises.value.find { it.workoutExercise.id == workoutExerciseId } ?: return@launch
            val currentSets = exerciseWithSets.sets
            val nextSetNumber = currentSets.size + 1
            val lastSet = currentSets.lastOrNull()

            val weight = if (lastSet != null) {
                (lastSet.weight - exerciseWithSets.workoutExercise.dropWeightDecrease).coerceAtLeast(0.0)
            } else {
                0.0
            }

            workoutRepository.addSet(
                workoutExerciseId = workoutExerciseId,
                setNumber = nextSetNumber,
                reps = lastSet?.reps ?: 10,
                weight = weight
            )
            _hasChanges.value = true
        }
    }

    fun addSet(workoutExerciseId: Long) {
        viewModelScope.launch {
            val exerciseWithSets = _workoutExercises.value.find { it.workoutExercise.id == workoutExerciseId } ?: return@launch
            val currentSets = exerciseWithSets.sets
            val nextSetNumber = currentSets.size + 1
            val lastSet = currentSets.lastOrNull()

            val weight = if (exerciseWithSets.workoutExercise.isDropset) {
                exerciseWithSets.workoutExercise.startingWeight
            } else {
                lastSet?.weight ?: 0.0
            }

            workoutRepository.addSet(
                workoutExerciseId = workoutExerciseId,
                setNumber = nextSetNumber,
                reps = lastSet?.reps ?: 10,
                weight = weight
            )
            _hasChanges.value = true
        }
    }

    fun deleteSet(set: SetEntity) {
        viewModelScope.launch {
            workoutRepository.deleteSet(set)
            
            // Reorder remaining sets for this workoutExercise
            val currentExerciseWithSets = _workoutExercises.value.find { it.workoutExercise.id == set.workoutExerciseId }
            currentExerciseWithSets?.let { exerciseWithSets ->
                val remainingSets = exerciseWithSets.sets
                    .filter { it.id != set.id }
                    .sortedBy { it.setNumber }
                
                remainingSets.forEachIndexed { index, remainingSet ->
                    val newNumber = index + 1
                    if (remainingSet.setNumber != newNumber) {
                        workoutRepository.updateSet(remainingSet.copy(setNumber = newNumber))
                    }
                }
            }
            _hasChanges.value = true
        }
    }

    fun updateSet(set: SetEntity) {
        viewModelScope.launch {
            val updatedSet = if (set.completed && set.completedAt == null) {
                set.copy(completedAt = System.currentTimeMillis())
            } else if (!set.completed) {
                set.copy(completedAt = null)
            } else {
                set
            }
            workoutRepository.updateSet(updatedSet)
            _hasChanges.value = true
        }
    }

    fun deleteExercise(workoutExerciseId: Long) {
        viewModelScope.launch {
            workoutRepository.deleteWorkoutExercise(workoutExerciseId)
            _hasChanges.value = true
        }
    }

    fun finishWorkout(notes: String?) {
        viewModelScope.launch {
            workoutRepository.finishWorkout(workoutId, notes)
        }
    }

    fun deleteWorkout(onDeleted: () -> Unit) {
        viewModelScope.launch {
            workoutRepository.deleteWorkout(workoutId)
            onDeleted()
        }
    }

    fun updateWorkoutDate(timestamp: Long) {
        viewModelScope.launch {
            _workout.value?.let { currentWorkout ->
                val currentCalendar = Calendar.getInstance().apply { timeInMillis = currentWorkout.startedAt }
                val newCalendar = Calendar.getInstance().apply { timeInMillis = timestamp }
                
                // Preserve current time
                newCalendar.set(Calendar.HOUR_OF_DAY, currentCalendar.get(Calendar.HOUR_OF_DAY))
                newCalendar.set(Calendar.MINUTE, currentCalendar.get(Calendar.MINUTE))
                newCalendar.set(Calendar.SECOND, currentCalendar.get(Calendar.SECOND))
                newCalendar.set(Calendar.MILLISECOND, currentCalendar.get(Calendar.MILLISECOND))

                val newStartedAt = newCalendar.timeInMillis
                val updatedWorkout = if (currentWorkout.finishedAt != null) {
                    val duration = currentWorkout.finishedAt - currentWorkout.startedAt
                    currentWorkout.copy(startedAt = newStartedAt, finishedAt = newStartedAt + duration)
                } else {
                    currentWorkout.copy(startedAt = newStartedAt)
                }

                workoutRepository.updateWorkout(updatedWorkout)
                _hasChanges.value = true
            }
        }
    }

    fun updateWorkoutTitle(title: String) {
        viewModelScope.launch {
            _workout.value?.let {
                workoutRepository.updateWorkout(it.copy(title = title))
                _hasChanges.value = true
            }
        }
    }

    fun updateWorkoutDuration(durationMinutes: Long) {
        viewModelScope.launch {
            _workout.value?.let { currentWorkout ->
                val newFinishedAt = currentWorkout.startedAt + (durationMinutes * 60 * 1000)
                workoutRepository.updateWorkout(currentWorkout.copy(finishedAt = newFinishedAt))
                _hasChanges.value = true
            }
        }
    }
}
