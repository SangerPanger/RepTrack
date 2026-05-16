package com.example.fitnessapp.data.local.dao

import androidx.room.*
import com.example.fitnessapp.data.local.entity.FoodLogEntity
import com.example.fitnessapp.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: UserProfileEntity)
}

@Dao
interface FoodLogDao {
    @Query("SELECT * FROM food_logs ORDER BY date DESC")
    fun getAllFoodLogs(): Flow<List<FoodLogEntity>>

    @Insert
    suspend fun insertFoodLog(foodLog: FoodLogEntity)

    @Update
    suspend fun updateFoodLog(foodLog: FoodLogEntity)

    @Delete
    suspend fun deleteFoodLog(foodLog: FoodLogEntity)

    @Query("DELETE FROM food_logs WHERE date >= :startTime AND date <= :endTime")
    suspend fun deleteFoodLogsInRange(startTime: Long, endTime: Long)
}
