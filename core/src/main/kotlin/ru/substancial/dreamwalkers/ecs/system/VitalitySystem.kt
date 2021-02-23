package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Family
import ru.substancial.dreamwalkers.ecs.component.VitalityComponent
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.utilities.RegisteringSystem

class VitalitySystem : RegisteringSystem() {

    private val entities by multiple(Family.all(VitalityComponent::class.java).get())

    override fun update(deltaTime: Float) {
        entities.forEach { entity ->
            val vitality = entity.extract<VitalityComponent>()
            if (vitality.vitalityLevel <= vitality.terminalThreshold) {
                engine.removeEntity(entity)
            }
        }
    }

}
