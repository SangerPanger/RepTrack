package com.example.fitnessapp.ui.screens.progress

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitnessapp.ui.components.NeonCard
import com.example.fitnessapp.ui.components.ProgressLineChart
import com.example.fitnessapp.ui.components.StatCard
import com.example.fitnessapp.ui.theme.NeonCyan
import com.example.fitnessapp.ui.theme.SuccessGreen
import com.example.fitnessapp.ui.theme.NeonPurple

@Composable
fun ProgressScreen(viewModel: ProgressViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "PERFORMANCE",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        when (val state = uiState) {
            is ProgressUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NeonCyan)
                }
            }
            is ProgressUiState.Empty -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No workout data yet.\nStart a workout to track progress!",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
            is ProgressUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.exercises) { progress ->
                        ExerciseProgressCard(progress)
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseProgressCard(progress: ExerciseProgress) {
    NeonCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = progress.exercise.name.uppercase(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = NeonCyan
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            ProgressLineChart(
                points = progress.historyPoints,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    label = "EST. 1RM",
                    value = String.format("%.1f kg", progress.estimated1RM),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "4-WEEK PROJ.",
                    value = String.format("%+.1f kg", progress.projectedGain),
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Projected Strength: ${String.format("%.1f kg", progress.estimated1RM + progress.projectedGain)}",
                style = MaterialTheme.typography.bodySmall,
                color = if (progress.projectedGain >= 0) SuccessGreen else MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
