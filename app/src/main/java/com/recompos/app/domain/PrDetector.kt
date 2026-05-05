package com.recompos.app.domain

import kotlin.math.pow

class PrDetector {
    fun estimatedOneRepMax(weight: Double, reps: Int): Double =
        if (reps <= 1) weight else weight * (1 + reps / 30.0)

    fun volume(sets: List<SetPerformance>): Double =
        sets.filterNot { it.isWarmup }.sumOf { it.weight * it.reps }

    fun bestSetLabel(sets: List<SetPerformance>): String {
        val best = sets.maxByOrNull { estimatedOneRepMax(it.weight, it.reps) }
        return best?.let { "${it.weight} x ${it.reps} (${estimatedOneRepMax(it.weight, it.reps).toInt()} e1RM)" } ?: "No sets yet"
    }
}
