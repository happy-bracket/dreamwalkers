package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.World
import ru.substancial.dreamwalkers.ecs.component.*
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.ecs.has
import ru.substancial.dreamwalkers.ecs.maybeExtract
import ru.substancial.dreamwalkers.level.ScenarioHolder
import ru.substancial.dreamwalkers.physics.BodyProp
import ru.substancial.dreamwalkers.physics.getProps
import ru.substancial.dreamwalkers.utilities.ContactAdapter
import ru.substancial.dreamwalkers.utilities.RegisteringSystem

class CollisionSystem(
        world: World,
        private val scenario: ScenarioHolder
) : RegisteringSystem() {

    private val entityByBody by listener(
            family,
            HashMap<Body, Entity>(),
            { s, e -> s[e.extract<BodyComponent>().body] = e },
            { s, e -> s.remove(e.extract<BodyComponent>().body) },
            { s -> s.clear() }
    )

    init {
        val listener = object : ContactAdapter() {
            override fun beginContact(contact: Contact) {
                reactToContact(contact, true)
            }

            override fun endContact(contact: Contact) {
                reactToContact(contact, false)
            }
        }

        world.setContactListener(listener)
    }

    private fun reactToContact(contact: Contact, begin: Boolean) {
        val bodyA = contact.fixtureA.body
        val entityA = entityByBody[bodyA]!!
        val fixtureA = contact.fixtureA
        val propsA = fixtureA.getProps().props

        val bodyB = contact.fixtureB.body
        val entityB = entityByBody[bodyB]!!
        val fixtureB = contact.fixtureB
        val propsB = fixtureB.getProps().props

        if (entityA.has<TerrainComponent>() && BodyProp.Foot in propsB) {
            reactToTerrainContact(bodyB, begin)
        }

        if (entityB.has<TerrainComponent>() && BodyProp.Foot in propsA) {
            reactToTerrainContact(bodyA, begin)
        }

        if ((entityA.has<OnCollisionStartComponent>()).xor(entityB.has<OnCollisionStartComponent>())) {
            val functionName: String
            val initiator: Fixture
            val target: Fixture
            if (entityA.has<OnCollisionStartComponent>()) {
                functionName = entityA.extract<OnCollisionStartComponent>().callbackName
                initiator = contact.fixtureB
                target = contact.fixtureA
            } else {
                functionName = entityB.extract<OnCollisionStartComponent>().callbackName
                initiator = contact.fixtureA
                target = contact.fixtureB
            }
            Gdx.app.postRunnable {
                scenario.call(
                        functionName,
                        entityByBody[initiator.body]!!,
                        entityByBody[target.body]!!
                )
            }
        }

//        if (begin) {
//            var hitBox: Fixture? = null
//            var hurtBox: Fixture? = null
//            when {
//                BodyProp.HitBox in propsA && BodyProp.HurtBox in propsB -> {
//                    hitBox = contact.fixtureA
//                    hurtBox = contact.fixtureB
//                }
//                BodyProp.HurtBox in propsA && BodyProp.HitBox in propsB -> {
//                    hitBox = contact.fixtureB
//                    hurtBox = contact.fixtureA
//                }
//            }
//            if (hitBox != null && hurtBox != null) {
//                val hurtEntity = entityByBody[hurtBox.body]!!
//                hurtEntity.extract<VitalityComponent>().vitalityLevel -= 10
//            }
//        }

    }

    private fun reactToTerrainContact(target: Body, begin: Boolean) {
        entityByBody[target]!!
                .maybeExtract<AerialComponent>()
                ?.let { it.terrainContacts += if (begin) 1 else -1 }
    }

    companion object {

        private val family =
                Family.all(
                        BodyComponent::class.java
                ).get()

    }

}
