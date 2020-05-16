package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.World
import ru.substancial.dreamwalkers.bodies.isGround
import ru.substancial.dreamwalkers.ecs.component.AerialComponent
import ru.substancial.dreamwalkers.ecs.component.BodyComponent
import ru.substancial.dreamwalkers.ecs.get
import ru.substancial.dreamwalkers.physics.GroundSensor
import ru.substancial.dreamwalkers.physics.info
import ru.substancial.dreamwalkers.utilities.ContactAdapter
import ru.substancial.dreamwalkers.ecs.ComponentExtractor as CE

class AerialSystem(
        world: World
) : IteratingSystem(family) {

    private val groundedBodies = HashSet<String>()

    init {
        world.setContactListener(
                object : ContactAdapter() {

                    override fun beginContact(contact: Contact) {
                        extractBodyIfGroundContact(contact) {
                            groundedBodies.add(it.info.id)
                        }
                    }

                    override fun endContact(contact: Contact) {
                        extractBodyIfGroundContact(contact) {
                            groundedBodies.remove(it.info.id)
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

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val (body) = entity[CE.Body]
        val aerial = entity[CE.Aerial]
        aerial.isAirborne = body.info.id !in groundedBodies
    }

    companion object {

        private val family =
                Family.all(
                        AerialComponent::class.java,
                        BodyComponent::class.java
                ).get()

    }

}