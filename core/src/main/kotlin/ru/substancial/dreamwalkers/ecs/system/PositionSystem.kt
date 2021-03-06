package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import ru.substancial.dreamwalkers.ecs.component.BodyComponent
import ru.substancial.dreamwalkers.ecs.component.HurtboxComponent
import ru.substancial.dreamwalkers.ecs.component.PositionComponent
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.ecs.maybeExtract
import ru.substancial.dreamwalkers.utilities.RegisteringSystem

class PositionSystem : RegisteringSystem() {

    private val entities by multiple(family)

    override fun update(deltaTime: Float) {
        entities.forEach {
            val body = it.extract<BodyComponent>()
            val position = it.extract<PositionComponent>()
            position.xy.set(body.pushbox.worldCenter)
        }
    }

    companion object {

        private val family = Family.all(BodyComponent::class.java, PositionComponent::class.java).get()

    }

}
