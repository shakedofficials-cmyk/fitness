package com.recompos.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.recompos.app.data.local.ExerciseSessionEntity
import com.recompos.app.data.local.ExerciseTemplateEntity
import com.recompos.app.domain.isCompound
import com.recompos.app.ui.AppViewModel
import com.recompos.app.ui.components.CheckLine
import com.recompos.app.ui.components.CoachCard
import com.recompos.app.ui.components.HeroPanel
import com.recompos.app.ui.components.MetricPill
import com.recompos.app.ui.components.ScreenHeader
import com.recompos.app.ui.components.StatRow
import com.recompos.app.ui.components.StatusPill
import kotlinx.coroutines.delay

@Composable
fun WorkoutScreen(viewModel: AppViewModel, nav: NavHostController) {
    val dashboard by viewModel.dashboard.collectAsStateWithLifecycle()
    val workouts by viewModel.workouts.collectAsStateWithLifecycle()
    val exercises by viewModel.allExercises.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { ScreenHeader("Workout", "Plan, library, and completed sessions", "Week ${dashboard.week}") }
        item {
            HeroPanel(
                eyebrow = if (dashboard.isDeload) "Deload rules active" else "Today's assignment",
                title = dashboard.workout?.name ?: "Rest day",
                body = dashboard.workout?.description ?: "Walk, eat, recover."
            ) {
                if (dashboard.workout?.isRestDay == false) {
                    Button(onClick = {
                        dashboard.workout?.let { w -> viewModel.startWorkout(w, exercises.filter { it.workoutTemplateId == w.id }) { nav.navigate("active/$it") } }
                    }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.FitnessCenter, contentDescription = null)
                        Text("Start active session")
                    }
                }
            }
        }
        items(workouts) { workout ->
            val dayExercises = exercises.filter { it.workoutTemplateId == workout.id }
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatusPill("Day ${workout.dayNumber}")
                        Column(Modifier.weight(1f)) {
                            Text(workout.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("${dayExercises.size} movements | about ${workout.estimatedMinutes} min", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Text(workout.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    dayExercises.take(3).forEach {
                        ExercisePreviewRow(it.name, "${it.sets} x ${it.minReps}-${it.maxReps}", onClick = { nav.navigate("exercise/${it.id}") })
                    }
                    if (dayExercises.size > 3) Text("+ ${dayExercises.size - 3} more", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
        item { Text("Exercise Library", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
        items(exercises) { exercise ->
            ExercisePreviewRow(
                title = exercise.name,
                subtitle = "${exercise.targetMuscle} | ${exercise.sets} x ${exercise.minReps}-${exercise.maxReps} | ${exercise.rirTarget} RIR",
                onClick = { nav.navigate("exercise/${exercise.id}") }
            )
        }
        item { Text("Workout History", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
        if (history.isEmpty()) item { CoachCard("No completed sessions yet", "Start a workout and finish it to build your logbook.") }
        items(history) { session ->
            var expanded by remember { mutableStateOf(false) }
            val sessionSets by viewModel.exerciseSessions(session.id).collectAsStateWithLifecycle(initialValue = emptyList())
            val allTemplates by viewModel.allExercises.collectAsStateWithLifecycle()

            CoachCard(
                title = "Week ${session.weekNumber} Day ${session.dayNumber}",
                supporting = "Status: ${session.status}",
                modifier = Modifier.clickable { expanded = !expanded }
            ) {
                if (expanded) {
                    sessionSets.forEach { exerciseSession ->
                        val template = allTemplates.firstOrNull { it.id == exerciseSession.exerciseTemplateId }
                        val sets by viewModel.exerciseSets(exerciseSession.id).collectAsStateWithLifecycle(initialValue = emptyList())
                        Text(template?.name ?: "Unknown Exercise", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                        sets.forEach { set ->
                            Text("Set ${set.setNumber}: ${set.weight} x ${set.reps} (RIR ${set.rir})", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Button(
                        onClick = { viewModel.deleteWorkoutSession(session.id) },
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                        Text("Delete Session")
                    }
                } else {
                    Text("Tap to view details", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                }
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
        item { ScreenHeader(exercise.name, exercise.targetMuscle, "${exercise.sets} sets") }
        item {
            CoachCard("Prescription") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    MetricPill("Reps", "${exercise.minReps}-${exercise.maxReps}", Modifier.weight(1f))
                    MetricPill("Rest", "${exercise.restSecondsMin}-${exercise.restSecondsMax}s", Modifier.weight(1f))
                    MetricPill("RIR", exercise.rirTarget, Modifier.weight(1f))
                }
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

    val currentSets by viewModel.exerciseSets(current?.id ?: 0L).collectAsStateWithLifecycle(initialValue = emptyList())
    val lastSets by viewModel.lastExerciseSets(currentTemplate?.id ?: 0, sessionId).collectAsStateWithLifecycle(initialValue = emptyList())

    var painDialog by remember { mutableStateOf(false) }
    var finishDialog by remember { mutableStateOf(false) }
    var restSeconds by remember { mutableIntStateOf(0) }
    LaunchedEffect(restSeconds) {
        if (restSeconds > 0) {
            delay(1000)
            restSeconds -= 1
        }
    }
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            ScreenHeader("Active Workout", "Move ${selectedIndex + 1} of ${sessions.size.coerceAtLeast(1)}", if (restSeconds > 0) "${restSeconds}s" else "Live")
            LinearProgressIndicator(progress = { if (sessions.isEmpty()) 0f else (selectedIndex + 1f) / sessions.size }, modifier = Modifier.fillMaxWidth())
        }
        item {
            CoachCard("Shoulder warm-up checklist") {
                CheckLine("Cable external rotation: 2 x 15 per side")
                CheckLine("Scapular push-ups: 2 x 10")
                CheckLine("Light lateral raises: 2 x 20")
                CheckLine("First press gets 2-3 ramp-up sets")
            }
        }
        if (currentTemplate != null && current != null) {
            item {
                val prescription = viewModel.deloadCalculator.prescription(currentTemplate, viewModel.dashboard.value.week)
                HeroPanel("Current exercise", currentTemplate.name, "${currentTemplate.targetMuscle} | ${currentTemplate.sets} x ${currentTemplate.minReps}-${currentTemplate.maxReps}") {
                    if (prescription.deload) Text("Deload prescription: ${prescription.sets} sets at ${prescription.rir} RIR. No drop sets or failure.")
                    Text(currentTemplate.cues)
                    if (restSeconds > 0) Text("Rest timer: ${restSeconds}s", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
            if (lastSets.isNotEmpty()) {
                item {
                    CoachCard("Last Session") {
                        lastSets.forEach { set ->
                            StatRow("Set ${set.setNumber}", "${set.weight} x ${set.reps} (RIR ${set.rir})")
                        }
                    }
                }
            }
            if (currentSets.isNotEmpty()) {
                item {
                    CoachCard("Completed Sets") {
                        currentSets.forEach { set ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                Text("Set ${set.setNumber}: ${set.weight} x ${set.reps} (RIR ${set.rir})", style = MaterialTheme.typography.bodyMedium)
                        IconButton(onClick = { viewModel.deleteSet(set) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete set", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
            item { SetLogger(viewModel, current, currentTemplate, onPain = { painDialog = true }, onSaved = { restSeconds = currentTemplate.restSecondsMin }) }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { if (selectedIndex > 0) selectedIndex-- }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                        Text("Previous")
                    }
                    Button(onClick = { if (selectedIndex < sessions.lastIndex) selectedIndex++ }, modifier = Modifier.weight(1f)) {
                        Text("Next")
                        Icon(Icons.Default.ArrowForward, contentDescription = null)
                    }
                }
            }
            item {
                OutlinedButton(onClick = { viewModel.skipExercise(current.id, "Skipped in session") }, modifier = Modifier.fillMaxWidth()) { Text("Skip exercise") }
            }
        } else {
            item { CoachCard("Loading session", "Exercise sessions are being created.") }
        }
        item {
            Button(onClick = { finishDialog = true }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.CheckCircle, contentDescription = null)
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
    if (finishDialog) {
        AlertDialog(
            onDismissRequest = { finishDialog = false },
            title = { Text("Finish session?") },
            text = { Text("Save this workout to history and return to the logbook.") },
            confirmButton = { Button(onClick = { viewModel.finishWorkout(sessionId); finishDialog = false; nav.navigate("workout") }) { Text("Finish") } },
            dismissButton = { TextButton(onClick = { finishDialog = false }) { Text("Keep logging") } }
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
    val haptic = LocalHapticFeedback.current

    CoachCard("Log set", "Keep it honest. Clean reps beat fantasy numbers.") {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(setNumber, { setNumber = it }, label = { Text("Set") }, modifier = Modifier.weight(1f))
            OutlinedTextField(weight, { weight = it }, label = { Text("Weight") }, modifier = Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(reps, { reps = it }, label = { Text("Reps") }, modifier = Modifier.weight(1f))
            OutlinedTextField(rir, { rir = it }, label = { Text("RIR") }, modifier = Modifier.weight(1f))
            OutlinedTextField(pain, { pain = it }, label = { Text("Pain") }, modifier = Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Surface(Modifier.weight(1f), shape = MaterialTheme.shapes.small, color = MaterialTheme.colorScheme.surface) {
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) { Checkbox(warmup, { warmup = it }); Text("Warm-up") }
            }
            Surface(Modifier.weight(1f), shape = MaterialTheme.shapes.small, color = MaterialTheme.colorScheme.surface) {
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) { Checkbox(drop, { drop = it }); Text("Drop set") }
            }
        }
        OutlinedTextField(notes, { notes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = {
            val painValue = pain.toIntOrNull() ?: 0
            viewModel.logSet(session.id, setNumber.toIntOrNull() ?: 1, weight.toDoubleOrNull() ?: 0.0, reps.toIntOrNull() ?: 0, rir.toIntOrNull() ?: 1, painValue, warmup, drop, notes)
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            if (painValue > 3 || (drop && !template.dropSetAllowed) || ((rir.toIntOrNull() ?: 1) <= 0 && template.isCompound())) onPain()
            onSaved()
            setNumber = ((setNumber.toIntOrNull() ?: 1) + 1).toString()
        }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) { Text("Save set") }
    }
}

@Composable
private fun ExercisePreviewRow(title: String, subtitle: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}
