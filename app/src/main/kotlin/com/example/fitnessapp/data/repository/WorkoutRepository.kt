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
    val latestWorkout: Flow<WorkoutEntity?> = workoutDao.getLatestWorkout()
    val workoutCount: Flow<Int> = workoutDao.getWorkoutCount()

    suspend fun startWorkout(title: String): Long {
        val workout = WorkoutEntity(title = title, startedAt = System.currentTimeMillis())
        return workoutDao.insertWorkout(workout)
    }

    suspend fun finishWorkout(workoutId: Long, notes: String?) {
        val workout = workoutDao.getWorkoutById(workoutId)
        workout?.let {
            workoutDao.updateWorkout(it.copy(finishedAt = System.currentTimeMillis(), notes = notes))
        }
    }

    fun getWorkoutExercisesWithSets(workoutId: Long): Flow<List<WorkoutExerciseWithSets>> {
        return workoutExerciseDao.getWorkoutExercisesWithSets(workoutId)
    }

    suspend fun addExerciseToWorkout(workoutId: Long, exerciseId: Long, orderIndex: Int): Long {
        return workoutExerciseDao.insertWorkoutExercise(
            WorkoutExerciseEntity(workoutId = workoutId, exerciseId = exerciseId, orderIndex = orderIndex)
        )
    }

    suspend fun addSet(workoutExerciseId: Long, setNumber: Int, reps: Int, weight: Double, rpe: Int? = null) {
        setDao.insertSet(
            SetEntity(workoutExerciseId = workoutExerciseId, setNumber = setNumber, reps = reps, weight = weight, rpe = rpe)
        )
    }

    suspend fun updateSet(set: SetEntity) {
        setDao.updateSet(set)
    }

    suspend fun deleteSet(set: SetEntity) {
        setDao.deleteSet(set)
    }
}
