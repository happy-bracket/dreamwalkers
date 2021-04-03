package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.physics.box2d.World
import ru.substancial.dreamwalkers.ecs.component.BodyComponent
import ru.substancial.dreamwalkers.ecs.component.HurtboxComponent
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.ecs.maybeExtract
import ru.substancial.dreamwalkers.utilities.RegisteringSystem

class WorldSystem(
        private val world: World
) : RegisteringSystem() {

    private val bodyListener by listener(
            Family.all(BodyComponent::class.java).get(),
            Unit,
            { _, _ -> },
            { _, e ->
                world.destroyBody(e.extract<BodyComponent>().pushbox)
                e.maybeExtract<HurtboxComponent>()?.hurtboxes?.keys?.forEach(world::destroyBody)
            },
            {}
    )

    override fun update(deltaTime: Float) {
        world.step(1 / 60f, 6, 2)
    }

}
