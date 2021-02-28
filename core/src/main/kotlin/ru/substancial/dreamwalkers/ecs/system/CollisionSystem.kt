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
import ru.substancial.dreamwalkers.ecs.other.HitMediator
import ru.substancial.dreamwalkers.level.ScenarioHolder
import ru.substancial.dreamwalkers.physics.BodyProp
import ru.substancial.dreamwalkers.physics.entity
import ru.substancial.dreamwalkers.physics.getProps
import ru.substancial.dreamwalkers.utilities.ContactAdapter
import ru.substancial.dreamwalkers.utilities.RegisteringSystem

class CollisionSystem(
        world: World,
        private val scenario: ScenarioHolder,
        private val hitMediator: HitMediator
) : RegisteringSystem() {

    private val entityByBody by listener(
            family,
            HashMap<Body, Entity>(),
            { s, e -> s[e.extract<BodyComponent>().pushbox] = e },
            { s, e -> s.remove(e.extract<BodyComponent>().pushbox) },
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
        val entityA = bodyA.entity
        val fixtureA = contact.fixtureA
        val propsA = fixtureA.getProps().props

        val bodyB = contact.fixtureB.body
        val entityB = bodyB.entity
        val fixtureB = contact.fixtureB
        val propsB = fixtureB.getProps().props

        if (entityA.has<TerrainComponent>() && BodyProp.Foot in propsB) {
            reactToTerrainContact(bodyB, begin)
        }

        if (entityB.has<TerrainComponent>() && BodyProp.Foot in propsA) {
            reactToTerrainContact(bodyA, begin)
        }

        if (begin && (entityA.has<OnCollisionStartComponent>()).xor(entityB.has<OnCollisionStartComponent>())) {
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
                        initiator.body.entity,
                        target.body.entity
                )
            }
        }

        if (begin)
            hitMediator.process(contact)
    }

    private fun reactToTerrainContact(target: Body, begin: Boolean) {
        target.entity
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
