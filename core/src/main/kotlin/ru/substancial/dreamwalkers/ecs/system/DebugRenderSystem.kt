package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Family
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import ru.substancial.dreamwalkers.ecs.component.CameraComponent
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.utilities.RegisteringSystem

class DebugRenderSystem(
        private val world: World,
        private val renderer: Box2DDebugRenderer
) : RegisteringSystem() {

    private val cameraEntity by singular(Family.all(CameraComponent::class.java).get())

    override fun update(deltaTime: Float) {
        renderer.render(world, cameraEntity.extract<CameraComponent>().camera.combined)
    }

}
