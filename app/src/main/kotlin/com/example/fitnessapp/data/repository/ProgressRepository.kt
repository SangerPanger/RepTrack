package com.example.fitnessapp.data.repository

import com.example.fitnessapp.data.local.dao.SetDao
import com.example.fitnessapp.data.local.entity.SetEntity
import com.example.fitnessapp.data.local.entity.SetWithDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProgressRepository(private val setDao: SetDao) {
    
    fun getExerciseProgress(exerciseId: Long): Flow<List<SetEntity>> {
        return setDao.getAllSetsForExercise(exerciseId)
    }

    fun getExerciseProgressWithDate(exerciseId: Long): Flow<List<SetWithDate>> {
        return setDao.getAllSetsForExerciseWithDate(exerciseId)
    }

    // Helper functions for calculations can be here or in a UseCase
    fun calculateVolume(reps: Int, weight: Double): Double = reps * weight
    
    fun calculateEstimated1RM(reps: Int, weight: Double): Double {
        if (reps == 0) return 0.0
        return weight * (1.0 + reps.toDouble() / 30.0)
    }
}
