package ru.substancial.dreamwalkers.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Fixture

class ContactComponent(
        val fixtureA: Fixture,
        val fixtureB: Fixture,
        val isTouching: Boolean
) : Component {

    val identity: Long = contactIdentityOf(this)

}

fun contactIdentityOf(fixtureA: Fixture, fixtureB: Fixture): Long {
    val ha = fixtureA.hashCode()
    val hb = fixtureB.hashCode()
    return maxOf(ha, hb).toLong().shl(16) + minOf(ha, hb)
}

fun contactIdentityOf(component: ContactComponent): Long {
    return contactIdentityOf(component.fixtureA, component.fixtureB)
}

fun contactIdentityOf(contact: Contact): Long {
    return contactIdentityOf(contact.fixtureA, contact.fixtureB)
}
