package ru.substancial.dreamwalkers.utilities

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body

fun Body.cancelGravity(): Vector2 {
    return world.gravity.cpy().scl(-mass * gravityScale)
}
