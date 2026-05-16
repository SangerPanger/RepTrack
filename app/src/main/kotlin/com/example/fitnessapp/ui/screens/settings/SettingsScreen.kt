package com.example.fitnessapp.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitnessapp.ui.components.NeonCard
import com.example.fitnessapp.ui.components.PrimaryNeonButton

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val userProfile by viewModel.userProfile.collectAsState()
    
    var age by remember(userProfile) { mutableStateOf(userProfile?.age?.toString() ?: "") }
    var gender by remember(userProfile) { mutableStateOf(userProfile?.gender ?: "") }
    var height by remember(userProfile) { mutableStateOf(userProfile?.height?.toString() ?: "") }
    var currentWeight by remember(userProfile) { mutableStateOf(userProfile?.currentWeight?.toString() ?: "") }
    var targetWeight by remember(userProfile) { mutableStateOf(userProfile?.targetWeight?.toString() ?: "") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "SETTINGS",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold
        )

        NeonCard {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "User Profile", fontWeight = FontWeight.Bold)
                
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text(text = "Gender", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Checkbox(
                            checked = gender.lowercase() == "male",
                            onCheckedChange = { if (it) gender = "Male" }
                        )
                        Text("Male")
                    }
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Checkbox(
                            checked = gender.lowercase() == "female",
                            onCheckedChange = { if (it) gender = "Female" }
                        )
                        Text("Female")
                    }
                }
                
                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it },
                    label = { Text("Height (cm)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = currentWeight,
                    onValueChange = { currentWeight = it },
                    label = { Text("Current Weight (kg)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = targetWeight,
                    onValueChange = { targetWeight = it },
                    label = { Text("Target Weight (kg)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                PrimaryNeonButton(
                    text = "Save Profile",
                    onClick = {
                        viewModel.saveProfile(
                            age = age.toIntOrNull() ?: 0,
                            gender = gender,
                            height = height.toDoubleOrNull() ?: 0.0,
                            currentWeight = currentWeight.toDoubleOrNull() ?: 0.0,
                            targetWeight = targetWeight.toDoubleOrNull() ?: 0.0
                        )
                    }
                )
            }
        }

        NeonCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Units")
                Text(text = "kg", color = MaterialTheme.colorScheme.primary)
            }
        }

        NeonCard {
            Text(text = "Theme")
            Text(text = "Neon Dark (Active)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
        }

        Spacer(modifier = Modifier.weight(1f))

        TextButton(
            onClick = { /* TODO: Clear Data */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Text("CLEAR ALL DATA")
        }
    }
}
