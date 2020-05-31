package ru.substancial.dreamwalkers.controls

import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerAdapter
import kotlin.math.abs

abstract class DeadzoneControllerAdapter(private val deadzone: Float) : ControllerAdapter() {

    final override fun axisMoved(controller: Controller?, axisIndex: Int, value: Float): Boolean {
        return when {
            abs(value) >= deadzone -> {
                reactToAxis(controller, axisIndex, value)
                true
            }
            else -> {
                false
            }
        }
    }

    abstract fun reactToAxis(controller: Controller?, axisIndex: Int, value: Float)

}