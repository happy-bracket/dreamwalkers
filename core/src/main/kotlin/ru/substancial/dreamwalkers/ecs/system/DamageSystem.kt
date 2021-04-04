package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.physics.box2d.Body
import ru.substancial.dreamwalkers.ecs.component.BodyComponent
import ru.substancial.dreamwalkers.ecs.component.ContactComponent
import ru.substancial.dreamwalkers.ecs.component.HitboxComponent
import ru.substancial.dreamwalkers.ecs.component.HurtboxComponent
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
            { e -> tryProcessContactStart(e.extract()) },
            { e -> }
    )

    private fun tryProcessContactStart(contact: ContactComponent) {
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
        val hurtboxFragment = hurtbox.hurtboxes.getValue(hurtboxBody)
        val hitboxBody = hitboxEntity.extract<BodyComponent>().pushbox
        if (hitboxBody.linearVelocity.len() < hurtboxFragment.armorImpact) {
            hitboxBody.setVelocityViaImpulse(hitboxBody.linearVelocity.cpy().scl(-1f))
        }
    }

}
