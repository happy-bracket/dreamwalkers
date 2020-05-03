package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.physics.box2d.Body
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
) : EntitySystem() {

    init {
        movementController.jumpClicked = {
            if (!isAirborne()) {
                val body = extractBody()
                body.applyLinearImpulse(0f, 10f, body.worldCenter.x, body.worldCenter.y, true)
            }
        }

        aerialController.strokeListener = {
            if (isAirborne()) {
                val body = extractBody()
                val strDir = aerialController.getStrokeDirection()
                val stroke = strDir
                        .nor()
                        .scl(10f, -10f)
                body.applyLinearImpulse(stroke, body.worldCenter, true)
            }
        }
    }

    private var _luna: Entity? = null
    private val luna: Entity
        get() = _luna!!

    override fun addedToEngine(engine: Engine) {
        _luna = engine.getEntitiesFor(family).first()
    }

    override fun removedFromEngine(engine: Engine?) {
        _luna = null
        movementController.jumpClicked = {}
        aerialController.strokeListener = {}
    }

    override fun update(deltaTime: Float) {
        val body = extractBody()
        val (airborne) = luna[CE.Aerial]

        if (!airborne) {
            val direction = movementController.pollDirection()
            body.applyForceToCenter(direction * 5f, 0f, true)
        }
    }

    private fun extractBody(): Body = luna[CE.Body].body

    private fun isAirborne(): Boolean = luna[CE.Aerial].isAirborne

    companion object {

        private val family = Family.all(
                LunaComponent::class.java,
                AerialComponent::class.java,
                BodyComponent::class.java
        ).get()

    }

}