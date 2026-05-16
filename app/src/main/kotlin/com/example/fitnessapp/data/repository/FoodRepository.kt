package com.example.fitnessapp.data.repository

import com.example.fitnessapp.data.local.dao.FoodLogDao
import com.example.fitnessapp.data.local.dao.UserProfileDao
import com.example.fitnessapp.data.local.entity.FoodLogEntity
import com.example.fitnessapp.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

class FoodRepository(
    private val userProfileDao: UserProfileDao,
    private val foodLogDao: FoodLogDao
) {
    fun getUserProfile(): Flow<UserProfileEntity?> = userProfileDao.getUserProfile()

    suspend fun saveUserProfile(profile: UserProfileEntity) {
        userProfileDao.insertOrUpdate(profile)
    }

    fun getAllFoodLogs(): Flow<List<FoodLogEntity>> = foodLogDao.getAllFoodLogs()

    suspend fun addFoodLog(foodLog: FoodLogEntity) {
        foodLogDao.insertFoodLog(foodLog)
    }

    suspend fun updateFoodLog(foodLog: FoodLogEntity) {
        foodLogDao.updateFoodLog(foodLog)
    }

    suspend fun deleteFoodLog(foodLog: FoodLogEntity) {
        foodLogDao.deleteFoodLog(foodLog)
    }

    suspend fun deleteFoodLogsInRange(startTime: Long, endTime: Long) {
        foodLogDao.deleteFoodLogsInRange(startTime, endTime)
    }
}
