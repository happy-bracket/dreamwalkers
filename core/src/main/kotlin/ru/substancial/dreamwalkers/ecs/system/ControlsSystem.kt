package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ru.substancial.dreamwalkers.controls.GroundController
import ru.substancial.dreamwalkers.ecs.component.AerialComponent
import ru.substancial.dreamwalkers.ecs.component.BodyComponent
import ru.substancial.dreamwalkers.ecs.component.LunaComponent
import ru.substancial.dreamwalkers.ecs.get
import ru.substancial.dreamwalkers.ecs.ComponentExtractor as CE

class ControlsSystem(
        private val movementController: GroundController
) : IteratingSystem(family) {

    override fun processEntity(luna: Entity, deltaTime: Float) {
        val direction = movementController.pollDirection()
        val (body) = luna[CE.Body]
        val (airborne) = luna[CE.Body]

        body.applyForceToCenter(direction * 5f, 0f, true)
    }

    override fun update(deltaTime: Float) {
    }

    companion object {

        private val family = Family.all(
                LunaComponent::class.java,
                AerialComponent::class.java,
                BodyComponent::class.java
        ).get()

    }

}