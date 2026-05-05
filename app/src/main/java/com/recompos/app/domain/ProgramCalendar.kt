package com.recompos.app.domain

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.floor

class ProgramCalendar {
    fun current(startDate: LocalDate, today: LocalDate = LocalDate.now()): ProgramDate {
        val days = ChronoUnit.DAYS.between(startDate, today).coerceAtLeast(0)
        val week = floor(days / 7.0).toInt() + 1
        val day = (days % 7).toInt() + 1
        return ProgramDate(today, week.coerceAtMost(12), day, week > 12)
    }
}
