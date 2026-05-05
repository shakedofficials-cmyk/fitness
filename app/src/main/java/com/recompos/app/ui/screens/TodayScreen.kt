package com.recompos.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.recompos.app.ui.AppViewModel
import com.recompos.app.ui.components.CoachCard
import com.recompos.app.ui.components.LineChart
import com.recompos.app.ui.theme.CoachColors

@Composable
fun TodayScreen(viewModel: AppViewModel, nav: NavHostController) {
    val dashboard by viewModel.dashboard.collectAsStateWithLifecycle()
    val exercises by viewModel.allExercises.collectAsStateWithLifecycle()
    val bodyweights by viewModel.bodyweights.collectAsStateWithLifecycle()
    val steps by viewModel.steps.collectAsStateWithLifecycle()
    val restTasks by viewModel.restTasks(dashboard.workout?.id ?: 0).collectAsStateWithLifecycle(initialValue = emptyList())
    val workoutExercises = exercises.filter { it.workoutTemplateId == dashboard.workout?.id }
    LazyColumn(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Week ${dashboard.week} Day ${dashboard.day}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
            Text(dashboard.phaseName, color = if (dashboard.isDeload) CoachColors.deload else MaterialTheme.colorScheme.primary)
        }
        if (dashboard.complete) {
            item { CoachCard("Block complete", "Review your trends, export your data, and start a new 12-week block from Settings.") }
        }
        if (dashboard.isDeload) {
            item { CoachCard("Deload week", "Reduce sets 40-50%, lighter loads, 3-4 RIR, no drop sets, no failure.") }
        }
        item {
            CoachCard(dashboard.coachTitle, dashboard.coachMessage)
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(dashboard.workout?.name ?: "No workout", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(dashboard.workout?.description ?: "Set your start date to begin.")
                    if (dashboard.workout?.isRestDay == false) {
                        Button(
                            onClick = {
                                dashboard.workout?.let { w ->
                                    viewModel.startWorkout(w, workoutExercises) { sessionId -> nav.navigate("active/$sessionId") }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(Modifier.height(1.dp))
                            Text("Start Workout")
                        }
                    }
                }
            }
        }
        item {
            CoachCard("Daily checklist") {
                if (restTasks.isNotEmpty()) {
                    restTasks.forEach { Text("- ${it.title}: ${it.description}") }
                } else {
                    Text("- 8,000 steps minimum")
                    Text("- Log food, sleep, digestion/reflux")
                    Text("- Shoulder rule: pain >3/10 means stop/swap")
                    Text("- No random junk volume")
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { viewModel.logSteps(8000) }, modifier = Modifier.weight(1f)) { Text("Log 8k steps") }
                Button(onClick = { viewModel.logSupplement(true, false, false, false) }, modifier = Modifier.weight(1f)) { Text("Creatine") }
            }
        }
        item { CoachCard("Bodyweight trend") { LineChart(bodyweights.map { it.weight }) } }
        item { CoachCard("Steps trend") { LineChart(steps.map { it.steps.toDouble() }) } }
    }
}
