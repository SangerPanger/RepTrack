package com.example.fitnessapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.fitnessapp.FitnessApp
import com.example.fitnessapp.ui.components.AppScaffold
import com.example.fitnessapp.ui.screens.history.WorkoutHistoryScreen
import com.example.fitnessapp.ui.screens.history.WorkoutHistoryViewModel
import com.example.fitnessapp.ui.screens.home.HomeScreen
import com.example.fitnessapp.ui.screens.home.HomeViewModel
import com.example.fitnessapp.ui.screens.progress.ProgressScreen
import com.example.fitnessapp.ui.screens.progress.ProgressViewModel
import com.example.fitnessapp.ui.screens.settings.SettingsScreen
import com.example.fitnessapp.ui.screens.settings.SettingsViewModel
import com.example.fitnessapp.ui.screens.workout.WorkoutSessionScreen
import com.example.fitnessapp.ui.screens.workout.WorkoutSessionViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = "home"
) {
    val context = LocalContext.current
    val app = context.applicationContext as FitnessApp
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    AppScaffold(
        currentRoute = currentRoute,
        onNavigate = { route ->
            navController.navigate(route) {
                popUpTo("home") { inclusive = false }
                launchSingleTop = true
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(padding)
        ) {
            composable("home") {
                val viewModel: HomeViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return HomeViewModel(app.workoutRepository, app.foodRepository) as T
                    }
                })
                HomeScreen(
                    viewModel = viewModel,
                    onWorkoutClick = { id ->
                        navController.navigate("workout_session/$id") {
                            // Don't popUpTo home if we want to be able to go back, 
                            // but usually starting a workout should be a clean state.
                            // However, we MUST NOT use launchSingleTop if it causes issues with rapid starts.
                        }
                    }
                )
            }
            
            composable(
                route = "workout_session/{workoutId}",
                arguments = listOf(navArgument("workoutId") { type = NavType.LongType })
            ) { backStackEntry ->
                val workoutId = backStackEntry.arguments?.getLong("workoutId") ?: 0L
                val viewModel: WorkoutSessionViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return WorkoutSessionViewModel(workoutId, app.workoutRepository, app.exerciseRepository) as T
                    }
                })
                WorkoutSessionScreen(
                    viewModel = viewModel,
                    onFinishWorkout = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
            }

            composable("history") {
                val viewModel: WorkoutHistoryViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return WorkoutHistoryViewModel(app.workoutRepository, app.foodRepository) as T
                    }
                })
                WorkoutHistoryScreen(
                    viewModel = viewModel,
                    onWorkoutClick = { id ->
                        navController.navigate("workout_session/$id")
                    }
                )
            }

            composable("progress") {
                val viewModel: ProgressViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return ProgressViewModel(app.progressRepository, app.exerciseRepository, app.foodRepository, app.workoutRepository) as T
                    }
                })
                ProgressScreen(viewModel = viewModel)
            }
            
            composable("settings") {
                val viewModel: SettingsViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return SettingsViewModel(app.foodRepository) as T
                    }
                })
                SettingsScreen(viewModel = viewModel)
            }
        }
    }
}
