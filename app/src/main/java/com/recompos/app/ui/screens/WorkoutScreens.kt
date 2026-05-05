package com.recompos.app.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.recompos.app.data.local.ExerciseSessionEntity
import com.recompos.app.data.local.ExerciseTemplateEntity
import com.recompos.app.domain.isCompound
import com.recompos.app.ui.AppViewModel
import com.recompos.app.ui.components.CoachCard
import com.recompos.app.ui.components.StatRow
import kotlinx.coroutines.delay

@Composable
fun WorkoutScreen(viewModel: AppViewModel, nav: NavHostController) {
    val dashboard by viewModel.dashboard.collectAsStateWithLifecycle()
    val workouts by viewModel.workouts.collectAsStateWithLifecycle()
    val exercises by viewModel.allExercises.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Workout", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black) }
        item {
            CoachCard("Today: ${dashboard.workout?.name ?: "Rest"}", dashboard.workout?.description) {
                if (dashboard.workout?.isRestDay == false) {
                    Button(onClick = {
                        dashboard.workout?.let { w -> viewModel.startWorkout(w, exercises.filter { it.workoutTemplateId == w.id }) { nav.navigate("active/$it") } }
                    }, modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) { Text("Start active session") }
                }
            }
        }
        items(workouts) { workout ->
            CoachCard("Day ${workout.dayNumber}: ${workout.name}", workout.description) {
                exercises.filter { it.workoutTemplateId == workout.id }.take(4).forEach {
                    Text("${it.orderIndex}. ${it.name} - ${it.sets} x ${it.minReps}-${it.maxReps}")
                }
            }
        }
        item { Text("Exercise Library", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
        items(exercises) { exercise ->
            CoachCard(exercise.name, "${exercise.targetMuscle} | ${exercise.sets} x ${exercise.minReps}-${exercise.maxReps}") {
                Text("Tap for cues and progression.", modifier = Modifier.clickable { nav.navigate("exercise/${exercise.id}") })
            }
        }
        item { Text("Workout History", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
        if (history.isEmpty()) item { CoachCard("No completed sessions yet", "Start a workout and finish it to build your logbook.") }
        items(history) { session ->
            CoachCard("Week ${session.weekNumber} Day ${session.dayNumber}", "Status: ${session.status}") {
                Text("Started: ${session.startedAtMillis}")
            }
        }
    }
}

@Composable
fun ExerciseDetailScreen(viewModel: AppViewModel, exerciseId: Int) {
    val exercises by viewModel.allExercises.collectAsStateWithLifecycle()
    val exercise = exercises.firstOrNull { it.id == exerciseId }
    if (exercise == null) {
        Text("Exercise not found", Modifier.padding(16.dp))
        return
    }
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text(exercise.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black) }
        item {
            CoachCard("Prescription") {
                StatRow("Target", exercise.targetMuscle)
                StatRow("Sets", exercise.sets.toString())
                StatRow("Reps", "${exercise.minReps}-${exercise.maxReps}")
                StatRow("Rest", "${exercise.restSecondsMin}-${exercise.restSecondsMax}s")
                StatRow("RIR", exercise.rirTarget)
            }
        }
        item { CoachCard("Cues", exercise.cues) }
        item { CoachCard("Shoulder / safety", exercise.cautions.ifBlank { "Use clean reps. No ugly ego lifting." }) }
        item { CoachCard("Alternatives", exercise.alternatives.ifBlank { "No programmed alternative." }) }
        item { CoachCard("Progression", "Double progression: hit the top of the rep range for all working sets at target RIR before increasing load.") }
    }
}

@Composable
fun ActiveWorkoutScreen(viewModel: AppViewModel, sessionId: Long, nav: NavHostController) {
    val sessions by viewModel.exerciseSessions(sessionId).collectAsStateWithLifecycle(initialValue = emptyList())
    val templates by viewModel.allExercises.collectAsStateWithLifecycle()
    var selectedIndex by remember { mutableIntStateOf(0) }
    val current = sessions.getOrNull(selectedIndex)
    val currentTemplate = templates.firstOrNull { it.id == current?.exerciseTemplateId }
    var painDialog by remember { mutableStateOf(false) }
    var restSeconds by remember { mutableIntStateOf(0) }
    LaunchedEffect(restSeconds) {
        if (restSeconds > 0) {
            delay(1000)
            restSeconds -= 1
        }
    }
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Active Workout", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
            LinearProgressIndicator(progress = { if (sessions.isEmpty()) 0f else (selectedIndex + 1f) / sessions.size }, modifier = Modifier.fillMaxWidth())
        }
        item {
            CoachCard("Shoulder warm-up checklist") {
                Text("- Cable external rotation: 2 x 15 per side")
                Text("- Scapular push-ups: 2 x 10")
                Text("- Light lateral raises: 2 x 20")
                Text("- First press gets 2-3 ramp-up sets")
            }
        }
        if (currentTemplate != null && current != null) {
            item {
                val prescription = viewModel.deloadCalculator.prescription(currentTemplate, viewModel.dashboard.value.week)
                CoachCard(currentTemplate.name, "${currentTemplate.targetMuscle} | ${currentTemplate.sets} x ${currentTemplate.minReps}-${currentTemplate.maxReps}") {
                    if (prescription.deload) Text("Deload prescription: ${prescription.sets} sets at ${prescription.rir} RIR. No drop sets or failure.")
                    Text(currentTemplate.cues)
                    if (restSeconds > 0) Text("Rest timer: ${restSeconds}s", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
            item { SetLogger(viewModel, current, currentTemplate, onPain = { painDialog = true }, onSaved = { restSeconds = currentTemplate.restSecondsMin }) }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { if (selectedIndex > 0) selectedIndex-- }, modifier = Modifier.weight(1f)) { Text("Previous") }
                    Button(onClick = { if (selectedIndex < sessions.lastIndex) selectedIndex++ }, modifier = Modifier.weight(1f)) { Text("Next") }
                }
            }
            item {
                OutlinedButton(onClick = { viewModel.skipExercise(current.id, "Skipped in session") }, modifier = Modifier.fillMaxWidth()) { Text("Skip exercise") }
            }
        } else {
            item { CoachCard("Loading session", "Exercise sessions are being created.") }
        }
        item {
            Button(onClick = { viewModel.finishWorkout(sessionId); nav.navigate("workout") }, modifier = Modifier.fillMaxWidth()) {
                Text("Finish Workout")
            }
        }
    }
    if (painDialog) {
        AlertDialog(
            onDismissRequest = { painDialog = false },
            title = { Text("Coach warning") },
            text = { Text("If pain is above 3/10, stop and swap. Do not log true failure on compounds or use drop sets where they are not programmed.") },
            confirmButton = { TextButton(onClick = { painDialog = false }) { Text("Understood") } }
        )
    }
}

