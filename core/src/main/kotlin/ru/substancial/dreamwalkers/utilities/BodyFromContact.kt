package ru.substancial.dreamwalkers.utilities

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Contact

operator fun Contact.component1(): Body =
        fixtureA.body

operator fun Contact.component2(): Body =
        fixtureB.body