package ru.substancial.dreamwalkers.controls

import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerAdapter
import com.badlogic.gdx.math.Vector2
import ru.substancial.dreamwalkers.ecs.component.ButtonLayout
import ru.substancial.dreamwalkers.ecs.component.ButtonLayout.L2
import ru.substancial.dreamwalkers.ecs.component.ButtonLayout.LX
import ru.substancial.dreamwalkers.ecs.component.ButtonLayout.LY
import ru.substancial.dreamwalkers.utilities.checkDeadzone

class TheController : ControllerAdapter() {

    private val leftStick = Vector2()
    private var _airTriggerDown = false

    val airTriggerDown: Boolean
        get() = _airTriggerDown

    var airTriggerDownListener = {}
    var airTriggerUpListener = {}

    fun pollLeftStick(): Vector2 {
        return if (leftStick.checkDeadzone(0.3f)) {
            leftStick.cpy()
        } else Vector2()
    }

    override fun axisMoved(controller: Controller?, axisIndex: Int, value: Float): Boolean {
        when (axisIndex) {
            ButtonLayout[LX] -> {
                leftStick.x = value
            }
            ButtonLayout[LY] -> {
                leftStick.y = -value
            }
            ButtonLayout[L2] -> {
                if (value >= 0.4f) {
                    if (!_airTriggerDown) {
                        _airTriggerDown = true
                        airTriggerDownListener()
                    }
                } else {
                    if (_airTriggerDown) {
                        _airTriggerDown = false
                        airTriggerUpListener()
                    }
                }
            }
        }
        return false
    }

}