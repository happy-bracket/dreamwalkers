package ru.substancial.dreamwalkers.utilities

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body

fun Body.applyImpulseToCenter(impulse: Vector2, wake: Boolean) {
    applyLinearImpulse(impulse, worldCenter, wake)
}