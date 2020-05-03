package ru.substancial.dreamwalkers.controls

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerAdapter
import com.badlogic.gdx.math.Vector2

class AerialController : ControllerAdapter() {

    private val flightDirection: Vector2 = Vector2()
    var wingsSpread: Boolean = false

    var strokeListener: () -> Unit = {}

    fun getStrokeDirection(): Vector2 {
        return flightDirection.cpy()
    }

    override fun buttonDown(controller: Controller?, buttonIndex: Int): Boolean {
        when (buttonIndex) {

        }
        return true
    }

    override fun buttonUp(controller: Controller?, buttonIndex: Int): Boolean {
        // 4 for L1
        // 6 for L2
        // 5 for R1
        when (buttonIndex) {
            6 -> strokeListener()
        }
        return true
    }

    override fun axisMoved(controller: Controller?, axisIndex: Int, value: Float): Boolean {
        // 3 for left-x
        // 2 for left-y
        when (axisIndex) {
            3 -> flightDirection.x = value
            2 -> flightDirection.y = value
        }
        return true
    }

}