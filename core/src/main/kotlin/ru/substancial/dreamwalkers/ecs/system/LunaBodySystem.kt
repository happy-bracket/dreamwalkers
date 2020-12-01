package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import ru.substancial.dreamwalkers.ecs.component.*
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
            lunaBody.gravityScale = 1f
            val ls = actualInput.leftStick.cpy()
            val movement = luna.extract<MovementComponent>()
            val pullForce = ls.scl(1f, 0f).nor().scl(movement.pullForceMagnitude)
            if (!actualInput.leftStick.isZero) {
                movement.desiresToMove = true
                lunaBody.applyForceToCenter(pullForce, true)
            } else {
                movement.desiresToMove = false
            }
        } else {
            if (actualInput.leftTriggerDown) {
                lunaBody.gravityScale = 0.1f
            } else {
                lunaBody.gravityScale = 1f
            }
        }
    }

    private fun leftTriggerDown() {
        val aerial = luna.extract<AerialComponent>()
        val input = input.extract<InputComponent>()
        if (aerial.isAirborne) {
            val rawDir = input.leftStick
            if (rawDir.checkDeadzone(0.3f)) {
                val direction = rawDir.nor().scl(8f)
                Gdx.app.log("egor2", direction.toString())
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