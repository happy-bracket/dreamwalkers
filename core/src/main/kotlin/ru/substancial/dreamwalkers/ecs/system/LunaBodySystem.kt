package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.math.Vector2
import ru.substancial.dreamwalkers.controls.TheController
import ru.substancial.dreamwalkers.ecs.component.*
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.utilities.RegisteringSystem
import ru.substancial.dreamwalkers.utilities.addVelocityViaImpulse
import ru.substancial.dreamwalkers.utilities.cancelGravity
import ru.substancial.dreamwalkers.utilities.checkDeadzone

class LunaBodySystem(
        private val controller: TheController
) : RegisteringSystem() {

    private val luna by optional(lunaFamily)

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        controller.leftTriggerDownListener = ::leftTriggerDown
        controller.leftTriggerUpListener = ::leftTriggerUp
    }

    override fun update(deltaTime: Float) {
        val luna = this.luna ?: return
        val lunaBody = luna.extract<BodyComponent>().pushbox
        val aerial = luna.extract<AerialComponent>()

        if (!aerial.isAirborne) {
            val ls = controller.leftStick
            val movement = luna.extract<TerrainMovementComponent>()
            val pullForce = ls.scl(1f, 0f).nor().scl(movement.pullForceMagnitude)
            if (!controller.leftStick.isZero) {
                movement.desiresToMove = true
                lunaBody.applyForceToCenter(pullForce, true)
            } else {
                movement.desiresToMove = false
            }
        } else {
            if (controller.leftTriggerDown) {
                val forces = luna.extract<ForcesComponent>().forces
                forces[FloatPressed] = PendingForce(lunaBody.cancelGravity().scl(0.7f), lunaBody.massData.center)
            }
        }
    }

    private fun leftTriggerDown() {
        val luna = this.luna ?: return
        val aerial = luna.extract<AerialComponent>()
        if (aerial.isAirborne) {
            val rawDir = controller.leftStick
            val dash = luna.extract<DashComponent>()
            if (rawDir.checkDeadzone(0.3f) && dash.isAvailable()) {
                val direction = rawDir.nor().scl(8f)
                luna.extract<BodyComponent>().pushbox.addVelocityViaImpulse(direction)
                dash.startCooldown()
            }
        } else {
            luna.extract<BodyComponent>().pushbox.addVelocityViaImpulse(Vector2(0f, 10f))
        }
    }

    private fun leftTriggerUp() {
        val luna = this.luna ?: return
        val aerial = luna.extract<AerialComponent>()
        if (!aerial.isAirborne) {
        }
    }

    companion object {

        private val lunaFamily = Family.all(
                LunaComponent::class.java,
                AerialComponent::class.java,
                ForcesComponent::class.java,
                BodyComponent::class.java
        ).get()

    }

}

object FloatPressed : Reason
object TerrainMovement : Reason
