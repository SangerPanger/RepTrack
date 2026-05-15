package com.example.fitnessapp.data.local.dao

import androidx.room.*
import com.example.fitnessapp.data.local.entity.WorkoutExerciseEntity
import com.example.fitnessapp.data.local.entity.WorkoutExerciseWithSets
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutExerciseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutExercise(workoutExercise: WorkoutExerciseEntity): Long

    @Transaction
    @Query("SELECT * FROM workout_exercises WHERE workoutId = :workoutId ORDER BY orderIndex")
    fun getWorkoutExercisesWithSets(workoutId: Long): Flow<List<WorkoutExerciseWithSets>>

    @Query("SELECT * FROM workout_exercises WHERE exerciseId = :exerciseId")
    fun getWorkoutExercisesByExercise(exerciseId: Long): Flow<List<WorkoutExerciseEntity>>
}
