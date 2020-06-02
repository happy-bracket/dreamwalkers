package ru.substancial.dreamwalkers.controls

import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerAdapter
import com.badlogic.gdx.math.Vector2

class MappedControllerAdapter(private val deadzone: Deadzone = Deadzone.SuperSensitive) : ControllerAdapter(), ControllerReporter {

    override fun buttonDown(controller: Controller?, buttonIndex: Int): Boolean {
        return false
    }

    override fun buttonUp(controller: Controller?, buttonIndex: Int): Boolean {
        return false
    }

    override fun axisMoved(controller: Controller?, axisIndex: Int, value: Float): Boolean {
        return false
    }

    override fun stick(side: ControllerReporter.Side): Vector2 {
        TODO("Not yet implemented")
    }

    override fun stickPressed(side: ControllerReporter.Side): Boolean {
        TODO("Not yet implemented")
    }

    override fun trigger(side: ControllerReporter.Side): Float {
        TODO("Not yet implemented")
    }

    override fun bumper(side: ControllerReporter.Side): Boolean {
        TODO("Not yet implemented")
    }

    override fun dpad(direction: ControllerReporter.Direction): Boolean {
        TODO("Not yet implemented")
    }

    override fun action(direction: ControllerReporter.Direction): Boolean {
        TODO("Not yet implemented")
    }
}