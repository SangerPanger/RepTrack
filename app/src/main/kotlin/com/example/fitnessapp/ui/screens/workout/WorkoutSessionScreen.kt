package com.example.fitnessapp.ui.screens.workout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitnessapp.ui.components.ExerciseSetRow
import com.example.fitnessapp.ui.components.NeonCard
import com.example.fitnessapp.ui.components.PrimaryNeonButton
import com.example.fitnessapp.ui.theme.NeonPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutSessionScreen(
    viewModel: WorkoutSessionViewModel,
    onFinishWorkout: () -> Unit
) {
    val workoutExercises by viewModel.workoutExercises.collectAsState()
    var showAddExerciseDialog by remember { mutableStateOf(false) }
    var newExerciseName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("WORKOUT SESSION", fontWeight = FontWeight.ExtraBold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            PrimaryNeonButton(
                text = "Finish Workout",
                onClick = {
                    viewModel.finishWorkout(null)
                    onFinishWorkout()
                },
                modifier = Modifier.padding(16.dp),
                containerColor = NeonPurple
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
            items(workoutExercises) { exerciseWithSets ->
                NeonCard {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = exerciseWithSets.exercise.name.uppercase(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { viewModel.addSet(exerciseWithSets.workoutExercise.id) }) {
                                Icon(Icons.Default.Add, contentDescription = "Add Set", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                        
                        exerciseWithSets.sets.forEach { set ->
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
                                }
                            )
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
            onDismissRequest = { showAddExerciseDialog = false },
            title = { Text("Add Exercise") },
            text = {
                OutlinedTextField(
                    value = newExerciseName,
                    onValueChange = { newExerciseName = it },
                    label = { Text("Exercise Name") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (newExerciseName.isNotBlank()) {
                        viewModel.addExercise(newExerciseName)
                        newExerciseName = ""
                        showAddExerciseDialog = false
                    }
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddExerciseDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
