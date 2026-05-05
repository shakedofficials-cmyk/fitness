package com.recompos.app.domain

import com.recompos.app.data.local.BodyweightLogEntity
import com.recompos.app.data.local.DigestionLogEntity
import com.recompos.app.data.local.SleepLogEntity
import com.recompos.app.data.local.StepsLogEntity
import com.recompos.app.data.local.WaistLogEntity

class CoachReviewEngine {
    fun review(
        bodyweights: List<BodyweightLogEntity>,
        waists: List<WaistLogEntity>,
        sleep: List<SleepLogEntity>,
        digestion: List<DigestionLogEntity>,
        steps: List<StepsLogEntity>
    ): CoachSignal {
        val latestWeight = bodyweights.takeLast(4).map { it.weight }.avgOrNull()
        val previousWeight = bodyweights.dropLast(4).takeLast(4).map { it.weight }.avgOrNull()
        val waistDelta = waists.takeLast(2).let { if (it.size == 2) it[1].waist - it[0].waist else 0.0 }
        val sleepAvg = sleep.takeLast(7).map { it.hours }.avgOrNull()
        val refluxBad = digestion.takeLast(4).count { it.reflux || it.score <= 4 } >= 2
        val stepsAvg = steps.takeLast(7).map { it.steps.toDouble() }.avgOrNull()

        return when {
            sleepAvg != null && sleepAvg < 6.0 -> CoachSignal("Watch recovery", "Sleep is averaging under 6 hours. Hold load jumps until recovery comes back.", "warning")
            refluxBad -> CoachSignal("Clean up dinner", "Reflux is trending worse. Reduce late fat, spicy meals, and large liquid shakes near bedtime.", "warning")
            latestWeight != null && previousWeight != null && latestWeight - previousWeight > 0.5 && waistDelta > 0.5 ->
                CoachSignal("Tighten the surplus", "Weight and waist are both jumping. Remove about 150 kcal, mostly from rest-day carbs/fats.", "warning")
            latestWeight != null && previousWeight != null && previousWeight - latestWeight > 0.5 ->
                CoachSignal("Add fuel", "Weight is dropping too fast for a lean-gain block. Add 150-200 kcal, mostly carbs.", "warning")
            stepsAvg != null && stepsAvg < 8000 ->
                CoachSignal("Get the walk in", "Steps are below target. Move to 8,000 daily before cutting food harder.", "info")
            else -> CoachSignal("Recomp is on track", "Keep waist controlled, beat the logbook cleanly, and do not donate recovery to junk volume.", "success")
        }
    }

    private fun List<Double>.avgOrNull(): Double? = if (isEmpty()) null else average()
}
