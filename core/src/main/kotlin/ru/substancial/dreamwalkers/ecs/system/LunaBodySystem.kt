package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.math.Vector2
import ru.substancial.dreamwalkers.ecs.component.AerialComponent
import ru.substancial.dreamwalkers.ecs.component.BodyComponent
import ru.substancial.dreamwalkers.ecs.component.InputComponent
import ru.substancial.dreamwalkers.ecs.component.LunaComponent
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.utilities.RegisteringSystem
import ru.substancial.dreamwalkers.utilities.checkDeadzone
import ru.substancial.dreamwalkers.utilities.setVelocityViaImpulse

class LunaBodySystem : RegisteringSystem() {

    private val luna by singular(lunaFamily)
    private val input: Entity by singular(
            inputFamily,
            onAdded = {
                val input = it.extract<InputComponent>()
                input.leftTriggerListener = { isDown ->
                    if (isDown) leftTriggerDown() else leftTriggerUp()
                }
            },
            onRemoved = {
                val input = it.extract<InputComponent>()
                input.leftTriggerListener = {}
            }
    )

    override fun update(deltaTime: Float) {
        val actualInput = input.extract<InputComponent>()
        val lunaBody = luna.extract<BodyComponent>().body
        val aerial = luna.extract<AerialComponent>()

        if (!aerial.isAirborne) {
            val ls = actualInput.leftStick.cpy()
            val pullForce = ls.scl(1f, 0f).nor().scl(7500f)
            if (!actualInput.leftStick.isZero) {
                lunaBody.applyForceToCenter(pullForce, true)
                if (lunaBody.linearVelocity.len2() >= 56.25) {
                    lunaBody.applyForceToCenter(pullForce.scl(-1f), true)
                }
            } else {
                val stoppingForce = lunaBody.linearVelocity.cpy().scl(-1f, 0f).nor().scl(7500f)
                lunaBody.applyForceToCenter(stoppingForce, true)
            }
        } else {
            if (actualInput.leftTriggerDown)
                lunaBody.applyForceToCenter(Vector2(0f, 8f), true)
        }
    }

    private fun leftTriggerDown() {
        val aerial = luna.extract<AerialComponent>()
        val input = input.extract<InputComponent>()
        if (aerial.isAirborne) {
            val rawDir = input.leftStick
            if (rawDir.checkDeadzone(0.3f)) {
                val direction = rawDir.nor().scl(8f)
                luna.extract<BodyComponent>().body.setVelocityViaImpulse(direction)
            }
        }
    }

    private fun leftTriggerUp() {
        val aerial = luna.extract<AerialComponent>()
        if (!aerial.isAirborne) {
            luna.extract<BodyComponent>().body.setVelocityViaImpulse(Vector2(0f, 10f))
        }
    }

    companion object {

        private val lunaFamily = Family.all(
                LunaComponent::class.java,
                AerialComponent::class.java,
                BodyComponent::class.java
        ).get()

        private val inputFamily = Family.all(InputComponent::class.java).get()

    }

}