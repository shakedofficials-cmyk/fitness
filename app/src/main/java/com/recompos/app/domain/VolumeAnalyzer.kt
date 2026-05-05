package com.recompos.app.domain

import com.recompos.app.data.local.ExerciseTemplateEntity
import com.recompos.app.data.local.SetLogEntity

class VolumeAnalyzer {
    val targets = mapOf(
        "chest" to 7,
        "back" to 20,
        "side delts" to 22,
        "rear delts" to 22,
        "biceps" to 18,
        "triceps" to 9,
        "quads" to 9,
        "hamstrings" to 8,
        "calves" to 7,
        "abs" to 11
    )

    fun completedSetsByMuscle(exercises: List<ExerciseTemplateEntity>, setsByExerciseId: Map<Int, List<SetLogEntity>>): Map<String, Int> {
        return exercises.associate { exercise ->
            val hardSets = setsByExerciseId[exercise.id].orEmpty().count { !it.isWarmup && !it.isDropSet }
            exercise.targetMuscle to hardSets
        }.toMutableMap().also { result ->
            val back = result.filterKeys { it.contains("back") || it.contains("lat") }.values.sum()
            if (back > 0) result["back"] = back
        }
    }
}
