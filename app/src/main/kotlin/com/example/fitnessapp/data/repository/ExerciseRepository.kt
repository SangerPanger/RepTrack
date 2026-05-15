package com.example.fitnessapp.data.repository

import com.example.fitnessapp.data.local.dao.ExerciseDao
import com.example.fitnessapp.data.local.entity.ExerciseEntity
import kotlinx.coroutines.flow.Flow

class ExerciseRepository(private val exerciseDao: ExerciseDao) {
    val allExercises: Flow<List<ExerciseEntity>> = exerciseDao.getAllExercises()

    suspend fun getOrCreateExercise(name: String): ExerciseEntity {
        val normalizedName = name.trim().replace("\\s+".toRegex(), " ")
        val existing = exerciseDao.getExerciseByNameIgnoreCase(normalizedName)
        if (existing != null) return existing
        
        val id = exerciseDao.insertExercise(ExerciseEntity(name = normalizedName))
        return ExerciseEntity(id = id, name = normalizedName)
    }

    suspend fun getExerciseById(id: Long): ExerciseEntity? {
        return exerciseDao.getExerciseById(id)
    }
}
