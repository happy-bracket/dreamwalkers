package ru.substancial.dreamwalkers.utilities

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body

fun Body.addVelocityViaImpulse(addedVelocity: Vector2) {
    applyImpulseToCenter(addedVelocity.scl(mass), true)
}