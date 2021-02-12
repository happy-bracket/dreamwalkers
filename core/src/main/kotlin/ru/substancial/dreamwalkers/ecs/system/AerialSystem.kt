package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.World
import ru.substancial.dreamwalkers.ecs.component.AerialComponent
import ru.substancial.dreamwalkers.ecs.component.BodyComponent
import ru.substancial.dreamwalkers.ecs.component.ScenarioComponent
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.ecs.maybeExtract
import ru.substancial.dreamwalkers.level.ScenarioCallbacks
import ru.substancial.dreamwalkers.level.ScenarioHolder
import ru.substancial.dreamwalkers.physics.BodyProp
import ru.substancial.dreamwalkers.physics.getProps
import ru.substancial.dreamwalkers.utilities.ContactAdapter
import ru.substancial.dreamwalkers.utilities.RegisteringSystem

class AerialSystem(
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
        val propsA = contact.fixtureA.getProps().props
        val propsB = contact.fixtureB.getProps().props

        if (BodyProp.Ground in propsA && BodyProp.Foot in propsB) {
            entityByBody[contact.fixtureB.body]!!
                    .maybeExtract<AerialComponent>()
                    ?.let { it.terrainContacts += if (begin) 1 else -1 }
        }

        if (BodyProp.Foot in propsA && BodyProp.Ground in propsB) {
            entityByBody[contact.fixtureA.body]!!
                    .maybeExtract<AerialComponent>()
                    ?.let { it.terrainContacts += if (begin) 1 else -1 }
        }

        if ((BodyProp.OnCollisionStart in propsA).xor(BodyProp.OnCollisionStart in propsB)) {
            val initiator: Fixture
            val target: Fixture
            if (BodyProp.OnCollisionStart in propsA) {
                initiator = contact.fixtureB
                target = contact.fixtureA
            } else {
                initiator = contact.fixtureA
                target = contact.fixtureB
            }
            scenario.processCollision(entityByBody[initiator.body]!!, entityByBody[target.body]!!)
        }
    }

    companion object {

        private val family =
                Family.all(
                        BodyComponent::class.java
                ).get()

    }

}
