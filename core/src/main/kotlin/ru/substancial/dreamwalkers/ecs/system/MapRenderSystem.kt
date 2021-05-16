package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Family
import ru.substancial.dreamwalkers.ecs.component.CameraComponent
import ru.substancial.dreamwalkers.ecs.component.LevelComponent
import ru.substancial.dreamwalkers.ecs.component.MapRendererComponent
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.utilities.RegisteringSystem

class MapRenderSystem : RegisteringSystem() {

    private val level by optional(Family.all(LevelComponent::class.java).get())
    private val renderer by optional(Family.all(MapRendererComponent::class.java).get())
    private val camera by singular(Family.all(CameraComponent::class.java).get())

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        level?.extract<LevelComponent>()?.level?.let { level ->
            renderer?.extract<MapRendererComponent>()?.renderer?.let { renderer ->
                renderer.setView(camera.extract<CameraComponent>().camera)
                renderer.renderObjects(level.rigidMapTextures)
            }
        }
    }

}
