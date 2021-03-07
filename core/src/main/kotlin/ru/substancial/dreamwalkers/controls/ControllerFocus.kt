package ru.substancial.dreamwalkers.controls

import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerAdapter
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.*
import ru.substancial.dreamwalkers.utilities.checkDeadzone

class ControllerFocus(
        targets: List<Actor>
) : ControllerAdapter() {

    private var heldFor: Float = 0f

    private val input: Vector2 = Vector2()

    private var focused: LURDGraph<Actor>

    init {
        val ltr = targets.sortedBy { it.x }
        val utd = targets.sortedBy { it.y }
        val raw = targets.map { LURDGraph(it) }
        raw.forEach { current ->
            val ltrIx = ltr.indexOf(current.content)
            val utdIx = utd.indexOf(current.content)
            val unwrappedLeft = ltr.getOrNull(ltrIx - 1)
            val unwrappedUp = utd.getOrNull(utdIx + 1)
            val unwrappedRight = ltr.getOrNull(ltrIx + 1)
            val unwrappedDown = utd.getOrNull(utdIx - 1)
            current.left = raw.firstOrNull { it.content == unwrappedLeft }
            current.up = raw.firstOrNull { it.content == unwrappedUp }
            current.right = raw.firstOrNull { it.content == unwrappedRight }
            current.down = raw.firstOrNull { it.content == unwrappedDown }
        }
        focused = raw.first()
    }

    private class LURDGraph<T>(
            val content: T,
            var left: LURDGraph<T>? = null,
            var up: LURDGraph<T>? = null,
            var right: LURDGraph<T>? = null,
            var down: LURDGraph<T>? = null
    )

    override fun axisMoved(controller: Controller, axisIndex: Int, value: Float): Boolean {
        val m = controller.mapping
        when (axisIndex) {
            m.axisLeftX -> {
                input.x = value
            }
            m.axisLeftY -> {
                input.y = -value
            }
        }
        return false
    }

    override fun buttonUp(controller: Controller, buttonIndex: Int): Boolean {
        val m = controller.mapping
        when (buttonIndex) {
            m.buttonA -> {
                val event = InputEvent()
                event.type = InputEvent.Type.keyUp
                focused.content.fire(event)
            }
        }
        return false
    }

    fun update(delta: Float) {
        if (input.checkDeadzone(0.3f)) {
            if (heldFor == 0f) {
                when {
                    input.x > 0 -> focused.right?.let { focused = it }
                    input.x < 0 -> focused.left?.let { focused = it }
                }
                when {
                    input.y > 0 -> focused.up?.let { focused = it }
                    input.y < 0 -> focused.down?.let { focused = it }
                }
            }
            heldFor += delta
        } else {
            heldFor = 0f
        }
        if (heldFor >= AVALANCHE_THRESHOLD) {
            heldFor = 0f
        }
    }

    private companion object {

        private const val AVALANCHE_THRESHOLD = 0.6f

    }
}