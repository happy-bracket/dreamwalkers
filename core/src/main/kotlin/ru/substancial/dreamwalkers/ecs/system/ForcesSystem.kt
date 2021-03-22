package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Family
import ru.substancial.dreamwalkers.ecs.component.BodyComponent
import ru.substancial.dreamwalkers.ecs.component.ForcesComponent
import ru.substancial.dreamwalkers.ecs.component1
import ru.substancial.dreamwalkers.ecs.component2
import ru.substancial.dreamwalkers.utilities.RegisteringSystem

class ForcesSystem : RegisteringSystem() {

    private val entities by multiple(
            Family.all(BodyComponent::class.java, ForcesComponent::class.java).get()
    )

    override fun update(deltaTime: Float) {
        entities.forEach {
            val (bc: BodyComponent, fc: ForcesComponent) = it
            fc.forces.values.forEach { force ->
                bc.pushbox.applyForce(force.vector, force.applicationPoint, true)
            }
        }
    }

}
