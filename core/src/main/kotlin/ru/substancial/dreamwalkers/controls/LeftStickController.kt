package ru.substancial.dreamwalkers.controls

import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerAdapter
import kotlin.math.round

class LeftStickController : ControllerAdapter() {

    private var accumulator: Float = 0f

    fun pollDirection(): Float {
        return accumulator
    }

    override fun axisMoved(controller: Controller?, axisIndex: Int, value: Float): Boolean {
        // 3 coz left stick x-axis
        when(axisIndex) {
            3 -> accumulator = round(value)
        }
        return true
    }

}