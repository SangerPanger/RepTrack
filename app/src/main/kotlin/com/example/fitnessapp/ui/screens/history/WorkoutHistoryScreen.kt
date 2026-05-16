package com.example.fitnessapp.ui.screens.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitnessapp.ui.components.NeonCard
import com.example.fitnessapp.ui.theme.NeonCyan
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WorkoutHistoryScreen(
    viewModel: WorkoutHistoryViewModel,
    onWorkoutClick: (Long) -> Unit
) {
    val workouts by viewModel.allWorkouts.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "WORKOUT HISTORY",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(workouts) { workoutWithExercises ->
                WorkoutHistoryItem(
                    workoutWithExercises = workoutWithExercises, 
                    onClick = { onWorkoutClick(workoutWithExercises.workout.id) }
                )
            }
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun WorkoutHistoryItem(
    workoutWithExercises: com.example.fitnessapp.data.local.entity.WorkoutWithExercises,
    onClick: () -> Unit
) {
    val workout = workoutWithExercises.workout
    val dateStr = SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault()).format(Date(workout.startedAt))
    val duration = if (workout.finishedAt != null) {
        val diff = workout.manualDurationMinutes ?: (((workout.finishedAt - workout.startedAt) - workout.durationOffsetMs) / (1000 * 60))
        " ($diff min)"
    } else " (In Progress)"

    val totalVolume = workoutWithExercises.workoutExercises.sumOf { exercise ->
        exercise.sets.filter { it.completed }.sumOf { it.reps * it.weight }
    }

    NeonCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${workout.title.uppercase()}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan
                )
                if (totalVolume > 0) {
                    Text(
                        text = "${String.format("%.1f", totalVolume)} kg",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$dateStr$duration",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}
