package ru.substancial.dreamwalkers.ecs.other

import com.badlogic.ashley.core.Entity
import ru.substancial.dreamwalkers.ecs.component.HitboxComponent
import ru.substancial.dreamwalkers.ecs.component.HurtboxComponent
import ru.substancial.dreamwalkers.ecs.component.VitalityComponent
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.ecs.has

class HitMediator {

    fun process(entityA: Entity, entityB: Entity) {
        val hitbox: Entity
        val hurtbox: Entity
        when {
            entityA.has<HitboxComponent>() && entityB.has<HurtboxComponent>() -> {
                hitbox = entityA
                hurtbox = entityB
            }
            entityA.has<HurtboxComponent>() && entityB.has<HitboxComponent>() -> {
                hitbox = entityB
                hurtbox = entityA
            }
            else -> return
        }
        if (hitbox.extract<HitboxComponent>().owner == hurtbox) return

        hurtbox.extract<VitalityComponent>().vitalityLevel -= 10
    }

}

sealed class DamageType {
    object Cut : DamageType()
    object Blunt : DamageType()
}
