package com.example.fitnessapp.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fitnessapp.data.local.dao.ExerciseDao
import com.example.fitnessapp.data.local.dao.SetDao
import com.example.fitnessapp.data.local.dao.WorkoutDao
import com.example.fitnessapp.data.local.dao.WorkoutExerciseDao
import com.example.fitnessapp.data.local.entity.ExerciseEntity
import com.example.fitnessapp.data.local.entity.SetEntity
import com.example.fitnessapp.data.local.entity.WorkoutEntity
import com.example.fitnessapp.data.local.entity.WorkoutExerciseEntity

@Database(
    entities = [
        WorkoutEntity::class,
        ExerciseEntity::class,
        WorkoutExerciseEntity::class,
        SetEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutExerciseDao(): WorkoutExerciseDao
    abstract fun setDao(): SetDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fitness_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
