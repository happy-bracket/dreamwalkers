package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import ru.substancial.dreamwalkers.controls.LeftStickController
import ru.substancial.dreamwalkers.ecs.component.BodyComponent
import ru.substancial.dreamwalkers.ecs.component.LunaComponent
import ru.substancial.dreamwalkers.ecs.get
import ru.substancial.dreamwalkers.ecs.ComponentExtractor as CE

class ControlsSystem(
        private val movementController: LeftStickController
) : EntitySystem() {

    private var luna: Entity? = null

    override fun addedToEngine(engine: Engine?) {
        luna = engine?.getEntitiesFor(family)?.first()
    }

    override fun removedFromEngine(engine: Engine?) {
        luna = null
    }

    override fun update(deltaTime: Float) {
        val direction = movementController.pollDirection()
        val (body) = luna!![CE.Body]
        body.applyForceToCenter(direction * 5f, 0f, true)
    }

    companion object {

        private val family = Family.all(
                LunaComponent::class.java,
                BodyComponent::class.java
        ).get()

    }

}