package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World

class DebugRenderSystem(
        private val world: World,
        private val camera: Camera,
        private val renderer: Box2DDebugRenderer
) : EntitySystem() {

    override fun update(deltaTime: Float) {
        renderer.render(world, camera.combined)
    }

}