package com.recompos.app.domain

import com.recompos.app.data.local.ExerciseTemplateEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class DomainLogicTest {
    @Test
    fun calendarCalculatesWeekAndDay() {
        val result = ProgramCalendar().current(LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 10))
        assertEquals(2, result.week)
        assertEquals(3, result.day)
        assertFalse(result.isComplete)
    }

    @Test
    fun calendarMarksBlockCompleteAfterWeekTwelve() {
        val result = ProgramCalendar().current(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 4, 1))
        assertEquals(12, result.week)
        assertTrue(result.isComplete)
    }

    @Test
    fun deloadReducesSetsAndRemovesIntensity() {
        val exercise = exercise(sets = 4)
        val result = DeloadCalculator().prescription(exercise, 4)
        assertEquals(3, result.sets)
        assertEquals("3-4", result.rir)
        assertFalse(result.dropSetAllowed)
        assertFalse(result.failureAllowed)
        assertTrue(result.deload)
    }

    @Test
    fun doubleProgressionSuggestsLoadIncreaseAtTopRange() {
        val exercise = exercise(sets = 3, min = 8, max = 12, name = "Incline Smith Press")
        val suggestion = ProgressionEngine().suggest(
            exercise,
            listOf(
                SetPerformance(80.0, 12, 2, 0),
                SetPerformance(80.0, 12, 2, 0),
                SetPerformance(80.0, 12, 2, 0)
            )
        )
        assertTrue(suggestion.readyForLoadIncrease)
    }

    @Test
    fun doubleProgressionRequiresTargetRir() {
        val exercise = exercise(sets = 3, min = 8, max = 12, name = "Cable Lateral Raise").copy(rirTarget = "1")
        val suggestion = ProgressionEngine().suggest(
            exercise,
            listOf(
                SetPerformance(12.5, 12, 0, 0),
                SetPerformance(12.5, 12, 0, 0),
                SetPerformance(12.5, 12, 0, 0)
            )
        )
        assertFalse(suggestion.readyForLoadIncrease)
    }

    @Test
    fun shoulderPainBlocksProgression() {
        val suggestion = ProgressionEngine().suggest(exercise(), listOf(SetPerformance(20.0, 20, 1, 4)))
        assertFalse(suggestion.readyForLoadIncrease)
        assertEquals("Swap this movement", suggestion.title)
    }

    @Test
    fun unitConversionsRoundTrip() {
        val pounds = UnitConverter.kgToLb(100.0)
        assertEquals(100.0, UnitConverter.lbToKg(pounds), 0.001)
    }

    private fun exercise(
        sets: Int = 3,
        min: Int = 8,
        max: Int = 12,
        name: String = "Cable Lateral Raise"
    ) = ExerciseTemplateEntity(
        id = 1,
        workoutTemplateId = 1,
        orderIndex = 1,
        name = name,
        targetMuscle = "side delts",
        secondaryMuscles = "",
        sets = sets,
        minReps = min,
        maxReps = max,
        restSecondsMin = 60,
        restSecondsMax = 90,
        rirTarget = "1-2",
        cues = "",
        cautions = "",
        alternatives = "",
        dropSetAllowed = true,
        failureAllowed = true
    )
}
