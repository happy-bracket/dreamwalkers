package ru.substancial.dreamwalkers.controls

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
    var aListener = {}
    var bListener = {}
    var xListener = {}
    var yListener = {}
    var r1Listener = {}

    override fun buttonUp(controller: Controller?, buttonIndex: Int): Boolean {
        switchControllerIfNeeded(controller)
        val m = latestUsedController?.mapping ?: return false
        when (buttonIndex) {
            m.buttonA -> aListener()
            m.buttonB -> bListener()
            m.buttonX -> xListener()
            m.buttonY -> yListener()
            m.buttonR1 -> r1Listener()
        }
        return false
    }

    override fun axisMoved(controller: Controller?, axisIndex: Int, value: Float): Boolean {
        switchControllerIfNeeded(controller)
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

    private fun switchControllerIfNeeded(controller: Controller?) {
        if (latestUsedController?.uniqueId != controller?.uniqueId) {
            latestUsedController = controller
        }
    }

}
