package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.World
import ru.substancial.dreamwalkers.ecs.component.ContactComponent
import ru.substancial.dreamwalkers.ecs.component.contactIdentityOf
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.utilities.ContactAdapter
import ru.substancial.dreamwalkers.utilities.EntityOf
import ru.substancial.dreamwalkers.utilities.RegisteringSystem
import ru.substancial.dreamwalkers.utilities.map
import java.util.*

class CollisionSystem(
        world: World
) : RegisteringSystem() {

    private val entityByContact: HashMap<Long, Entity> by map(
            Family.all(ContactComponent::class.java).get()
    ) { e -> e.extract<ContactComponent>().identity }

    init {
        val listener = object : ContactAdapter() {
            override fun beginContact(contact: Contact) {
                engine.addEntity(EntityOf(
                        ContactComponent(
                                contact.fixtureA,
                                contact.fixtureB,
                                contact.isTouching
                        )
                ))
            }

            override fun endContact(contact: Contact) {
                engine.removeEntity(entityByContact[contactIdentityOf(contact)])
            }
        }

        world.setContactListener(listener)
    }

}
