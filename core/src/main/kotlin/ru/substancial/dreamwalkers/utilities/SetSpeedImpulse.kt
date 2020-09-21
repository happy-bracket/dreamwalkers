package ru.substancial.dreamwalkers.utilities

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body

fun Body.setVelocityViaImpulse(targetVelocity: Vector2) {
    applyImpulseToCenter(targetVelocity.scl(mass), true)
}