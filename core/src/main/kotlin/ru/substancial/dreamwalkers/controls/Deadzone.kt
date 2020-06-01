package ru.substancial.dreamwalkers.controls

import kotlin.math.abs

class Deadzone(val deadzone: Float) {

    companion object {

        val SuperSensitive = Deadzone(0f)

    }

}

fun <R> Deadzone.tryReport(extent: Float, cont: (Float) -> R): R? {
    return if (abs(extent) >= deadzone) {
        cont(extent)
    } else {
        null
    }
}