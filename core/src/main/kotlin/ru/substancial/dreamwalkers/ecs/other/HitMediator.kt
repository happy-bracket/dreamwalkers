package ru.substancial.dreamwalkers.ecs.other

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Fixture
import ru.substancial.dreamwalkers.ecs.component.*
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.ecs.has
import ru.substancial.dreamwalkers.ecs.maybeExtract
import ru.substancial.dreamwalkers.physics.entity
import ru.substancial.dreamwalkers.utilities.toDegrees
import ru.substancial.dreamwalkers.utilities.typedAngle
import ru.substancial.dreamwalkers.utilities.typedAngleDegrees
import ru.substancial.dreamwalkers.utilities.withValue
import kotlin.math.abs

class HitMediator(private val engine: Engine) {

    fun process(contact: Contact, begin: Boolean) {
        val fixtureA = contact.fixtureA
        val fixtureB = contact.fixtureB
        val bodyA = fixtureA.body
        val bodyB = fixtureB.body
        val entityA = bodyA.entity
        val entityB = bodyB.entity

        val hit: Entity
        val hitF: Fixture
        val hurt: Entity

        when {
            checkHitContact(entityA, entityB, bodyB) -> {
                hit = entityA
                hitF = fixtureA
                hurt = entityB
            }
            checkHitContact(entityB, entityA, bodyA) -> {
                hit = entityB
                hitF = fixtureB
                hurt = entityA
            }
            else -> return
        }

        val hitboxComponent = hit.extract<HitboxComponent>()

        if (hitboxComponent.owner == hurt) return

        val fragment = hitboxComponent.fragments.getValue(hitF)
        val hurtboxComponent = hurt.extract<HurtboxComponent>()

        if (begin) {
            if (hit in hurtboxComponent.hitBy) return
            hurtboxComponent.hitBy.add(hit)
            if (fragment.damageType is DamageType.Pierce) {
                val hitBody = fragment.fixture.body
                val rotation = hitBody.typedAngle.toDegrees().withValue {
                    it + 90 - fragment.damageType.offsetAngle
                }
                val rotationVector = Vector2(1f, 0f).rotate(rotation.value)
                val velocityAngle = hitBody.linearVelocity
                val angle = rotationVector.angle(velocityAngle)
                Gdx.app.log("egor2", angle.toString())
                val difference = abs(angle)
                if (difference <= 15f) {
                    Gdx.app.log("egor2", "impaled")
                    hurt.add(ImpaleComponent(hit))
                }
            }
        } else {
            hurtboxComponent.hitBy.remove(hit)
        }

    }

    private fun checkHitContact(assumedHitbox: Entity, assumedHurtbox: Entity, testedBody: Body): Boolean {
        return assumedHitbox.has<HitboxComponent>() && assumedHurtbox.maybeExtract<HurtboxComponent>()?.let { testedBody in it.hurtboxes } == true
    }

}
