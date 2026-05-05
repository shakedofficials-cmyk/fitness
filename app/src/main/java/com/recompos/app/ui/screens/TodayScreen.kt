package com.recompos.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.recompos.app.ui.AppViewModel
import com.recompos.app.ui.components.ActionTile
import com.recompos.app.ui.components.CheckLine
import com.recompos.app.ui.components.CoachCard
import com.recompos.app.ui.components.HeroPanel
import com.recompos.app.ui.components.LineChart
import com.recompos.app.ui.components.MetricPill
import com.recompos.app.ui.components.ScreenHeader
import com.recompos.app.ui.theme.CoachColors

@Composable
fun TodayScreen(viewModel: AppViewModel, nav: NavHostController) {
    val dashboard by viewModel.dashboard.collectAsStateWithLifecycle()
    val exercises by viewModel.allExercises.collectAsStateWithLifecycle()
    val bodyweights by viewModel.bodyweights.collectAsStateWithLifecycle()
    val steps by viewModel.steps.collectAsStateWithLifecycle()
    val restTasks by viewModel.restTasks(dashboard.workout?.id ?: 0).collectAsStateWithLifecycle(initialValue = emptyList())
    val workoutExercises = exercises.filter { it.workoutTemplateId == dashboard.workout?.id }
    val latestWeight = bodyweights.lastOrNull()?.weight?.let { "%.1f".format(it) } ?: "--"
    val latestSteps = steps.lastOrNull()?.steps?.toString() ?: "--"
    val blockProgress = ((dashboard.week - 1) * 7 + dashboard.day).coerceIn(1, 84)
    var quickLog by remember { mutableStateOf<QuickLogType?>(null) }

    LazyColumn(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            ScreenHeader(
                title = "Today",
                subtitle = "Week ${dashboard.week}, Day ${dashboard.day} of the 12-week block",
                trailing = "$blockProgress/84"
            )
        }
        if (dashboard.complete) {
            item { CoachCard("Block complete", "Review your trends, export your data, and start a new 12-week block from Settings.") }
        }
        if (dashboard.isDeload) {
            item { CoachCard("Deload week", "Reduce sets 40-50%, lighter loads, 3-4 RIR, no drop sets, no failure.") }
        }
        item {
            HeroPanel(
                eyebrow = dashboard.phaseName,
                title = dashboard.workout?.name ?: "Plan ready",
                body = dashboard.workout?.description ?: "Set up your plan and begin the block.",
                accent = if (dashboard.isDeload) CoachColors.deload else MaterialTheme.colorScheme.primary
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    MetricPill("Weight", latestWeight, Modifier.weight(1f))
                    MetricPill("Steps", latestSteps, Modifier.weight(1f))
                    MetricPill("Mode", if (dashboard.isDeload) "Deload" else "Build", Modifier.weight(1f))
                }
                if (dashboard.workout?.isRestDay == false) {
                    Button(
                        onClick = {
                            dashboard.workout?.let { workout ->
                                viewModel.startWorkout(workout, workoutExercises) { sessionId -> nav.navigate("active/$sessionId") }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Text("Start today's workout")
                    }
                } else {
                    OutlinedButton(onClick = { quickLog = QuickLogType.Steps }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.DirectionsWalk, contentDescription = null)
                        Text("Log recovery work")
                    }
                }
            }
        }
        item {
            CoachCard(dashboard.coachTitle, dashboard.coachMessage) {
                Text("One job at a time: train clean, log honestly, recover like it counts.")
            }
        }
        item {
            CoachCard("Today's checklist") {
                if (restTasks.isNotEmpty()) {
                    restTasks.forEach { CheckLine("${it.title}: ${it.description}") }
                } else {
                    CheckLine("8,000 steps minimum")
                    CheckLine("Log food, sleep, digestion/reflux")
                    CheckLine("Shoulder rule: pain >3/10 means stop/swap")
                    CheckLine("No random junk volume")
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                ActionTile("Steps", "Quick add today's walk", Icons.Default.DirectionsWalk, Modifier.weight(1f), onClick = { quickLog = QuickLogType.Steps })
                ActionTile("Weight", "Morning check-in", Icons.Default.Add, Modifier.weight(1f), onClick = { quickLog = QuickLogType.Weight })
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                ActionTile("Macros", "Calories and protein", Icons.Default.LocalFireDepartment, Modifier.weight(1f), onClick = { quickLog = QuickLogType.Nutrition })
                ActionTile("Creatine", "Mark 5 g taken", Icons.Default.Medication, Modifier.weight(1f), onClick = { viewModel.logSupplement(true, false, false, false) })
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                ActionTile("Sleep", "Recovery score", Icons.Default.Bedtime, Modifier.weight(1f), onClick = { quickLog = QuickLogType.Sleep })
                ActionTile("Workout", "Open full plan", Icons.Default.FitnessCenter, Modifier.weight(1f), onClick = { nav.navigate("workout") })
            }
        }
        item { CoachCard("Bodyweight trend") { LineChart(bodyweights.map { it.weight }) } }
        item { CoachCard("Steps trend") { LineChart(steps.map { it.steps.toDouble() }) } }
    }

    quickLog?.let { type ->
        QuickLogDialog(
            type = type,
            onDismiss = { quickLog = null },
            onSave = { first, second, third, fourth ->
                when (type) {
                    QuickLogType.Weight -> first.toDoubleOrNull()?.let(viewModel::logBodyweight)
                    QuickLogType.Steps -> first.toIntOrNull()?.let(viewModel::logSteps)
                    QuickLogType.Nutrition -> viewModel.logNutrition(first.toIntOrNull() ?: 0, second.toIntOrNull() ?: 190, third.toIntOrNull() ?: 0, fourth.toIntOrNull() ?: 0)
                    QuickLogType.Sleep -> viewModel.logSleep(first.toDoubleOrNull() ?: 0.0, second.toIntOrNull() ?: 7)
                }
                quickLog = null
            }
        )
    }
}

