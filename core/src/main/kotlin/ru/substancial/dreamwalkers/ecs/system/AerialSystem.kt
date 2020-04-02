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
import ru.substancial.dreamwalkers.physics.info
import ru.substancial.dreamwalkers.utilities.ContactAdapter
import ru.substancial.dreamwalkers.utilities.component1
import ru.substancial.dreamwalkers.utilities.component2
import ru.substancial.dreamwalkers.ecs.ComponentExtractor as CE

class AerialSystem(
        world: World
) : IteratingSystem(family) {

    private val groundedBodies = HashSet<String>()

    init {
        world.setContactListener(
                object : ContactAdapter() {

                    override fun beginContact(contact: Contact) {
                        Gdx.app.log("beginContact", "")
                        val body = extractBodyIfGroundContact(contact) ?: return
                        groundedBodies.add(body.info.id)
                    }

                    override fun endContact(contact: Contact) {
                        Gdx.app.log("endContact", "")
                        val body = extractBodyIfGroundContact(contact) ?: return
                        groundedBodies.remove(body.info.id)
                    }

                    private fun extractBodyIfGroundContact(contact: Contact): Body? {
                        val (a, b) = contact
                        return when {
                            a.isGround() && b.isGround() -> null
                            a.isGround() -> b
                            b.isGround() -> a
                            else -> null
                        }
                    }

                }
        )
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val (body) = entity[CE.Body]
        val aerial = entity[CE.Aerial]
//        Gdx.app.log("groundedBodies", groundedBodies.toString())
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