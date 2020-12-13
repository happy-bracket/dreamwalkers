package ru.substancial.dreamwalkers.controls

import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerAdapter
import com.badlogic.gdx.math.Vector2
import com.studiohartman.jamepad.ControllerAxis
import ru.substancial.dreamwalkers.utilities.checkDeadzone

class TheController : ControllerAdapter() {

    private var latestUsedController: Controller? = null

    private val _leftStick = Vector2()
    val leftStick: Vector2
        get() = if (_leftStick.checkDeadzone(0.3f)) {
            _leftStick.cpy()
        } else Vector2()

    private val _rightStick = Vector2()
    val rightStick: Vector2
        get() = if (_rightStick.checkDeadzone(0.3f)) {
            _rightStick.cpy()
        } else Vector2()

    private var _airTriggerDown = false
    val airTriggerDown: Boolean
        get() = _airTriggerDown

    var airTriggerDownListener = {}
    var airTriggerUpListener = {}

    override fun axisMoved(controller: Controller?, axisIndex: Int, value: Float): Boolean {
        if (latestUsedController?.uniqueId != controller?.uniqueId) {
            latestUsedController = controller
        }
        val m = latestUsedController?.mapping ?: return false
        when (axisIndex) {
            m.axisLeftX -> {
                _leftStick.x = value
            }
            m.axisLeftY -> {
                _leftStick.y = value
            }
            ControllerAxis.TRIGGERLEFT.ordinal -> {
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
            m.axisRightX -> {
                _rightStick.x = value
            }
            m.axisRightY -> {
                _rightStick.y = value
            }
        }
        return false
    }

}