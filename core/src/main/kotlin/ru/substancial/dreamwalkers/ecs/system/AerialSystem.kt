package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.World
import ru.substancial.dreamwalkers.bodies.isGround
import ru.substancial.dreamwalkers.ecs.component.AerialComponent
import ru.substancial.dreamwalkers.ecs.component.BodyComponent
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.physics.GroundSensor
import ru.substancial.dreamwalkers.physics.info
import ru.substancial.dreamwalkers.utilities.ContactAdapter
import ru.substancial.dreamwalkers.utilities.RegisteringSystem

class AerialSystem(
        world: World
) : RegisteringSystem() {

    private var listener: EntityListener? = null
    private val bodies: ImmutableArray<Entity> by multiple(family)

    init {
        world.setContactListener(
                object : ContactAdapter() {

                    override fun beginContact(contact: Contact) {
                        extractBodyIfGroundContact(contact) { body ->
                            bodies.firstOrNull {
                                it.extract<BodyComponent>().body.info.id == body.info.id
                            }?.let {
                                it.extract<AerialComponent>().terrainContacts += 1
                            }
                        }
                    }

                    override fun endContact(contact: Contact) {
                        extractBodyIfGroundContact(contact) { body ->
                            bodies.firstOrNull {
                                it.extract<BodyComponent>().body.info.id == body.info.id
                            }?.let {
                                it.extract<AerialComponent>().terrainContacts -= 1
                            }
                        }
                    }

                    private fun extractBodyIfGroundContact(contact: Contact, ifExtracted: (Body) -> Unit) {
                        val fa = contact.fixtureA
                        val fb = contact.fixtureB
                        val ba = fa.body
                        val bb = fb.body

                        val ga = ba.isGround()
                        val gb = bb.isGround()

                        if (!(ga && gb).xor(ga || gb)) return

                        val touch = if (ga) fb else fa

                        if (touch.info?.tag is GroundSensor)
                            ifExtracted(touch.body)
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