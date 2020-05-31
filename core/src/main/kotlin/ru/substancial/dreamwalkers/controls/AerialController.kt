package ru.substancial.dreamwalkers.controls

import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerAdapter
import com.badlogic.gdx.math.Vector2
import ru.substancial.dreamwalkers.ecs.component.ButtonLayout
import ru.substancial.dreamwalkers.ecs.component.ButtonLayout.L2
import ru.substancial.dreamwalkers.ecs.component.ButtonLayout.LX
import ru.substancial.dreamwalkers.ecs.component.ButtonLayout.LY

class AerialController(
        deadzone: Float,
        private val buttonLayout: ButtonLayout
) : DeadzoneControllerAdapter(deadzone) {

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
        when (buttonIndex) {
            buttonLayout[L2] -> strokeListener()
        }
        return true
    }

    override fun reactToAxis(controller: Controller?, axisIndex: Int, value: Float) {
        when (axisIndex) {
            buttonLayout[LX] -> flightDirection.x = value
            buttonLayout[LY] -> flightDirection.y = value
            buttonLayout[L2] -> strokeListener()
        }
    }

}