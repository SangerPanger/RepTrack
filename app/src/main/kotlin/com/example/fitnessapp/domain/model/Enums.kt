package com.example.fitnessapp.domain.model

enum class Sex {
    MALE, FEMALE
}

enum class TrainingGoal {
    CUT, MAINTAIN, LEAN_BULK, BULK, UNKNOWN
}

enum class TrainingStatus {
    NEWBIE, NOVICE, INTERMEDIATE, ADVANCED, RETURNING
}

enum class PotentialLabel {
    LOW, MODERATE, GOOD, HIGH
}

enum class PredictionConfidence {
    LOW, MEDIUM, HIGH
}
