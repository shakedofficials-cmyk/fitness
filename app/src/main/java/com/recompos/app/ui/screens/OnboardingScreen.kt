package com.recompos.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.recompos.app.ui.AppViewModel
import com.recompos.app.ui.components.CoachCard
import java.time.LocalDate

@Composable
fun OnboardingScreen(viewModel: AppViewModel) {
    var startDate by remember { mutableStateOf(LocalDate.now().toString()) }
    var units by remember { mutableStateOf("metric") }
    var bodyweight by remember { mutableStateOf("") }
    var waist by remember { mutableStateOf("") }
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("RecompOS", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black)
        Text("A 12-week bodybuilding recomp coach. Local-first. No login. No cloud.")
        CoachCard("Medical disclaimer", "This app is not medical advice. For high triglycerides, reflux, supplements, and vitamin D dosing, consult a licensed clinician.")
        OutlinedTextField(startDate, { startDate = it }, label = { Text("Start date (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = units == "metric", onClick = { units = "metric" }, label = { Text("kg / cm") })
            FilterChip(selected = units == "imperial", onClick = { units = "imperial" }, label = { Text("lb / in") })
        }
        OutlinedTextField(bodyweight, { bodyweight = it }, label = { Text("Baseline bodyweight") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(waist, { waist = it }, label = { Text("Baseline waist") }, modifier = Modifier.fillMaxWidth())
        CoachCard("Default reminders", "Morning weight, workouts, steps, nutrition, creatine, weekly waist, progress photos, deloads, and sleep wind-down.")
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                val parsed = runCatching { LocalDate.parse(startDate) }.getOrDefault(LocalDate.now())
                viewModel.completeOnboarding(parsed, units, bodyweight.toDoubleOrNull(), waist.toDoubleOrNull())
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start 12-week block")
        }
    }
}
