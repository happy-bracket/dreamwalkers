package ru.substancial.dreamwalkers.bodies

import com.badlogic.gdx.physics.box2d.Body
import ru.substancial.dreamwalkers.physics.info

object GroundTag

fun Body.isGround() =
        this.info.tag is GroundTag