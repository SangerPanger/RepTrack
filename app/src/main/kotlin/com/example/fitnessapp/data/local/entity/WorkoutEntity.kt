package com.example.fitnessapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val startedAt: Long,
    val finishedAt: Long? = null,
    val notes: String? = null
)
