package com.example.fitnessapp.ui.screens.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitnessapp.ui.components.NeonCard
import com.example.fitnessapp.ui.theme.NeonCyan
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@Composable
fun WorkoutHistoryScreen(
    viewModel: WorkoutHistoryViewModel,
    onWorkoutClick: (Long) -> Unit
) {
    val workouts by viewModel.allWorkouts.collectAsState()
    val foodLogs by viewModel.allFoodLogs.collectAsState()
    
    var isFoodLogView by remember { mutableStateOf(false) }
    var editingFoodLog by remember { mutableStateOf<com.example.fitnessapp.data.local.entity.FoodLogEntity?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .draggable(
                state = rememberDraggableState { delta ->
                    if (delta < -20 && !isFoodLogView) isFoodLogView = true
                    if (delta > 20 && isFoodLogView) isFoodLogView = false
                },
                orientation = Orientation.Horizontal
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(bottom = 24.dp)) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "HISTORY",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isFoodLogView) "(Food-log)" else "(Workouts)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                Text(
                    text = if (isFoodLogView) "Swipe -> for Workouts" else "Swipe <- for Food-log",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (!isFoodLogView) {
                    items(workouts) { workoutWithExercises ->
                        WorkoutHistoryItem(
                            workoutWithExercises = workoutWithExercises, 
                            onClick = { onWorkoutClick(workoutWithExercises.workout.id) }
                        )
                    }
                } else {
                    items(foodLogs) { log ->
                        FoodLogHistoryItem(
                            log = log,
                            onClick = { editingFoodLog = log }
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    if (editingFoodLog != null) {
        EditFoodLogDialog(
            log = editingFoodLog!!,
            onDismiss = { editingFoodLog = null },
            onConfirm = { updatedLog ->
                viewModel.updateFoodLog(updatedLog)
                editingFoodLog = null
            },
            onDelete = {
                viewModel.deleteFoodLog(editingFoodLog!!)
                editingFoodLog = null
            }
        )
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun EditFoodLogDialog(
    log: com.example.fitnessapp.data.local.entity.FoodLogEntity,
    onDismiss: () -> Unit,
    onConfirm: (com.example.fitnessapp.data.local.entity.FoodLogEntity) -> Unit,
    onDelete: () -> Unit
) {
    var carbs by remember { mutableStateOf(log.carbs.toString()) }
    var fats by remember { mutableStateOf(log.fats.toString()) }
    var protein by remember { mutableStateOf(log.protein.toString()) }
    var calories by remember { mutableStateOf(log.calories.toString()) }
    var selectedDate by remember { mutableStateOf(log.date) }
    var showDatePicker by remember { mutableStateOf(false) }

    val dateStr = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(selectedDate))

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Daily Food Log") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "This will update the total for $dateStr",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                
                if (showDatePicker) {
                    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                selectedDate = datePickerState.selectedDateMillis ?: selectedDate
                                showDatePicker = false
                            }) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) {
                                Text("Cancel")
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }

                OutlinedTextField(value = carbs, onValueChange = { carbs = it }, label = { Text("Carbs") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = fats, onValueChange = { fats = it }, label = { Text("Fats") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = protein, onValueChange = { protein = it }, label = { Text("Protein") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = calories, onValueChange = { calories = it }, label = { Text("Calories") }, modifier = Modifier.fillMaxWidth())
                
                TextButton(
                    onClick = {
                        val c = carbs.toDoubleOrNull() ?: 0.0
                        val f = fats.toDoubleOrNull() ?: 0.0
                        val p = protein.toDoubleOrNull() ?: 0.0
                        val cal = (p * 4) + (c * 4) + (f * 8)
                        calories = cal.toString()
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Auto calculate calories")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(log.copy(
                    carbs = carbs.toDoubleOrNull() ?: 0.0,
                    fats = fats.toDoubleOrNull() ?: 0.0,
                    protein = protein.toDoubleOrNull() ?: 0.0,
                    calories = calories.toDoubleOrNull() ?: 0.0,
                    date = selectedDate
                ))
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onDelete, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                    Text("Delete")
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}

@Composable
fun FoodLogHistoryItem(
    log: com.example.fitnessapp.data.local.entity.FoodLogEntity,
    onClick: () -> Unit
) {
    val dateStr = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(log.date))
    
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
                    text = dateStr,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan
                )
                Text(
                    text = "${log.calories.roundToInt()} kcal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "P: ${log.protein.roundToInt()}g | C: ${log.carbs.roundToInt()}g | F: ${log.fats.roundToInt()}g",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
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

    val totalVolume = workoutWithExercises.workoutExercises.sumOf { exerciseWithSets ->
        exerciseWithSets.sets.filter { it.completed }.sumOf { set ->
            if (set.isDrop) {
                val weight = set.weight
                val startingWeight = exerciseWithSets.workoutExercise.startingWeight
                if (startingWeight > 0) {
                    ((weight / startingWeight) / 2.0) * startingWeight * set.reps
                } else {
                    (weight / 2.0) * set.reps
                }
            } else {
                set.reps * set.weight
            }
        }
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
