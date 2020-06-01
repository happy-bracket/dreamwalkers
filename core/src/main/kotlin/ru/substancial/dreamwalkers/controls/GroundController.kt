package ru.substancial.dreamwalkers.controls

import com.badlogic.gdx.controllers.Controller
import ru.substancial.dreamwalkers.ecs.component.ButtonLayout
import ru.substancial.dreamwalkers.ecs.component.ButtonLayout.L2
import ru.substancial.dreamwalkers.ecs.component.ButtonLayout.LX
import kotlin.math.round

class GroundController(deadzone: Float, private val buttonLayout: ButtonLayout) : DeadzoneControllerAdapter(deadzone) {

    private var accumulator: Float = 0f
    var jumpClicked: () -> Unit = {}

    private val leftTrigger = TriggerAsButton(0.4f)

    fun pollDirection(): Float {
        return accumulator
    }

    override fun buttonUp(controller: Controller?, buttonIndex: Int): Boolean {
        when (buttonIndex) {
            buttonLayout[L2] -> jumpClicked()
        }
        return true
    }

    override fun reactToAxis(controller: Controller?, axisIndex: Int, value: Float) {
        when (axisIndex) {
            buttonLayout[LX] -> accumulator = round(value)
            buttonLayout[L2] -> leftTrigger.process(value, jumpClicked)
        }
    }

}