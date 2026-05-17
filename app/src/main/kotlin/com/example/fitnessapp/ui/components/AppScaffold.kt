package com.example.fitnessapp.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable

@Composable
fun AppScaffold(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        bottomBar = {
            if (currentRoute != null && !currentRoute.startsWith("workout_session")) {
                BottomNavigationBar(currentRoute = currentRoute, onNavigate = onNavigate)
            }
        },
        content = content
    )
}
