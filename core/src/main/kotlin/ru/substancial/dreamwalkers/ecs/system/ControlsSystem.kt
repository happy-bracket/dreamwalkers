package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import ru.substancial.dreamwalkers.controls.AerialController
import ru.substancial.dreamwalkers.controls.GroundController
import ru.substancial.dreamwalkers.controls.TheController
import ru.substancial.dreamwalkers.ecs.component.AerialComponent
import ru.substancial.dreamwalkers.ecs.component.BodyComponent
import ru.substancial.dreamwalkers.ecs.component.LunaComponent
import ru.substancial.dreamwalkers.ecs.get
import ru.substancial.dreamwalkers.utilities.applyImpulseToCenter
import ru.substancial.dreamwalkers.utilities.checkDeadzone
import ru.substancial.dreamwalkers.ecs.ComponentExtractor as CE

class ControlsSystem(
        private val controller: TheController
) : EntitySystem() {

    init {
        controller.airTriggerDownListener = {
            if (isAirborne()) {
                val rawDir = controller.pollLeftStick()
                if (rawDir.checkDeadzone(0.3f)) {
                    val direction = rawDir.nor().scl(8f, 8f)
                    luna[CE.Body].body.applyImpulseToCenter(direction, true)
                }
            }
        }

        controller.airTriggerUpListener = {
            if (!isAirborne()) {
                luna[CE.Body].body.applyImpulseToCenter(Vector2(0f, 10f), true)
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
        controller.airTriggerDownListener = {}
        controller.airTriggerUpListener = {}
    }

    override fun update(deltaTime: Float) {
        val body = extractBody()
        val (airborne) = luna[CE.Aerial]

        if (!airborne) {
            val direction = controller.pollLeftStick()
                    .nor()
                    .scl(5f, 0f)
            body.applyForceToCenter(direction, true)
        } else {
            body.applyForceToCenter(Vector2(0f, 8f), true)
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