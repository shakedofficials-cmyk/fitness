package com.recompos.app.domain

import com.recompos.app.data.local.ExerciseTemplateEntity
import kotlin.math.ceil

class DeloadCalculator {
    fun prescription(exercise: ExerciseTemplateEntity, week: Int): Prescription {
        val isDeload = week == 4 || week == 9
        return if (isDeload) {
            Prescription(
                sets = ceil(exercise.sets * 0.55).toInt().coerceAtLeast(1),
                rir = "3-4",
                dropSetAllowed = false,
                failureAllowed = false,
                deload = true
            )
        } else {
            Prescription(
                sets = exercise.sets,
                rir = exercise.rirTarget,
                dropSetAllowed = exercise.dropSetAllowed,
                failureAllowed = exercise.failureAllowed,
                deload = false
            )
        }
    }
}
