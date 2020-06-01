package ru.substancial.dreamwalkers.controls

import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerAdapter
import com.badlogic.gdx.math.Vector2

abstract class MappedControllerAdapter(private val deadzone: Deadzone = Deadzone.SuperSensitive) : ControllerAdapter(), ControllerReporter {

    final override fun buttonDown(controller: Controller?, buttonIndex: Int): Boolean {
        return false
    }

    final override fun buttonUp(controller: Controller?, buttonIndex: Int): Boolean {
        return false
    }

    final override fun axisMoved(controller: Controller?, axisIndex: Int, value: Float): Boolean {
        return false
    }

    override fun leftStick(): Vector2 {
        TODO("Not yet implemented")
    }

    override fun rightStick(): Vector2 {
        TODO("Not yet implemented")
    }

    override fun leftStickPressed(): Boolean {
        TODO("Not yet implemented")
    }

    override fun rightStickPressed(): Boolean {
        TODO("Not yet implemented")
    }

    override fun leftTrigger(): Float {
        TODO("Not yet implemented")
    }

    override fun rightTrigger(): Float {
        TODO("Not yet implemented")
    }

    override fun dpadDown(): Boolean {
        TODO("Not yet implemented")
    }

    override fun dpadUp(): Boolean {
        TODO("Not yet implemented")
    }

    override fun dpadLeft(): Boolean {
        TODO("Not yet implemented")
    }

    override fun dpadRight(): Boolean {
        TODO("Not yet implemented")
    }

    override fun actionDown(): Boolean {
        TODO("Not yet implemented")
    }

    override fun actionLeft(): Boolean {
        TODO("Not yet implemented")
    }

    override fun actionUp(): Boolean {
        TODO("Not yet implemented")
    }

    override fun actionRight(): Boolean {
        TODO("Not yet implemented")
    }
}