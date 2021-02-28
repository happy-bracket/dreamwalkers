package ru.substancial.dreamwalkers.ecs.other

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Contact
import ru.substancial.dreamwalkers.ecs.component.HitboxComponent
import ru.substancial.dreamwalkers.ecs.component.HurtboxComponent
import ru.substancial.dreamwalkers.ecs.component.IdentityComponent
import ru.substancial.dreamwalkers.ecs.component.VitalityComponent
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.ecs.has
import ru.substancial.dreamwalkers.ecs.maybeExtract
import ru.substancial.dreamwalkers.physics.entity

class HitMediator {

    fun process(contact: Contact, begin: Boolean) {
        val fixtureA = contact.fixtureA
        val fixtureB = contact.fixtureB
        val bodyA = fixtureA.body
        val bodyB = fixtureB.body
        val entityA = bodyA.entity
        val entityB = bodyB.entity

        val hit: Entity
        val hurt: Entity

        when {
            checkHitContact(entityA, entityB, bodyB) -> {
                hit = entityA
                hurt = entityB
            }
            checkHitContact(entityB, entityA, bodyA) -> {
                hit = entityB
                hurt = entityA
            }
            else -> return
        }

        if (hit.extract<HitboxComponent>().owner == hurt) return

        val hurtboxComponent = hurt.extract<HurtboxComponent>()
        if (begin) {
            if (hit in hurtboxComponent.hitBy) return
            hurtboxComponent.hitBy.add(hit)
            hurt.extract<VitalityComponent>().vitalityLevel -= 10
        } else {
            hurtboxComponent.hitBy.remove(hit)
        }
        if (hit in hurtboxComponent.hitBy) return

    }

    private fun checkHitContact(assumedHitbox: Entity, assumedHurtbox: Entity, testedBody: Body): Boolean {
        return assumedHitbox.has<HitboxComponent>() && assumedHurtbox.maybeExtract<HurtboxComponent>()?.let { testedBody in it.hurtboxes } == true
    }

}

sealed class DamageType {
    object Cut : DamageType()
    object Blunt : DamageType()
}
