package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.physics.box2d.World
import ru.substancial.dreamwalkers.ecs.component.BodyComponent
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.utilities.RegisteringSystem

class WorldSystem(
        private val world: World
) : RegisteringSystem() {

    private var listener: EntityListener? = null

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        val newListener = Listener()
        engine.addEntityListener(Family.all(BodyComponent::class.java).get(), newListener)
        listener = newListener
    }

    override fun removedFromEngine(engine: Engine) {
        super.removedFromEngine(engine)
        engine.removeEntityListener(listener)
        listener = null
    }

    override fun update(deltaTime: Float) {
        world.step(1 / 60f, 6, 2)
    }

    private inner class Listener : EntityListener {

        override fun entityAdded(entity: Entity) {}

        override fun entityRemoved(entity: Entity) {
            val body = entity.extract<BodyComponent>().body
            world.destroyBody(body)
        }

    }

}