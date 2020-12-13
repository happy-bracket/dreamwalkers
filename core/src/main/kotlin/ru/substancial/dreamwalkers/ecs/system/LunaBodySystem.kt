package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.math.Vector2
import ru.substancial.dreamwalkers.controls.TheController
import ru.substancial.dreamwalkers.ecs.component.*
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.utilities.RegisteringSystem
import ru.substancial.dreamwalkers.utilities.checkDeadzone
import ru.substancial.dreamwalkers.utilities.setVelocityViaImpulse

class LunaBodySystem(
        private val controller: TheController
) : RegisteringSystem() {

    private val luna by singular(lunaFamily)

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        controller.airTriggerDownListener = ::leftTriggerDown
        controller.airTriggerUpListener = ::leftTriggerUp
    }

    override fun update(deltaTime: Float) {
        val lunaBody = luna.extract<BodyComponent>().body
        val aerial = luna.extract<AerialComponent>()

        if (!aerial.isAirborne) {
            lunaBody.gravityScale = 1f
            val ls = controller.leftStick
            val movement = luna.extract<MovementComponent>()
            val pullForce = ls.scl(1f, 0f).nor().scl(movement.pullForceMagnitude)
            if (!controller.leftStick.isZero) {
                movement.desiresToMove = true
                lunaBody.applyForceToCenter(pullForce, true)
            } else {
                movement.desiresToMove = false
            }
        } else {
            if (controller.airTriggerDown) {
                lunaBody.gravityScale = 0.1f
            } else {
                lunaBody.gravityScale = 1f
            }
        }
    }

    private fun leftTriggerDown() {
        val aerial = luna.extract<AerialComponent>()
        if (aerial.isAirborne) {
            val rawDir = controller.leftStick
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

    }

}