package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.graphics.OrthographicCamera
import ru.substancial.dreamwalkers.ecs.component.BodyComponent
import ru.substancial.dreamwalkers.ecs.component.CameraComponent
import ru.substancial.dreamwalkers.ecs.component.LunaComponent
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.utilities.RegisteringSystem

class CameraSystem : RegisteringSystem() {

    private val cameraEntity by singular(Family.all(CameraComponent::class.java).get())
    private val luna by optional(family)

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        cameraEntity.extract<CameraComponent>().camera.zoom = 2f
    }

    override fun update(deltaTime: Float) {
        luna?.extract<BodyComponent>()?.let { (body) ->
            val camera = cameraEntity.extract<CameraComponent>().camera
            camera.position.set(body.worldCenter, 0f)
            camera.update()
        }
    }

    fun setZoom(zoom: Float) {
        val camera = cameraEntity.extract<CameraComponent>().camera
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
