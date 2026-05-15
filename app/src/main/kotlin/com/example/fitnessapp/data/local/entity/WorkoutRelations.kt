package com.example.fitnessapp.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class WorkoutWithExercises(
    @Embedded val workout: WorkoutEntity,
    @Relation(
        entity = WorkoutExerciseEntity::class,
        parentColumn = "id",
        entityColumn = "workoutId"
    )
    val workoutExercises: List<WorkoutExerciseWithSets>
)

data class WorkoutExerciseWithSets(
    @Embedded val workoutExercise: WorkoutExerciseEntity,
    @Relation(
        parentColumn = "exerciseId",
        entityColumn = "id"
    )
    val exercise: ExerciseEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "workoutExerciseId"
    )
    val sets: List<SetEntity>
)

data class SetWithDate(
    @Embedded val setEntity: SetEntity,
    val startedAt: Long
)
