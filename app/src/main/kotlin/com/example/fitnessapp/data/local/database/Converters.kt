package com.example.fitnessapp.data.local.database

import androidx.room.TypeConverter
import com.example.fitnessapp.domain.model.*

class Converters {
    @TypeConverter
    fun fromSex(value: Sex): String = value.name

    @TypeConverter
    fun toSex(value: String): Sex = Sex.valueOf(value)

    @TypeConverter
    fun fromTrainingGoal(value: TrainingGoal): String = value.name

    @TypeConverter
    fun toTrainingGoal(value: String): TrainingGoal = TrainingGoal.valueOf(value)

    @TypeConverter
    fun fromTrainingStatus(value: TrainingStatus): String = value.name

    @TypeConverter
    fun toTrainingStatus(value: String): TrainingStatus = TrainingStatus.valueOf(value)

    @TypeConverter
    fun fromPotentialLabel(value: PotentialLabel): String = value.name

    @TypeConverter
    fun toPotentialLabel(value: String): PotentialLabel = PotentialLabel.valueOf(value)

    @TypeConverter
    fun fromPredictionConfidence(value: PredictionConfidence): String = value.name

    @TypeConverter
    fun toPredictionConfidence(value: String): PredictionConfidence = PredictionConfidence.valueOf(value)
}
