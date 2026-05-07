package com.recompos.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.recompos.app.ui.screens.*
import com.recompos.app.ui.theme.RecompTheme
import kotlinx.coroutines.flow.collectLatest

private data class Tab(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot(viewModel: AppViewModel) {
    val prefs by viewModel.preferences.collectAsStateWithLifecycle()
    RecompTheme(prefs.themeMode) {
        if (!prefs.onboardingComplete) {
            OnboardingScreen(viewModel)
            return@RecompTheme
        }
        val nav = rememberNavController()
        val snackbarHostState = remember { SnackbarHostState() }
        LaunchedEffect(Unit) {
            viewModel.uiEvent.collectLatest { message ->
                snackbarHostState.showSnackbar(message)
            }
        }
        val tabs = listOf(
            Tab("today", "Today", Icons.Default.CalendarToday),
            Tab("workout", "Workout", Icons.Default.FitnessCenter),
            Tab("log", "Log", Icons.Default.ShowChart),
            Tab("analytics", "Analytics", Icons.Default.Analytics),
            Tab("knowledge", "Knowledge", Icons.Default.Book)
        )
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = { Text("RecompOS") },
                    actions = { IconButton(onClick = { nav.navigate("settings") }) { Icon(Icons.Default.Settings, contentDescription = "Settings") } }
                )
            },
            bottomBar = {
                val backStack by nav.currentBackStackEntryAsState()
                val route = backStack?.destination?.route
                NavigationBar {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = route == tab.route,
                            onClick = { nav.navigate(tab.route) { launchSingleTop = true; popUpTo("today") } },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        ) { padding ->
            NavHost(nav, startDestination = "today", modifier = Modifier.padding(padding)) {
                composable("today") { TodayScreen(viewModel, nav) }
                composable("workout") { WorkoutScreen(viewModel, nav) }
                composable("log") { LogScreen(viewModel) }
                composable("analytics") { AnalyticsScreen(viewModel) }
                composable("knowledge") { KnowledgeScreen(viewModel) }
                composable("settings") { SettingsScreen(viewModel) }
                composable("exercise/{id}", arguments = listOf(navArgument("id") { type = NavType.IntType })) {
                    ExerciseDetailScreen(viewModel, it.arguments?.getInt("id") ?: 0)
                }
                composable("active/{id}", arguments = listOf(navArgument("id") { type = NavType.LongType })) {
                    ActiveWorkoutScreen(viewModel, it.arguments?.getLong("id") ?: 0L, nav)
                }
            }
        }
    }
}