@Composable
private fun SetLogger(
    viewModel: AppViewModel,
    session: ExerciseSessionEntity,
    template: ExerciseTemplateEntity,
    onPain: () -> Unit,
    onSaved: () -> Unit
) {
    var setNumber by remember(session.id) { mutableStateOf("1") }
    var weight by remember(session.id) { mutableStateOf("") }
    var reps by remember(session.id) { mutableStateOf("") }
    var rir by remember(session.id) { mutableStateOf("1") }
    var pain by remember(session.id) { mutableStateOf("0") }
    var warmup by remember(session.id) { mutableStateOf(false) }
    var drop by remember(session.id) { mutableStateOf(false) }
    var notes by remember(session.id) { mutableStateOf("") }
    CoachCard("Log set") {
        OutlinedTextField(setNumber, { setNumber = it }, label = { Text("Set #") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(weight, { weight = it }, label = { Text("Weight") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(reps, { reps = it }, label = { Text("Reps") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(rir, { rir = it }, label = { Text("RIR") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(pain, { pain = it }, label = { Text("Pain 0-10") }, modifier = Modifier.fillMaxWidth())
        Row { Checkbox(warmup, { warmup = it }); Text("Warm-up") }
        Row { Checkbox(drop, { drop = it }); Text("Drop set") }
        OutlinedTextField(notes, { notes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = {
            val painValue = pain.toIntOrNull() ?: 0
            viewModel.logSet(session.id, setNumber.toIntOrNull() ?: 1, weight.toDoubleOrNull() ?: 0.0, reps.toIntOrNull() ?: 0, rir.toIntOrNull() ?: 1, painValue, warmup, drop, notes)
            if (painValue > 3 || (drop && !template.dropSetAllowed) || ((rir.toIntOrNull() ?: 1) <= 0 && template.isCompound())) onPain()
            onSaved()
            setNumber = ((setNumber.toIntOrNull() ?: 1) + 1).toString()
        }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) { Text("Save set") }
    }
}
