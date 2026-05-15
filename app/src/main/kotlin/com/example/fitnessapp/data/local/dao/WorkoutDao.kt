package com.example.fitnessapp.data.local.dao

import androidx.room.*
import com.example.fitnessapp.data.local.entity.WorkoutEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity): Long

    @Update
    suspend fun updateWorkout(workout: WorkoutEntity)

    @Delete
    suspend fun deleteWorkout(workout: WorkoutEntity)

    @Query("SELECT * FROM workouts ORDER BY startedAt DESC")
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getWorkoutById(id: Long): WorkoutEntity?

    @Query("SELECT * FROM workouts ORDER BY startedAt DESC LIMIT 1")
    fun getLatestWorkout(): Flow<WorkoutEntity?>

    @Query("SELECT COUNT(*) FROM workouts")
    fun getWorkoutCount(): Flow<Int>
}
