package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Family
import ru.substancial.dreamwalkers.ecs.component.BodyComponent
import ru.substancial.dreamwalkers.ecs.component.HurtboxComponent
import ru.substancial.dreamwalkers.ecs.component.PositionComponent
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.utilities.RegisteringSystem

class HurtboxFollowSystem : RegisteringSystem() {

    private val entitiesWithHurtboxes by multiple(Family.all(PositionComponent::class.java, HurtboxComponent::class.java).get())

    override fun update(deltaTime: Float) {
        entitiesWithHurtboxes.forEach { e ->
            val pushboxXy = e.extract<PositionComponent>().xy
            e.extract<HurtboxComponent>().hurtboxes.keys.forEach { hurtbox ->
                hurtbox.setTransform(pushboxXy, hurtbox.angle)
            }
        }
    }

}
