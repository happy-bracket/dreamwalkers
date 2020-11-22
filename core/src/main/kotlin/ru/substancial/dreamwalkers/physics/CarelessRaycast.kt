package ru.substancial.dreamwalkers.physics

import com.badlogic.gdx.physics.box2d.RayCastCallback

fun AnyRayCastCallback(callback: () -> Unit): RayCastCallback {
    return RayCastCallback { _, _, _, _ ->
        callback()
        0f
    }
}