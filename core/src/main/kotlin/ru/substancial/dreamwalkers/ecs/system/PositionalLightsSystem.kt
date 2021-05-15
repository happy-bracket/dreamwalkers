package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Family
import ru.substancial.dreamwalkers.ecs.component.LightComponent
import ru.substancial.dreamwalkers.ecs.component.PositionComponent
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.utilities.RegisteringSystem

class PositionalLightsSystem : RegisteringSystem() {

    private val positionalLights by multiple(Family.all(LightComponent::class.java, PositionComponent::class.java).get())

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        positionalLights.forEach {
            val position = it.extract<PositionComponent>()
            val light = it.extract<LightComponent>()
            light.light.position = position.xy
        }
    }

}
