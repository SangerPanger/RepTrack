package com.example.fitnessapp.data.repository

import com.example.fitnessapp.data.local.dao.WorkoutDao
import com.example.fitnessapp.data.local.dao.WorkoutExerciseDao
import com.example.fitnessapp.data.local.dao.SetDao
import com.example.fitnessapp.data.local.entity.*
import kotlinx.coroutines.flow.Flow

class WorkoutRepository(
    private val workoutDao: WorkoutDao,
    private val workoutExerciseDao: WorkoutExerciseDao,
    private val setDao: SetDao
) {
    val allWorkouts: Flow<List<WorkoutEntity>> = workoutDao.getAllWorkouts()
    val allWorkoutsWithExercises: Flow<List<WorkoutWithExercises>> = workoutDao.getAllWorkoutsWithExercises()
    val latestWorkout: Flow<WorkoutEntity?> = workoutDao.getLatestWorkout()
    val workoutCount: Flow<Int> = workoutDao.getWorkoutCount()
    val uniqueWorkoutTitles: Flow<List<String>> = workoutDao.getUniqueWorkoutTitles()

    suspend fun startWorkout(title: String): Long {
        val lastWorkout = workoutDao.getLastWorkoutByTitle(title)
        val workoutId = workoutDao.insertWorkout(WorkoutEntity(title = title, startedAt = System.currentTimeMillis()))
        
        if (lastWorkout != null) {
            val lastExercises = workoutExerciseDao.getWorkoutExercisesWithSetsSuspend(lastWorkout.id)
            lastExercises.forEach { exerciseWithSets ->
                val newWorkoutExerciseId = workoutExerciseDao.insertWorkoutExercise(
                    WorkoutExerciseEntity(
                        workoutId = workoutId,
                        exerciseId = exerciseWithSets.exercise.id,
                        orderIndex = exerciseWithSets.workoutExercise.orderIndex,
                        isDropset = exerciseWithSets.workoutExercise.isDropset,
                        startingWeight = exerciseWithSets.workoutExercise.startingWeight,
                        dropWeightDecrease = exerciseWithSets.workoutExercise.dropWeightDecrease
                    )
                )
                // Optionally add the same number of sets but reset them
                exerciseWithSets.sets.forEach { set ->
                    setDao.insertSet(
                        SetEntity(
                            workoutExerciseId = newWorkoutExerciseId,
                            setNumber = set.setNumber,
                            reps = set.reps,
                            weight = set.weight,
                            isDrop = set.isDrop,
                            completed = false
                        )
                    )
                }
            }
        }
        
        return workoutId
    }

    suspend fun finishWorkout(workoutId: Long, notes: String?) {
        val workout = workoutDao.getWorkoutById(workoutId)
        workout?.let {
            val finishedAt = it.finishedAt ?: (System.currentTimeMillis() - it.durationOffsetMs)
            val duration = ((finishedAt - it.startedAt)) / (1000 * 60)
            workoutDao.updateWorkout(it.copy(
                finishedAt = finishedAt, 
                notes = notes,
                manualDurationMinutes = duration
            ))
        }
    }

    suspend fun updateWorkout(workout: WorkoutEntity) {
        workoutDao.updateWorkout(workout)
    }

    suspend fun deleteWorkout(workoutId: Long) {
        val workout = workoutDao.getWorkoutById(workoutId)
        workout?.let {
            workoutDao.deleteWorkout(it)
        }
    }

    suspend fun getWorkout(workoutId: Long): WorkoutEntity? {
        return workoutDao.getWorkoutById(workoutId)
    }

    fun getWorkoutFlow(workoutId: Long): Flow<WorkoutEntity?> {
        return workoutDao.getWorkoutFlow(workoutId)
    }

    fun getWorkoutExercisesWithSets(workoutId: Long): Flow<List<WorkoutExerciseWithSets>> {
        return workoutExerciseDao.getWorkoutExercisesWithSets(workoutId)
    }

    suspend fun addExerciseToWorkout(
        workoutId: Long, 
        exerciseId: Long, 
        orderIndex: Int, 
        isDropset: Boolean = false,
        startingWeight: Double = 0.0,
        dropWeightDecrease: Double = 0.0
    ): Long {
        return workoutExerciseDao.insertWorkoutExercise(
            WorkoutExerciseEntity(
                workoutId = workoutId, 
                exerciseId = exerciseId, 
                orderIndex = orderIndex, 
                isDropset = isDropset,
                startingWeight = startingWeight,
                dropWeightDecrease = dropWeightDecrease
            )
        )
    }

    suspend fun addSet(workoutExerciseId: Long, setNumber: Int, reps: Int, weight: Double, rpe: Int? = null, isDrop: Boolean = false) {
        setDao.insertSet(
            SetEntity(workoutExerciseId = workoutExerciseId, setNumber = setNumber, reps = reps, weight = weight, rpe = rpe, isDrop = isDrop)
        )
    }

    suspend fun updateSet(set: SetEntity) {
        setDao.updateSet(set)
    }

    suspend fun deleteSet(set: SetEntity) {
        setDao.deleteSet(set)
    }

    suspend fun deleteWorkoutExercise(workoutExerciseId: Long) {
        workoutExerciseDao.deleteWorkoutExerciseById(workoutExerciseId)
    }

    suspend fun getSetsForWorkout(workoutId: Long): List<SetEntity> {
        return setDao.getSetsForWorkout(workoutId)
    }
}
