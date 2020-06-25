package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import ru.substancial.dreamwalkers.ecs.component.BodyComponent
import ru.substancial.dreamwalkers.ecs.component.LunaComponent
import ru.substancial.dreamwalkers.ecs.get
import ru.substancial.dreamwalkers.ecs.ComponentExtractor as CE

class CameraSystem(
        private val camera: OrthographicCamera
) : EntitySystem() {

    private var luna: Entity? = null

    init {
        camera.zoom = 2f
    }

    override fun addedToEngine(engine: Engine?) {
        luna = engine?.getEntitiesFor(family)?.first()
    }

    override fun removedFromEngine(engine: Engine?) {
        luna = null
    }

    override fun update(deltaTime: Float) {
        val (body) = luna!![CE.Body]/*
        camera.position.set(body.worldCenter, 0f)
        camera.update()*/
    }

    companion object {

        private val family = Family.all(
                LunaComponent::class.java,
                BodyComponent::class.java
        ).get()

    }

}