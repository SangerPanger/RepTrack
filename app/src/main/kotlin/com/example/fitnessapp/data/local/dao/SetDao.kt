package com.example.fitnessapp.data.local.dao

import androidx.room.*
import com.example.fitnessapp.data.local.entity.SetEntity
import com.example.fitnessapp.data.local.entity.SetWithDate
import kotlinx.coroutines.flow.Flow

@Dao
interface SetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(set: SetEntity): Long

    @Update
    suspend fun updateSet(set: SetEntity)

    @Delete
    suspend fun deleteSet(set: SetEntity)

    @Query("SELECT * FROM sets WHERE workoutExerciseId = :workoutExerciseId ORDER BY setNumber")
    fun getSetsForWorkoutExercise(workoutExerciseId: Long): Flow<List<SetEntity>>

    @Query("""
        SELECT s.*, w.startedAt 
        FROM sets s 
        JOIN workout_exercises we ON s.workoutExerciseId = we.id 
        JOIN workouts w ON we.workoutId = w.id
        WHERE we.exerciseId = :exerciseId 
        ORDER BY w.startedAt ASC
    """)
    fun getAllSetsForExerciseWithDate(exerciseId: Long): Flow<List<SetWithDate>>

    @Query("SELECT * FROM sets s JOIN workout_exercises we ON s.workoutExerciseId = we.id WHERE we.exerciseId = :exerciseId ORDER BY s.id DESC")
    fun getAllSetsForExercise(exerciseId: Long): Flow<List<SetEntity>>
}