private enum class QuickLogType { Weight, Steps, Nutrition, Sleep }

@Composable
private fun QuickLogDialog(type: QuickLogType, onDismiss: () -> Unit, onSave: (String, String, String, String) -> Unit) {
    var first by remember { mutableStateOf(if (type == QuickLogType.Steps) "8000" else "") }
    var second by remember { mutableStateOf(if (type == QuickLogType.Nutrition) "190" else if (type == QuickLogType.Sleep) "7" else "") }
    var third by remember { mutableStateOf("") }
    var fourth by remember { mutableStateOf("") }
    val title = when (type) {
        QuickLogType.Weight -> "Log bodyweight"
        QuickLogType.Steps -> "Log steps"
        QuickLogType.Nutrition -> "Log macros"
        QuickLogType.Sleep -> "Log sleep"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(first, { first = it }, label = { Text(firstLabel(type)) }, modifier = Modifier.fillMaxWidth())
                if (type == QuickLogType.Nutrition || type == QuickLogType.Sleep) {
                    OutlinedTextField(second, { second = it }, label = { Text(if (type == QuickLogType.Sleep) "Quality 1-10" else "Protein") }, modifier = Modifier.fillMaxWidth())
                }
                if (type == QuickLogType.Nutrition) {
                    OutlinedTextField(third, { third = it }, label = { Text("Carbs") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(fourth, { fourth = it }, label = { Text("Fat") }, modifier = Modifier.fillMaxWidth())
                }
            }
        },
        confirmButton = { Button(onClick = { onSave(first, second, third, fourth) }) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

private fun firstLabel(type: QuickLogType) = when (type) {
    QuickLogType.Weight -> "Bodyweight"
    QuickLogType.Steps -> "Steps"
    QuickLogType.Nutrition -> "Calories"
    QuickLogType.Sleep -> "Hours"
}
