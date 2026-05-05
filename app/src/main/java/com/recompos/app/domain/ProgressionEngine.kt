package com.recompos.app.domain

import com.recompos.app.data.local.ExerciseTemplateEntity

class ProgressionEngine {
    fun suggest(exercise: ExerciseTemplateEntity, sets: List<SetPerformance>): ProgressionSuggestion {
        val working = sets.filterNot { it.isWarmup || it.isDropSet }
        if (working.isEmpty()) {
            return ProgressionSuggestion("Start the logbook", "Log clean working sets. Reps first, load second.", false)
        }
        val targetSets = exercise.sets
        val hitTop = working.size >= targetSets && working.takeLast(targetSets).all {
            it.reps >= exercise.maxReps && it.rir >= requiredRir(exercise)
        }
        val pain = working.maxOfOrNull { it.painScore } ?: 0
        val tooHard = working.any { it.rir <= 0 && exercise.isCompound() }
        return when {
            pain > 3 -> ProgressionSuggestion("Swap this movement", "Pain was above 3/10. Do not chase progression here until it is calm.", false)
            tooHard -> ProgressionSuggestion("Back off slightly", "True failure on compounds is not the assignment. Keep one clean rep in reserve.", false)
            hitTop && exercise.isCompound() -> ProgressionSuggestion("Increase load", "All sets hit the top of the range at target RIR. Add 2.5-5 kg next time.", true)
            hitTop -> ProgressionSuggestion("Increase carefully", "All working sets hit the top. Use the smallest jump available or add reps first on cables/dumbbells.", true)
            else -> ProgressionSuggestion("Keep load", "Repeat this weight and beat the logbook with cleaner reps or more total reps.", false)
        }
    }

    private fun requiredRir(exercise: ExerciseTemplateEntity): Int {
        val target = exercise.rirTarget.lowercase()
        return when {
            target.contains("3") || target.contains("4") -> 3
            target.contains("1") -> 1
            target.contains("2") -> 2
            else -> 1
        }
    }
}
