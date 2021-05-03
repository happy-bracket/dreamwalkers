package ru.substancial.dreamwalkers.physics

import com.badlogic.gdx.physics.box2d.Body

fun Body.destroy() {
    world.destroyBody(this)
}
