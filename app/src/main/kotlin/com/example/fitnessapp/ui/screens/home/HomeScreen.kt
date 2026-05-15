package com.example.fitnessapp.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitnessapp.ui.components.PrimaryNeonButton
import com.example.fitnessapp.ui.components.StatCard
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onWorkoutClick: (Long) -> Unit
) {
    val latestWorkout by viewModel.latestWorkout.collectAsState()
    val workoutCount by viewModel.workoutCount.collectAsState()
    val uniqueTitles by viewModel.uniqueWorkoutTitles.collectAsState()
    val scope = rememberCoroutineScope()

    var showStartDialog by remember { mutableStateOf(false) }
    var workoutTitle by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val filteredTitles = remember(workoutTitle, uniqueTitles) {
        if (workoutTitle.isEmpty()) {
            uniqueTitles
        } else {
            uniqueTitles.filter { it.contains(workoutTitle, ignoreCase = true) }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text(
                text = "DASHBOARD",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )
        }

        item {
            PrimaryNeonButton(
                text = "Start Workout",
                onClick = {
                    showStartDialog = true
                }
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    label = "TOTAL WORKOUTS",
                    value = workoutCount.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "LATEST PR",
                    value = "85 kg", // Placeholder
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (latestWorkout != null) {
            item {
                Text(
                    text = "LATEST WORKOUT",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                val workout = latestWorkout!!
                val dateStr = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(workout.startedAt))
                val duration = if (workout.finishedAt != null) {
                    val diff = (workout.finishedAt - workout.startedAt) / (1000 * 60)
                    " ($diff min)"
                } else ""
                
                StatCard(
                    label = "${workout.title} - $dateStr$duration",
                    value = if (workout.finishedAt != null) "Completed" else "In Progress",
                    modifier = Modifier.clickable { onWorkoutClick(workout.id) }
                )
            }
        }
    }

    if (showStartDialog) {
        AlertDialog(
            onDismissRequest = { 
                showStartDialog = false
                expanded = false
            },
            title = { Text("Start Workout") },
            text = {
                Box {
                    OutlinedTextField(
                        value = workoutTitle,
                        onValueChange = { 
                            workoutTitle = it
                            expanded = true
                        },
                        label = { Text("Workout Title (e.g. Chest Day)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    if (expanded && filteredTitles.isNotEmpty()) {
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            filteredTitles.forEach { title ->
                                DropdownMenuItem(
                                    text = { Text(title) },
                                    onClick = {
                                        workoutTitle = title
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (workoutTitle.isNotBlank()) {
                        scope.launch {
                            val id = viewModel.startNewWorkout(workoutTitle)
                            onWorkoutClick(id)
                            showStartDialog = false
                            workoutTitle = ""
                        }
                    }
                }) {
                    Text("Start")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showStartDialog = false
                    expanded = false
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}
