package ru.substancial.dreamwalkers.bodies

import com.badlogic.gdx.physics.box2d.Body

object GroundTag

fun Body.isGround() =
        userData is GroundTag