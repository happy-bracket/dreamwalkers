package ru.substancial.dreamwalkers.controls

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerAdapter
import com.badlogic.gdx.math.Vector2
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

    private var _leftTriggerDown = false
    val leftTriggerDown: Boolean
        get() = _leftTriggerDown

    var leftTriggerDownListener = {}
    var leftTriggerUpListener = {}

    private var _rightTriggerDown = false
    val rightTriggerDown: Boolean
        get() = _rightTriggerDown

    var rightTriggerDownListener = {}
    var rightTriggerUpListener = {}

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
                _leftStick.y = -value
            }
            4 -> {
                if (value >= 0.4f) {
                    if (!_leftTriggerDown) {
                        _leftTriggerDown = true
                        leftTriggerDownListener()
                    }
                } else {
                    if (_leftTriggerDown) {
                        _leftTriggerDown = false
                        leftTriggerUpListener()
                    }
                }
            }
            5 -> {
                if (value >= 0.4f) {
                    if (!_rightTriggerDown) {
                        _rightTriggerDown = true
                        rightTriggerDownListener()
                    }
                } else {
                    if (_rightTriggerDown) {
                        _rightTriggerDown = false
                        rightTriggerUpListener()
                    }
                }
            }
            m.axisRightX -> {
                _rightStick.x = value
            }
            m.axisRightY -> {
                _rightStick.y = -value
            }
        }
        return false
    }

}
