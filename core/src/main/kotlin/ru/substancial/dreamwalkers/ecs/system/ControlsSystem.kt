package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import ru.substancial.dreamwalkers.controls.AerialController
import ru.substancial.dreamwalkers.controls.GroundController
import ru.substancial.dreamwalkers.ecs.component.AerialComponent
import ru.substancial.dreamwalkers.ecs.component.BodyComponent
import ru.substancial.dreamwalkers.ecs.component.LunaComponent
import ru.substancial.dreamwalkers.ecs.get
import ru.substancial.dreamwalkers.ecs.ComponentExtractor as CE

class ControlsSystem(
        private val movementController: GroundController,
        private val aerialController: AerialController
) : IteratingSystem(family) {

    init {
        movementController.jumpClicked = {
            jumpScheduled = true
        }
    }

    private var jumpScheduled = false

    override fun processEntity(luna: Entity, deltaTime: Float) {
        val (body) = luna[CE.Body]
        val (airborne) = luna[CE.Aerial]

        if (!airborne) {
            val direction = movementController.pollDirection()
            body.applyForceToCenter(direction * 5f, 0f, true)
            if (jumpScheduled) {
                jumpScheduled = false
                body.applyLinearImpulse(0f, 10f, body.worldCenter.x, body.worldCenter.y, true)
            }
        }

    }

    companion object {

        private val family = Family.all(
                LunaComponent::class.java,
                AerialComponent::class.java,
                BodyComponent::class.java
        ).get()

    }

}