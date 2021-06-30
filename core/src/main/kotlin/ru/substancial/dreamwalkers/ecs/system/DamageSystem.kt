package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Fixture
import ru.substancial.dreamwalkers.ecs.component.*
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.ecs.has
import ru.substancial.dreamwalkers.level.ScenarioCallbacks
import ru.substancial.dreamwalkers.physics.entity
import ru.substancial.dreamwalkers.utilities.RegisteringSystem
import ru.substancial.dreamwalkers.utilities.justListen
import ru.substancial.dreamwalkers.utilities.setVelocityViaImpulse
import java.util.*

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
        if (begin && !hurtbox.hitBy.contains(hitbox)) {
            hurtbox.hitBy.add(hitbox)
            val hurtboxFragment = hurtbox.hurtboxes.getValue(hurtboxBody)
            val hitboxBody = hitboxEntity.extract<BodyComponent>().pushbox

            when {
                hurtboxEntity.has<PrismaticComponent>() -> {
                    val prism = hurtboxEntity.extract<PrismaticComponent>()
                    val impact = getImpact(hitboxBody, hitbox.fragments.getValue(hitboxFixture))
                    if (!processArmor(impact, hurtboxFragment, hitboxBody)) return
                    processHurtbox(impact, hurtboxFragment, hurtboxEntity)
                    prism.damageFor(impact)
                }
            }

        } else {
            hurtbox.hitBy.remove(hitbox)
        }
    }

    private fun getImpact(hitboxBody: Body, fragment: HitboxFragment): Float {
        return hitboxBody.linearVelocity.len() * fragment.impactScale
    }

    private fun processHurtbox(impact: Float, hurtboxFragment: HurtboxFragment, hurtboxEntity: Entity) {
        if (impact >= hurtboxFragment.impactToDestroy) {
            hurtboxFragment.onDestroy(engine, hurtboxEntity)
        }
    }

    private fun processArmor(impact: Float, hurtboxFragment: HurtboxFragment, hitboxBody: Body): Boolean {
        return if (hurtboxFragment.armor is ArmorProperties.HasArmor && impact < hurtboxFragment.armor.criticalImpact) {
            hitboxBody.setVelocityViaImpulse(
                hitboxBody.linearVelocity.cpy().setLength(hurtboxFragment.armor.restitution).scl(-1f)
            )
            true
        } else false
    }

}
