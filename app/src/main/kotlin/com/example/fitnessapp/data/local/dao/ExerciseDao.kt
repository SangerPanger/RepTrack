package com.example.fitnessapp.data.local.dao

import androidx.room.*
import com.example.fitnessapp.data.local.entity.ExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: ExerciseEntity): Long

    @Query("SELECT * FROM exercises ORDER BY name ASC")
    fun getAllExercises(): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE id = :id")
    suspend fun getExerciseById(id: Long): ExerciseEntity?

    @Query("SELECT * FROM exercises WHERE LOWER(name) = LOWER(:name) LIMIT 1")
    suspend fun getExerciseByNameIgnoreCase(name: String): ExerciseEntity?
}
