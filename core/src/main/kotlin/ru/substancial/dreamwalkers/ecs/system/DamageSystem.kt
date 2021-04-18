package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.scenes.scene2d.ui.Label
import ru.substancial.dreamwalkers.ecs.component.*
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.ecs.has
import ru.substancial.dreamwalkers.physics.entity
import ru.substancial.dreamwalkers.utilities.RegisteringSystem
import ru.substancial.dreamwalkers.utilities.setVelocityViaImpulse
import java.util.*

class DamageSystem : RegisteringSystem() {

    private val listener by listener(
        Family.all(ContactComponent::class.java).get(),
        LinkedList<OpeningWound>(),
        { ws, e -> tryProcessContact(ws, e.extract(), true) },
        { ws, e -> tryProcessContact(ws, e.extract(), false) },
        { ws -> ws.clear() }
    )

    private fun tryProcessContact(wounds: LinkedList<OpeningWound>, contact: ContactComponent, begin: Boolean) {
        if (!contact.isTouching) return
        val fa = contact.fixtureA
        val fb = contact.fixtureB
        val ea = fa.body.entity ?: return
        val eb = fb.body.entity ?: return
        val hitboxEntity: Entity
        val hurtboxEntity: Entity
        val hurtboxBody: Body
        val hitboxFixture: Fixture
        when {
            ea.has<HurtboxComponent>() && eb.has<HitboxComponent>() -> {
                hurtboxEntity = ea
                hitboxEntity = eb
                hurtboxBody = fa.body
                hitboxFixture = fb
            }
            ea.has<HitboxComponent>() && eb.has<HurtboxComponent>() -> {
                hurtboxEntity = eb
                hitboxEntity = ea
                hurtboxBody = fb.body
                hitboxFixture = fa
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
            when {
                hurtboxEntity.has<VitalityComponent>() -> {
                    val vitality = hurtboxEntity.extract<VitalityComponent>()
                    val fragment = hitbox.fragments.getValue(hitboxFixture)
                    val impact = hitboxBody.linearVelocity.len() * fragment.impactScale
                    if (hurtboxFragment.armor is ArmorProperties.HasArmor && impact < hurtboxFragment.armor.criticalImpact) {
                        hitboxBody.setVelocityViaImpulse(
                            hitboxBody.linearVelocity.cpy().setLength(hurtboxFragment.armor.restitution).scl(-1f)
                        )
                        return
                    }
                    vitality.vitalityLevel -= impact.toInt()
                    if (vitality.vitalityLevel <= vitality.terminalThreshold) {
                        engine.removeEntity(hurtboxEntity)
                    }
                    wounds.add(OpeningWound(hitboxEntity, hurtboxEntity, impact))
                }
            }
        } else {
            hurtbox.hitBy.remove(hitboxEntity)
            val wound = wounds.firstOrNull { it.target === hurtboxEntity && it.weapon === hitboxEntity } ?: return
            wounds.remove(wound)
        }
    }

    class OpeningWound(
        val weapon: Entity,
        val target: Entity,
        val impact: Float
    )

}
