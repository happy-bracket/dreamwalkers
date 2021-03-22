package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Family
import ru.substancial.dreamwalkers.ecs.component.*
import ru.substancial.dreamwalkers.ecs.component1
import ru.substancial.dreamwalkers.ecs.component2
import ru.substancial.dreamwalkers.ecs.component3
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.utilities.RegisteringSystem

class AirFrictionSystem : RegisteringSystem() {

    private val entities by multiple(
            Family.all(
                    AerialComponent::class.java,
                    ForcesComponent::class.java,
                    BodyComponent::class.java
            ).get()
    )

    override fun update(deltaTime: Float) {
        entities.forEach {
            val (ac: AerialComponent, bc: BodyComponent, fc: ForcesComponent) = it
            if (ac.isAirborne && bc.pushbox.linearVelocity.len2() >= ac.maxSpeed) {
                fc.forces[AirFriction] = PendingForce(bc.pushbox.linearVelocity.cpy().nor().scl(-ac.airFriction), AirFriction)
            } else {
                fc.forces.remove(AirFriction)
            }
        }
    }

}

object AirFriction : Reason
