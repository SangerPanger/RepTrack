package com.example.fitnessapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.fitnessapp.ui.theme.NeonCyan

data class NavigationItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        NavigationItem("Home", Icons.Default.Home, "home"),
        NavigationItem("History", Icons.Default.List, "history"),
        NavigationItem("Progress", Icons.Default.Info, "progress"),
        NavigationItem("Settings", Icons.Default.Settings, "settings")
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = NeonCyan
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NeonCyan,
                    selectedTextColor = NeonCyan,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    indicatorColor = NeonCyan.copy(alpha = 0.1f)
                )
            )
        }
    }
}
