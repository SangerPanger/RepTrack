package com.example.fitnessapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.fitnessapp.ui.theme.NeonCyan
import com.example.fitnessapp.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseSetRow(
    setNumber: Int,
    reps: String,
    weight: String,
    completed: Boolean,
    onRepsChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onCompletedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = setNumber.toString(),
            modifier = Modifier.width(32.dp),
            style = MaterialTheme.typography.bodyMedium
        )
        
        OutlinedTextField(
            value = weight,
            onValueChange = onWeightChange,
            modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
            label = { Text("kg") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = Color.Gray
            )
        )

        OutlinedTextField(
            value = reps,
            onValueChange = onRepsChange,
            modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
            label = { Text("reps") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = Color.Gray
            )
        )

        Checkbox(
            checked = completed,
            onCheckedChange = onCompletedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = SuccessGreen,
                uncheckedColor = Color.Gray
            )
        )
    }
}
