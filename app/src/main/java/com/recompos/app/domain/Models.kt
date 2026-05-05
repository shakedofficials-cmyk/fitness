package com.recompos.app.domain

import com.recompos.app.data.local.ExerciseTemplateEntity
import java.time.LocalDate

data class ProgramDate(
    val today: LocalDate,
    val week: Int,
    val day: Int,
    val isComplete: Boolean
)

data class Prescription(
    val sets: Int,
    val rir: String,
    val dropSetAllowed: Boolean,
    val failureAllowed: Boolean,
    val deload: Boolean
)

data class ProgressionSuggestion(
    val title: String,
    val message: String,
    val readyForLoadIncrease: Boolean
)

data class SetPerformance(
    val weight: Double,
    val reps: Int,
    val rir: Int,
    val painScore: Int,
    val isWarmup: Boolean = false,
    val isDropSet: Boolean = false
)

data class CoachSignal(
    val title: String,
    val message: String,
    val severity: String
)

fun ExerciseTemplateEntity.isCompound(): Boolean {
    val n = name.lowercase()
    return listOf("press", "squat", "deadlift", "row", "pulldown", "leg press").any { n.contains(it) } &&
        !n.contains("pushdown") &&
        !n.contains("curl") &&
        !n.contains("fly")
}
