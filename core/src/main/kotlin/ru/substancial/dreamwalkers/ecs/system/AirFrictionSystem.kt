package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Family
import ru.substancial.dreamwalkers.ecs.component.AerialComponent
import ru.substancial.dreamwalkers.ecs.component.BodyComponent
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.utilities.RegisteringSystem

class AirFrictionSystem : RegisteringSystem() {

    private val entities by multiple(
            Family.all(AerialComponent::class.java, BodyComponent::class.java).get()
    )

    override fun update(deltaTime: Float) {
        entities.forEach {
            val ac = it.extract<AerialComponent>()
            val bc = it.extract<BodyComponent>()
            if (bc.pushbox.linearVelocity.len2() >= ac.maxSpeed) {
                bc.pushbox.applyForceToCenter(bc.pushbox.linearVelocity.cpy().nor().scl(-ac.airFriction), false)
            }
        }
    }

}