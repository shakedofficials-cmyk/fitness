package com.recompos.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.recompos.app.ui.AppViewModel
import com.recompos.app.ui.components.BarChart
import com.recompos.app.ui.components.CoachCard
import com.recompos.app.ui.components.LineChart
import com.recompos.app.ui.components.StatRow

@Composable
fun LogScreen(viewModel: AppViewModel) {
    val nutrition by viewModel.nutrition.collectAsStateWithLifecycle()
    val supplements by viewModel.supplements.collectAsStateWithLifecycle()
    val habits by viewModel.habits.collectAsStateWithLifecycle()
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Log", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black) }
        item { BodyQuickLog(viewModel) }
        item { NutritionQuickLog(viewModel) }
        item { RecoveryQuickLog(viewModel) }
        item { CardioHabitQuickLog(viewModel) }
        item { PhotoQuickLog(viewModel) }
        item {
            CoachCard("Latest nutrition") {
                nutrition.take(3).forEach { StatRow("${it.dateEpochDay}", "${it.calories} kcal / P${it.protein} C${it.carbs} F${it.fat}") }
                if (nutrition.isEmpty()) Text("No macro logs yet.")
            }
        }
        item {
            CoachCard("Supplements and habits") {
                Text("Supplement logs: ${supplements.size}")
                Text("Habit logs: ${habits.size}")
            }
        }
    }
}

@Composable
private fun PhotoQuickLog(viewModel: AppViewModel) {
    var uri by remember { mutableStateOf("") }
    var viewType by remember { mutableStateOf("front") }
    CoachCard("Progress Photos", "Every 2 weeks. Store a local URI only; nothing is uploaded.") {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("front", "side", "back").forEach {
                FilterChip(selected = viewType == it, onClick = { viewType = it }, label = { Text(it) })
            }
        }
        OutlinedTextField(uri, { uri = it }, label = { Text("Local image URI") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = { if (uri.isNotBlank()) viewModel.logPhoto(viewType, uri) }, modifier = Modifier.fillMaxWidth()) {
            Text("Save photo URI")
        }
    }
}

