package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Family
import com.badlogic.gdx.graphics.OrthographicCamera
import ru.substancial.dreamwalkers.ecs.component.BodyComponent
import ru.substancial.dreamwalkers.ecs.component.LunaComponent
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.utilities.RegisteringSystem

class CameraSystem(
        private val camera: OrthographicCamera
) : RegisteringSystem() {

    private val luna by multiple(family)

    init {
        camera.zoom = 20f
    }

    override fun update(deltaTime: Float) {
        val (body) = luna.first().extract<BodyComponent>()
        camera.position.set(body.worldCenter, 0f)
        camera.update()
    }

    fun setZoom(zoom: Float) {
        camera.zoom = zoom
        camera.update()
    }

    companion object {

        private val family = Family.all(
                LunaComponent::class.java,
                BodyComponent::class.java
        ).get()

    }

}