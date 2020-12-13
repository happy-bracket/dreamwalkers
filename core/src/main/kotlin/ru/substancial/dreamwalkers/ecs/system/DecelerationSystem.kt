package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Family
import com.badlogic.gdx.math.Vector2
import ru.substancial.dreamwalkers.ecs.component.AerialComponent
import ru.substancial.dreamwalkers.ecs.component.BodyComponent
import ru.substancial.dreamwalkers.ecs.component.MovementComponent
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.ecs.maybeExtract
import ru.substancial.dreamwalkers.utilities.RegisteringSystem
import ru.substancial.dreamwalkers.utilities.addVelocityViaImpulse

class DecelerationSystem : RegisteringSystem() {

    private val entities by multiple(Family.all(MovementComponent::class.java, BodyComponent::class.java).get())

    override fun update(deltaTime: Float) {
        val tmp = Vector2()
        entities.forEach { entity ->
            val movement = entity.extract<MovementComponent>()
            val body = entity.extract<BodyComponent>().body
            val aerial = entity.maybeExtract<AerialComponent>()
            val velocity2 = body.linearVelocity.len2()
            if (velocity2 >= movement.maxSpeed2 || !movement.desiresToMove) {
                if (aerial == null || !aerial.isAirborne) {
                    if (velocity2 in 0.01f..MIN_VELOCITY_THRESHOLD) {
                        body.addVelocityViaImpulse(body.linearVelocity.cpy().scl(-1f))
                    } else if (velocity2 >= MIN_VELOCITY_THRESHOLD) {
                        val stoppingForce = tmp.set(body.linearVelocity).nor().scl(-movement.pullForceMagnitude)
                        body.applyForceToCenter(stoppingForce, false)
                    }
                }
            }
        }
    }

    companion object {

        private const val MIN_VELOCITY_THRESHOLD = 1f

    }

}