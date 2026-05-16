package com.example.fitnessapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    restTime: String? = null,
    isDrop: Boolean = false,
    isLastInGroup: Boolean = false
) {
    val scale = if (isDrop) 0.9f else 1.0f
    
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isDrop) {
                Text(
                    text = if (isLastInGroup) "  └─ " else "  ├─ ",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(32.dp)
            ) {
                Text(
                    text = setNumber.toString(),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = MaterialTheme.typography.bodyMedium.fontSize * scale)
                )
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size((24 * scale).dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Set",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                        modifier = Modifier.size((16 * scale).dp)
                    )
                }
            }
            
            OutlinedTextField(
                value = weight,
                onValueChange = onWeightChange,
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                label = { Text("kg", fontSize = MaterialTheme.typography.labelSmall.fontSize * scale) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = MaterialTheme.typography.bodyMedium.fontSize * scale),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = Color.Gray
                )
            )

            OutlinedTextField(
                value = reps,
                onValueChange = onRepsChange,
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                label = { Text("reps", fontSize = MaterialTheme.typography.labelSmall.fontSize * scale) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = MaterialTheme.typography.bodyMedium.fontSize * scale),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = Color.Gray
                )
            )

            Checkbox(
                checked = completed,
                onCheckedChange = onCompletedChange,
                modifier = Modifier.size((48 * scale).dp),
                colors = CheckboxDefaults.colors(
                    checkedColor = SuccessGreen,
                    uncheckedColor = Color.Gray
                )
            )
        }
        
        if (restTime != null) {
            Text(
                text = "Rest: $restTime",
                style = MaterialTheme.typography.labelSmall,
                color = NeonCyan.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 32.dp, bottom = 4.dp)
            )
        }
    }
}
