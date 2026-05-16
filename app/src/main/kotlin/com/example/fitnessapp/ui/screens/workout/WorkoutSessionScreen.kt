package com.example.fitnessapp.ui.screens.workout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.example.fitnessapp.ui.components.ExerciseSetRow
import com.example.fitnessapp.ui.components.NeonCard
import com.example.fitnessapp.ui.components.PrimaryNeonButton
import com.example.fitnessapp.ui.theme.NeonPurple
import com.example.fitnessapp.ui.theme.SuccessGreen
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutSessionScreen(
    viewModel: WorkoutSessionViewModel,
    onFinishWorkout: () -> Unit
) {
    val workout by viewModel.workout.collectAsState()
    val workoutExercises by viewModel.workoutExercises.collectAsState()
    val availableExercises by viewModel.availableExercises.collectAsState()
    val elapsedTime by viewModel.elapsedTime.collectAsState()
    val restTimers by viewModel.restTimers.collectAsState()
    var showAddExerciseDialog by remember { mutableStateOf(false) }
    var newExerciseName by remember { mutableStateOf("") }
    var isDropset by remember { mutableStateOf(false) }
    var startingWeight by remember { mutableStateOf("") }
    var weightDecrease by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    var showEditTitleDialog by remember { mutableStateOf(false) }
    var editedTitle by remember { mutableStateOf("") }

    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    var showEditDurationDialog by remember { mutableStateOf(false) }
    var editedDuration by remember { mutableStateOf("") }

    val hasChanges by viewModel.hasChanges.collectAsState()

    val filteredExercises = remember(newExerciseName, availableExercises) {
        if (newExerciseName.isEmpty()) {
            availableExercises
        } else {
            availableExercises.filter { it.name.contains(newExerciseName, ignoreCase = true) }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "WORKOUT DETAILS",
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                actions = {
                    IconButton(onClick = { showDeleteConfirmDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Workout", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        },
        bottomBar = {
            PrimaryNeonButton(
                text = if (workout?.finishedAt == null) "Finish Workout" else "Workout Finished",
                onClick = {
                    if (workout?.finishedAt == null) {
                        viewModel.finishWorkout(null)
                    }
                    onFinishWorkout()
                },
                modifier = Modifier.padding(16.dp),
                containerColor = if (workout?.finishedAt == null) {
                    NeonPurple
                } else if (hasChanges) {
                    SuccessGreen
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                workout?.let {
                    NeonCard {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Title Row
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = it.title.uppercase(),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                                IconButton(
                                    onClick = {
                                        editedTitle = it.title
                                        showEditTitleDialog = true
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Edit Title",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // Date Row
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val dateStr = SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault()).format(Date(it.startedAt))
                                Text(
                                    text = dateStr,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                IconButton(
                                    onClick = { showDatePicker = true },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Edit Date",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // Duration Row (if finished)
                            if (it.finishedAt != null) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    val durationMinutes = (it.finishedAt - it.startedAt) / (1000 * 60)
                                    Text(
                                        text = "Duration: $durationMinutes min",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                    IconButton(
                                        onClick = {
                                            editedDuration = durationMinutes.toString()
                                            showEditDurationDialog = true
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Edit Duration",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            } else {
                                // Live Timer Row
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Current Duration: $elapsedTime",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            items(workoutExercises) { exerciseWithSets ->
                NeonCard {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = (exerciseWithSets.exercise.name + (if (exerciseWithSets.workoutExercise.isDropset) " (DS)" else "")).uppercase(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Add-set",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                IconButton(onClick = { viewModel.addSet(exerciseWithSets.workoutExercise.id) }) {
                                    Icon(Icons.Default.Add, contentDescription = "Add Set", tint = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = { viewModel.deleteExercise(exerciseWithSets.workoutExercise.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove Exercise", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                        
                        exerciseWithSets.sets.forEachIndexed { index, set ->
                            val previousSetCompletedAt = if (index > 0) {
                                exerciseWithSets.sets[index - 1].completedAt
                            } else {
                                workout?.startedAt
                            }
                            
                            val restTimeStr = if (set.completedAt != null && previousSetCompletedAt != null) {
                                val restMs = set.completedAt - previousSetCompletedAt
                                val restSeconds = (restMs / 1000).coerceAtLeast(0)
                                val rSec = restSeconds % 60
                                val rMin = restSeconds / 60
                                String.format("%02d:%02d", rMin, rSec)
                            } else null

                            val isLastInGroup = index == exerciseWithSets.sets.size - 1 || !exerciseWithSets.sets[index + 1].isDrop

                            ExerciseSetRow(
                                setNumber = set.setNumber,
                                reps = set.reps.toString(),
                                weight = set.weight.toString(),
                                completed = set.completed,
                                onRepsChange = { 
                                    val reps = it.toIntOrNull() ?: 0
                                    viewModel.updateSet(set.copy(reps = reps))
                                },
                                onWeightChange = {
                                    val weight = it.toDoubleOrNull() ?: 0.0
                                    viewModel.updateSet(set.copy(weight = weight))
                                },
                                onCompletedChange = {
                                    viewModel.updateSet(set.copy(completed = it))
                                },
                                onDelete = {
                                    viewModel.deleteSet(set)
                                },
                                restTime = restTimeStr,
                                isDrop = set.isDrop,
                                isLastInGroup = isLastInGroup
                            )
                        }

                        if (exerciseWithSets.workoutExercise.isDropset && exerciseWithSets.sets.isNotEmpty()) {
                            TextButton(
                                onClick = { viewModel.addDrop(exerciseWithSets.workoutExercise.id) },
                                modifier = Modifier.align(Alignment.End).padding(end = 16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Add-drop", style = MaterialTheme.typography.labelLarge, color = NeonPurple)
                                    Icon(
                                        Icons.Default.KeyboardArrowDown, 
                                        contentDescription = null, 
                                        tint = NeonPurple,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        // Rest Timer Display
                        restTimers[exerciseWithSets.workoutExercise.id]?.let { restTime ->
                            if (workout?.finishedAt == null) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "REST: $restTime",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                OutlinedButton(
                    onClick = { showAddExerciseDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ADD EXERCISE")
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }

    if (showAddExerciseDialog) {
        AlertDialog(
            onDismissRequest = { 
                showAddExerciseDialog = false
                expanded = false
                isDropset = false
                startingWeight = ""
                weightDecrease = ""
            },
            title = { Text("Add Exercise") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box {
                        OutlinedTextField(
                            value = newExerciseName,
                            onValueChange = { 
                                newExerciseName = it
                                expanded = true
                            },
                            label = { Text("Exercise Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        if (expanded && filteredExercises.isNotEmpty()) {
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.fillMaxWidth(0.8f),
                                properties = PopupProperties(focusable = false)
                            ) {
                                filteredExercises.forEach { exercise ->
                                    DropdownMenuItem(
                                        text = { Text(exercise.name) },
                                        onClick = {
                                            newExerciseName = exercise.name
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = isDropset,
                            onCheckedChange = { isDropset = it }
                        )
                        Text("Dropset")
                    }

                    if (isDropset) {
                        OutlinedTextField(
                            value = startingWeight,
                            onValueChange = { startingWeight = it },
                            label = { Text("Starting Weight (kg/lbs)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                        OutlinedTextField(
                            value = weightDecrease,
                            onValueChange = { weightDecrease = it },
                            label = { Text("Weight Decrease per Drop") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (newExerciseName.isNotBlank()) {
                        viewModel.addExercise(
                            name = newExerciseName,
                            isDropset = isDropset,
                            initialWeight = startingWeight.toDoubleOrNull() ?: 0.0,
                            dropDecrease = weightDecrease.toDoubleOrNull() ?: 0.0
                        )
                        newExerciseName = ""
                        isDropset = false
                        startingWeight = ""
                        weightDecrease = ""
                        showAddExerciseDialog = false
                        expanded = false
                    }
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showAddExerciseDialog = false
                    expanded = false
                    isDropset = false
                    startingWeight = ""
                    weightDecrease = ""
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showEditTitleDialog) {
        AlertDialog(
            onDismissRequest = { showEditTitleDialog = false },
            title = { Text("Edit Workout Title") },
            text = {
                OutlinedTextField(
                    value = editedTitle,
                    onValueChange = { editedTitle = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateWorkoutTitle(editedTitle)
                        showEditTitleDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditTitleDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Delete Workout") },
            text = { Text("Are you sure you want to delete this workout? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteWorkout {
                            onFinishWorkout()
                        }
                        showDeleteConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            viewModel.updateWorkoutDate(it)
                        }
                        showDatePicker = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = SuccessGreen)
                ) {
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

    if (showEditDurationDialog) {
        AlertDialog(
            onDismissRequest = { showEditDurationDialog = false },
            title = { Text("Edit Duration") },
            text = {
                OutlinedTextField(
                    value = editedDuration,
                    onValueChange = { editedDuration = it.filter { char -> char.isDigit() } },
                    label = { Text("Duration (minutes)") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        editedDuration.toLongOrNull()?.let {
                            viewModel.updateWorkoutDuration(it)
                        }
                        showEditDurationDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDurationDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
