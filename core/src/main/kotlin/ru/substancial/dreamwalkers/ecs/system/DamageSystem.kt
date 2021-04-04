package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.physics.box2d.Body
import ru.substancial.dreamwalkers.ecs.component.*
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.ecs.has
import ru.substancial.dreamwalkers.physics.entity
import ru.substancial.dreamwalkers.utilities.RegisteringSystem
import ru.substancial.dreamwalkers.utilities.addVelocityViaImpulse
import ru.substancial.dreamwalkers.utilities.justListen
import ru.substancial.dreamwalkers.utilities.setVelocityViaImpulse

class DamageSystem : RegisteringSystem() {

    private val listener by justListen(
            Family.all(ContactComponent::class.java).get(),
            { e -> tryProcessContact(e.extract(), true) },
            { e -> tryProcessContact(e.extract(), false) }
    )

    private fun tryProcessContact(contact: ContactComponent, begin: Boolean) {
        if (!contact.isTouching) return
        val fa = contact.fixtureA
        val fb = contact.fixtureB
        val ea = fa.body.entity
        val eb = fb.body.entity
        val hitboxEntity: Entity
        val hurtboxEntity: Entity
        val hurtboxBody: Body
        when {
            ea.has<HurtboxComponent>() && eb.has<HitboxComponent>() -> {
                hurtboxEntity = ea
                hitboxEntity = eb
                hurtboxBody = fa.body
            }
            ea.has<HitboxComponent>() && eb.has<HurtboxComponent>() -> {
                hurtboxEntity = eb
                hitboxEntity = ea
                hurtboxBody = fb.body
            }
            else -> return
        }
        val hitbox = hitboxEntity.extract<HitboxComponent>()
        val hurtbox = hurtboxEntity.extract<HurtboxComponent>()
        if (hitbox.owner === hurtboxEntity) return
        if (begin) {
            if (hurtbox.hitBy.contains(hitboxEntity)) return
            hurtbox.hitBy.add(hitboxEntity)
            val hurtboxFragment = hurtbox.hurtboxes.getValue(hurtboxBody)
            val hitboxBody = hitboxEntity.extract<BodyComponent>().pushbox
            if (hurtboxFragment.armor is ArmorProperties.HasArmor && hitboxBody.linearVelocity.len() < hurtboxFragment.armor.criticalImpact) {
                hitboxBody.setVelocityViaImpulse(hitboxBody.linearVelocity.cpy().setLength(hurtboxFragment.armor.restitution).scl(-1f))
                return
            }
        } else {
            hurtbox.hitBy.remove(hitboxEntity)
        }
    }

}