@Composable
private fun BodyQuickLog(viewModel: AppViewModel) {
    var weight by remember { mutableStateOf("") }
    var waist by remember { mutableStateOf("") }
    CoachCard("Body Log", "Morning bodyweight 4x/week. Waist at navel 1x/week.") {
        OutlinedTextField(weight, { weight = it }, label = { Text("Bodyweight") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(waist, { waist = it }, label = { Text("Waist") }, modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Button(onClick = { weight.toDoubleOrNull()?.let(viewModel::logBodyweight) }, modifier = Modifier.weight(1f)) { Text("Save weight") }
            Button(onClick = { waist.toDoubleOrNull()?.let(viewModel::logWaist) }, modifier = Modifier.weight(1f)) { Text("Save waist") }
        }
    }
}

@Composable
private fun NutritionQuickLog(viewModel: AppViewModel) {
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("190") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    CoachCard("Nutrition", "Training target: 2600 kcal, 190P, 300C, 65-70F. Rest target: 2300-2400 kcal, 190P, 220-240C, 70-75F.") {
        OutlinedTextField(calories, { calories = it }, label = { Text("Calories") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(protein, { protein = it }, label = { Text("Protein") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(carbs, { carbs = it }, label = { Text("Carbs") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(fat, { fat = it }, label = { Text("Fat") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = {
            viewModel.logNutrition(calories.toIntOrNull() ?: 0, protein.toIntOrNull() ?: 0, carbs.toIntOrNull() ?: 0, fat.toIntOrNull() ?: 0)
        }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) { Text("Save macros") }
        Text("Avoid huge fatty late meals, spicy/fried foods, and large bedtime shakes if reflux worsens.")
    }
}

@Composable
private fun RecoveryQuickLog(viewModel: AppViewModel) {
    var hours by remember { mutableStateOf("") }
    var quality by remember { mutableStateOf("7") }
    var digestion by remember { mutableStateOf("7") }
    var reflux by remember { mutableStateOf(false) }
    var bloating by remember { mutableStateOf(false) }
    var triggers by remember { mutableStateOf("") }
    CoachCard("Sleep + Digestion/Reflux") {
        OutlinedTextField(hours, { hours = it }, label = { Text("Sleep hours") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(quality, { quality = it }, label = { Text("Sleep quality 1-10") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(digestion, { digestion = it }, label = { Text("Digestion score 1-10") }, modifier = Modifier.fillMaxWidth())
        Row { Checkbox(reflux, { reflux = it }); Text("Reflux") }
        Row { Checkbox(bloating, { bloating = it }); Text("Bloating") }
        OutlinedTextField(triggers, { triggers = it }, label = { Text("Trigger foods") }, modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Button(onClick = { viewModel.logSleep(hours.toDoubleOrNull() ?: 0.0, quality.toIntOrNull() ?: 5) }, modifier = Modifier.weight(1f)) { Text("Save sleep") }
            Button(onClick = { viewModel.logDigestion(digestion.toIntOrNull() ?: 5, reflux, bloating, triggers) }, modifier = Modifier.weight(1f)) { Text("Save digestion") }
        }
    }
}

@Composable
private fun CardioHabitQuickLog(viewModel: AppViewModel) {
    var steps by remember { mutableStateOf("") }
    var modality by remember { mutableStateOf("Incline treadmill") }
    var duration by remember { mutableStateOf("25") }
    var creatine by remember { mutableStateOf(true) }
    var whey by remember { mutableStateOf(false) }
    var vitaminD by remember { mutableStateOf(false) }
    var omega3 by remember { mutableStateOf(false) }
    var mobility by remember { mutableStateOf(false) }
    var breathing by remember { mutableStateOf(false) }
    CoachCard("Cardio + Habits", "Zone 2: you can talk, but not comfortably sing. Avoid brutal HIIT right now.") {
        OutlinedTextField(steps, { steps = it }, label = { Text("Steps") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(modality, { modality = it }, label = { Text("Cardio modality") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(duration, { duration = it }, label = { Text("Duration minutes") }, modifier = Modifier.fillMaxWidth())
        Row { Checkbox(creatine, { creatine = it }); Text("Creatine 5 g") }
        Row { Checkbox(whey, { whey = it }); Text("Whey") }
        Row { Checkbox(vitaminD, { vitaminD = it }); Text("Vitamin D") }
        Row { Checkbox(omega3, { omega3 = it }); Text("Omega-3") }
        Row { Checkbox(mobility, { mobility = it }); Text("Shoulder mobility 10 min") }
        Row { Checkbox(breathing, { breathing = it }); Text("Deep breathing 5 min") }
        Button(onClick = { steps.toIntOrNull()?.let(viewModel::logSteps) }, modifier = Modifier.fillMaxWidth()) { Text("Save steps") }
        Button(onClick = { viewModel.logCardio(modality, duration.toIntOrNull() ?: 0, "Zone 2") }, modifier = Modifier.fillMaxWidth()) { Text("Save cardio") }
        Button(onClick = { viewModel.logSupplement(creatine, whey, vitaminD, omega3); viewModel.logHabit(mobility, breathing, false) }, modifier = Modifier.fillMaxWidth()) { Text("Save habits") }
    }
}

@Composable
fun AnalyticsScreen(viewModel: AppViewModel) {
    val dashboard by viewModel.dashboard.collectAsStateWithLifecycle()
    val bodyweights by viewModel.bodyweights.collectAsStateWithLifecycle()
    val waists by viewModel.waists.collectAsStateWithLifecycle()
    val steps by viewModel.steps.collectAsStateWithLifecycle()
    val sleep by viewModel.sleep.collectAsStateWithLifecycle()
    val digestion by viewModel.digestion.collectAsStateWithLifecycle()
    val cardio by viewModel.cardio.collectAsStateWithLifecycle()
    val sets by viewModel.sets.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Analytics", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black) }
        item { CoachCard(dashboard.coachTitle, dashboard.coachMessage) }
        item { CoachCard("Bodyweight trend") { LineChart(bodyweights.map { it.weight }) } }
        item { CoachCard("Waist trend") { LineChart(waists.map { it.waist }) } }
        item { CoachCard("Strength volume") { LineChart(sets.groupBy { it.completedAtMillis / 86_400_000L }.values.map { day -> day.sumOf { it.weight * it.reps } }) } }
        item { CoachCard("Weekly volume by muscle", "Completed hard sets are shown as logged set count for now.") { BarChart(listOf("Sets" to sets.count { !it.isWarmup && !it.isDropSet }.toDouble())) } }
        item { CoachCard("Training adherence") { StatRow("Completed workouts", history.count { it.status == "completed" }.toString()); StatRow("Completed sets", sets.size.toString()) } }
        item { CoachCard("Cardio adherence") { BarChart(cardio.groupBy { it.dateEpochDay / 7 }.map { "W${it.key}" to it.value.sumOf { c -> c.durationMinutes }.toDouble() }) } }
        item { CoachCard("Steps") { LineChart(steps.map { it.steps.toDouble() }) } }
        item { CoachCard("Sleep") { LineChart(sleep.map { it.hours }) } }
        item { CoachCard("Digestion/reflux") { LineChart(digestion.map { it.score.toDouble() }) } }
    }
}

@Composable
fun KnowledgeScreen(viewModel: AppViewModel) {
    val articles by viewModel.articles.collectAsStateWithLifecycle()
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Knowledge Base", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black) }
        items(articles) { article -> CoachCard(article.title, article.body) }
    }
}

@Composable
fun SettingsScreen(viewModel: AppViewModel) {
    val prefs by viewModel.preferences.collectAsStateWithLifecycle()
    var exportText by remember { mutableStateOf("") }
    var importText by remember { mutableStateOf("") }
    var importStatus by remember { mutableStateOf("") }
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Settings", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black) }
        item {
            CoachCard("Units") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = prefs.unitSystem == "metric", onClick = { viewModel.setUnits("metric") }, label = { Text("kg/cm") })
                    FilterChip(selected = prefs.unitSystem == "imperial", onClick = { viewModel.setUnits("imperial") }, label = { Text("lb/in") })
                }
            }
        }
        item {
            CoachCard("Theme") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = prefs.themeMode == "dark", onClick = { viewModel.setTheme("dark") }, label = { Text("Dark") })
                    FilterChip(selected = prefs.themeMode == "light", onClick = { viewModel.setTheme("light") }, label = { Text("Light") })
                    FilterChip(selected = prefs.themeMode == "system", onClick = { viewModel.setTheme("system") }, label = { Text("System") })
                }
            }
        }
        item {
            CoachCard("Reminders", "Workout, steps, nutrition, creatine, waist, progress photos, deload, and wind-down reminders.") {
                Row {
                    Text("Enabled", Modifier.weight(1f))
                    Switch(checked = prefs.remindersEnabled, onCheckedChange = { viewModel.setReminders(it) })
                }
            }
        }
        item {
            CoachCard("Export / import", "JSON backup and CSV set export are generated fully offline. Import currently validates schema before restore.") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = { exportText = viewModel.buildJsonExport() }, modifier = Modifier.weight(1f)) { Text("JSON") }
                    Button(onClick = { exportText = viewModel.buildFullCsv() }, modifier = Modifier.weight(1f)) { Text("CSV") }
                }
                if (exportText.isNotBlank()) {
                    Text(exportText, maxLines = 8, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodySmall)
                }
                OutlinedTextField(importText, { importText = it }, label = { Text("Paste JSON import") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = { importStatus = if (viewModel.validateImport(importText)) "Import file is valid schema v1." else "Invalid import JSON." }, modifier = Modifier.weight(1f)) { Text("Validate") }
                    Button(onClick = { viewModel.importJson(importText) { ok -> importStatus = if (ok) "Import complete." else "Import failed." } }, modifier = Modifier.weight(1f)) { Text("Import") }
                }
                if (importStatus.isNotBlank()) Text(importStatus)
            }
        }
        item { CoachCard("About / disclaimer", "RecompOS stores data locally and does not require login. This app is not medical advice. Consult a licensed clinician for high triglycerides, reflux, supplements, and vitamin D dosing.") }
    }
}
