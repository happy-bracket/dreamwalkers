package ru.substancial.dreamwalkers.controls

import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerAdapter
import kotlin.math.round

class GroundController : ControllerAdapter() {

    private var accumulator: Float = 0f
    var jumpClicked: () -> Unit = {}

    fun pollDirection(): Float {
        return accumulator
    }

    override fun buttonUp(controller: Controller?, buttonIndex: Int): Boolean {
        when (buttonIndex) {
            0 -> jumpClicked()
        }
        return true
    }

    override fun axisMoved(controller: Controller?, axisIndex: Int, value: Float): Boolean {
        // 3 coz left stick x-axis
        when(axisIndex) {
            3 -> accumulator = round(value)
        }
        return true
    }

}