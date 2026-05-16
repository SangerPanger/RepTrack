package com.example.fitnessapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1, // Only one profile
    val age: Int = 0,
    val gender: String = "",
    val height: Double = 0.0,
    val currentWeight: Double = 0.0,
    val targetWeight: Double = 0.0
)

@Entity(tableName = "food_logs")
data class FoodLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long, // Timestamp
    val carbs: Double,
    val fats: Double,
    val protein: Double,
    val calories: Double
)
