package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.World
import ru.substancial.dreamwalkers.ecs.component.AerialComponent
import ru.substancial.dreamwalkers.ecs.component.BodyComponent
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.physics.BodyProp
import ru.substancial.dreamwalkers.physics.getProps
import ru.substancial.dreamwalkers.utilities.ContactAdapter
import ru.substancial.dreamwalkers.utilities.RegisteringSystem

class AerialSystem(
        world: World
) : RegisteringSystem() {

    private val entityByBody by listener(
            family,
            HashMap<Body, Entity>(),
            { s, e -> s[e.extract<BodyComponent>().body] = e },
            { s, e -> s.remove(e.extract<BodyComponent>().body) },
            { s -> s.clear() }
    )

    init {
        world.setContactListener(
                object : ContactAdapter() {

                    override fun beginContact(contact: Contact) {
                        reactToGroundContact(contact) { notGround ->
                            entityByBody[notGround]!!.extract<AerialComponent>().terrainContacts += 1
                        }
                    }

                    override fun endContact(contact: Contact) {
                        reactToGroundContact(contact) { notGround ->
                            entityByBody[notGround]!!.extract<AerialComponent>().terrainContacts -= 1
                        }
                    }

                    private inline fun reactToGroundContact(contact: Contact, ifGroundContact: (notGround: Body) -> Unit) {
                        val propsA = contact.fixtureA.getProps().props
                        val propsB = contact.fixtureB.getProps().props

                        if (BodyProp.Ground in propsA && BodyProp.Foot in propsB)
                            ifGroundContact(contact.fixtureB.body)

                        if (BodyProp.Foot in propsA && BodyProp.Ground in propsB)
                            ifGroundContact(contact.fixtureA.body)
                    }

                }
        )
    }

    companion object {

        private val family =
                Family.all(
                        AerialComponent::class.java,
                        BodyComponent::class.java
                ).get()

    }

}