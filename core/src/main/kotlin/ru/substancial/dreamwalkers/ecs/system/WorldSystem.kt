package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.gdx.physics.box2d.World
import ru.substancial.dreamwalkers.utilities.RegisteringSystem

class WorldSystem(
        private val world: World
) : RegisteringSystem() {

    override fun update(deltaTime: Float) {
        world.step(1 / 60f, 6, 2)
    }

}
