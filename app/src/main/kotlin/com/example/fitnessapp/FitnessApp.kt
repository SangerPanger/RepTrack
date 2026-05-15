package com.example.fitnessapp

import android.app.Application
import com.example.fitnessapp.data.local.database.AppDatabase
import com.example.fitnessapp.data.repository.ExerciseRepository
import com.example.fitnessapp.data.repository.ProgressRepository
import com.example.fitnessapp.data.repository.WorkoutRepository

class FitnessApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val workoutRepository by lazy { 
        WorkoutRepository(database.workoutDao(), database.workoutExerciseDao(), database.setDao()) 
    }
    val exerciseRepository by lazy { ExerciseRepository(database.exerciseDao()) }
    val progressRepository by lazy { ProgressRepository(database.setDao()) }
}
