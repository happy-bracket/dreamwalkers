package ru.substancial.dreamwalkers.controls

import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerAdapter
import com.badlogic.gdx.math.Vector2

class AerialController : ControllerAdapter() {

    private var leftTiltDown: Boolean = false
    private var rightTiltDown: Boolean = false

    private val strokeDirection: Vector2 = Vector2()

    /**
     * -1f for left, 1f for right
     */
    fun getTiltDirection(): Float {
        var acc = 0f
        if (leftTiltDown)
            acc -= 1f
        if (rightTiltDown)
            acc += 1f
        return acc
    }

    fun getStrokeDirection(): Vector2 {
        return strokeDirection.cpy()
    }

    override fun buttonDown(controller: Controller?, buttonIndex: Int): Boolean {
        when (buttonIndex) {
            4 -> leftTiltDown = true
            5 -> rightTiltDown = true
        }
        return true
    }

    override fun buttonUp(controller: Controller?, buttonIndex: Int): Boolean {
        // 4 for L1
        // 5 for R1
        when (buttonIndex) {
            4 -> leftTiltDown = false
            5 -> rightTiltDown = false
        }
        return true
    }

    override fun axisMoved(controller: Controller?, axisIndex: Int, value: Float): Boolean {
        // 3 for left-x
        // 4 for left-y
        when (axisIndex) {
            3 -> strokeDirection.x = value
            4 -> strokeDirection.y = value
        }
        return true
    }

}