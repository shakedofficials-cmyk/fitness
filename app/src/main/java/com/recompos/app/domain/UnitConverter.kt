package com.recompos.app.domain

object UnitConverter {
    fun kgToLb(kg: Double) = kg * 2.2046226218
    fun lbToKg(lb: Double) = lb / 2.2046226218
    fun cmToIn(cm: Double) = cm / 2.54
    fun inToCm(inches: Double) = inches * 2.54
}
