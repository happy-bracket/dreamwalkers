package ru.substancial.dreamwalkers.utilities

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold

open class ContactAdapter : ContactListener {

    override fun beginContact(contact: Contact) {

    }

    override fun endContact(contact: Contact) {

    }

    override fun postSolve(contact: Contact, impulse: ContactImpulse) {

    }

    override fun preSolve(contact: Contact, oldManifold: Manifold) {

    }

}