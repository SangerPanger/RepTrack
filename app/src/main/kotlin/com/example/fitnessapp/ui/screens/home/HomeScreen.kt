package com.example.fitnessapp.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitnessapp.ui.components.PrimaryNeonButton
import com.example.fitnessapp.ui.components.StatCard
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onStartWorkout: (Long) -> Unit
) {
    val latestWorkout by viewModel.latestWorkout.collectAsState()
    val workoutCount by viewModel.workoutCount.collectAsState()
    val scope = rememberCoroutineScope()

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
                    scope.launch {
                        val id = viewModel.startNewWorkout()
                        onStartWorkout(id)
                    }
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
                StatCard(
                    label = latestWorkout?.title ?: "Workout",
                    value = "Completed"
                )
            }
        }
    }
}
