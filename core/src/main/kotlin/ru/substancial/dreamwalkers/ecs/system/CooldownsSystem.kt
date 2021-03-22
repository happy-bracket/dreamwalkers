package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Family
import ru.substancial.dreamwalkers.ecs.component.Cooldown
import ru.substancial.dreamwalkers.ecs.component.DashComponent
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.utilities.RegisteringSystem

class CooldownsSystem : RegisteringSystem() {

    private val entities by multiple(
            Family.all(DashComponent::class.java).get()
    )

    override fun update(deltaTime: Float) {
        entities.forEach {
            it.extract<DashComponent>().let { dash ->
                dash.tickAway(deltaTime)
            }
        }
    }

}
