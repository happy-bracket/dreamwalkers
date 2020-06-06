package ru.substancial.dreamwalkers.utilities

import com.badlogic.gdx.math.Vector2
import kotlin.math.abs

fun Vector2.checkDeadzone(deadzone: Float): Boolean {
    return abs(x) >= deadzone || abs(y) >= deadzone
}