package com.example.fitnessapp.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitnessapp.data.local.entity.UserProfileEntity
import com.example.fitnessapp.domain.model.Sex
import com.example.fitnessapp.domain.model.TrainingGoal
import com.example.fitnessapp.ui.components.NeonCard
import com.example.fitnessapp.ui.components.PrimaryNeonButton
import java.util.*

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val userProfile by viewModel.userProfile.collectAsState()
    
    var age by remember(userProfile) { mutableStateOf(userProfile?.age?.toString() ?: "") }
    var sex by remember(userProfile) { mutableStateOf(userProfile?.sex ?: Sex.MALE) }
    var heightCm by remember(userProfile) { mutableStateOf(userProfile?.heightCm?.toString() ?: "") }
    var bodyWeightKg by remember(userProfile) { mutableStateOf(userProfile?.bodyWeightKg?.toString() ?: "") }
    
    var trainingExperienceMonths by remember(userProfile) { mutableStateOf(userProfile?.trainingExperienceMonths?.toString() ?: "") }
    var detrainingWeeks by remember(userProfile) { mutableStateOf(userProfile?.detrainingWeeks?.toString() ?: "") }
    var isReturningLifter by remember(userProfile) { mutableStateOf(userProfile?.isReturningLifter ?: false) }
    
    var averageProteinGramsPerDay by remember(userProfile) { mutableStateOf(userProfile?.averageProteinGramsPerDay?.toString() ?: "") }
    var averageCaloriesPerDay by remember(userProfile) { mutableStateOf(userProfile?.averageCaloriesPerDay?.toString() ?: "") }
    var estimatedTdee by remember(userProfile) { mutableStateOf(userProfile?.estimatedTdee?.toString() ?: "") }
    var fatGramsPerDay by remember(userProfile) { mutableStateOf(userProfile?.fatGramsPerDay?.toString() ?: "") }
    var fatPercentCalories by remember(userProfile) { mutableStateOf(userProfile?.fatPercentCalories?.toString() ?: "") }
    var goal by remember(userProfile) { mutableStateOf(userProfile?.goal ?: TrainingGoal.UNKNOWN) }
    var useSettingsForNutrition by remember(userProfile) { mutableStateOf(userProfile?.useSettingsForNutrition ?: true) }

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
                Text(text = "Personal Profile", fontWeight = FontWeight.Bold)
                
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text(text = "Sex", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Sex.values().forEach { s ->
                        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            RadioButton(
                                selected = sex == s,
                                onClick = { sex = s }
                            )
                            Text(s.name.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() })
                        }
                    }
                }
                
                OutlinedTextField(
                    value = heightCm,
                    onValueChange = { heightCm = it },
                    label = { Text("Height (cm)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = bodyWeightKg,
                    onValueChange = { bodyWeightKg = it },
                    label = { Text("Current Weight (kg)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        NeonCard {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Training Profile", fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = trainingExperienceMonths,
                    onValueChange = { trainingExperienceMonths = it },
                    label = { Text("Training Experience (Months)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Checkbox(
                        checked = isReturningLifter,
                        onCheckedChange = { isReturningLifter = it }
                    )
                    Text("Are you a returning lifter?")
                }

                if (isReturningLifter) {
                    OutlinedTextField(
                        value = detrainingWeeks,
                        onValueChange = { detrainingWeeks = it },
                        label = { Text("Detraining Weeks") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        NeonCard {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Nutrition & Goal", fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Checkbox(
                            checked = useSettingsForNutrition,
                            onCheckedChange = { useSettingsForNutrition = it }
                        )
                        Text("don't use food logs", style = MaterialTheme.typography.labelSmall)
                    }
                }

                if (useSettingsForNutrition) {
                    OutlinedTextField(
                        value = averageCaloriesPerDay,
                        onValueChange = { averageCaloriesPerDay = it },
                        label = { Text("Average Calories/Day") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = estimatedTdee,
                        onValueChange = { estimatedTdee = it },
                        label = { Text("Estimated TDEE") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = averageProteinGramsPerDay,
                        onValueChange = { averageProteinGramsPerDay = it },
                        label = { Text("Average Protein (g/day)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = fatGramsPerDay,
                        onValueChange = { fatGramsPerDay = it },
                        label = { Text("Average Fat (g/day)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(text = "Training Goal", style = MaterialTheme.typography.labelMedium)
                    // Simplified goal selection
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TrainingGoal.values().filter { it != TrainingGoal.UNKNOWN }.forEach { g ->
                            FilterChip(
                                selected = goal == g,
                                onClick = { goal = g },
                                label = { Text(g.name) }
                            )
                        }
                    }

                    Button(
                        onClick = {
                            val a = age.toIntOrNull() ?: 30
                            val h = heightCm.toDoubleOrNull() ?: 175.0
                            val w = bodyWeightKg.toDoubleOrNull() ?: 70.0
                            val res = viewModel.calculateNutrition(a, sex, h, w, goal)
                            
                            averageCaloriesPerDay = "%.0f".format(res.calories)
                            estimatedTdee = "%.0f".format(res.tdee)
                            averageProteinGramsPerDay = "%.1f".format(res.protein)
                            fatGramsPerDay = "%.1f".format(res.fat)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Calculate for me")
                    }
                } else {
                    Text(
                        text = "Using data from food logs for progress estimation. Macros and calories from settings are ignored.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }

        PrimaryNeonButton(
            text = "Save All Settings",
            onClick = {
                viewModel.saveProfile(
                    UserProfileEntity(
                        age = age.toIntOrNull() ?: 0,
                        sex = sex,
                        heightCm = heightCm.toDoubleOrNull() ?: 0.0,
                        bodyWeightKg = bodyWeightKg.toDoubleOrNull() ?: 0.0,
                        trainingExperienceMonths = trainingExperienceMonths.toIntOrNull() ?: 0,
                        detrainingWeeks = detrainingWeeks.toIntOrNull() ?: 0,
                        isReturningLifter = isReturningLifter,
                        averageProteinGramsPerDay = averageProteinGramsPerDay.toDoubleOrNull() ?: 0.0,
                        averageCaloriesPerDay = averageCaloriesPerDay.toDoubleOrNull() ?: 0.0,
                        estimatedTdee = estimatedTdee.toDoubleOrNull() ?: 0.0,
                        fatGramsPerDay = fatGramsPerDay.toDoubleOrNull() ?: 0.0,
                        fatPercentCalories = fatPercentCalories.toDoubleOrNull() ?: 0.0,
                        goal = goal,
                        useSettingsForNutrition = useSettingsForNutrition
                    )
                )
            }
        )

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
