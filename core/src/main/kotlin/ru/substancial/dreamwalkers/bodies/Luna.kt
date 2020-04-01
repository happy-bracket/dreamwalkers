package ru.substancial.dreamwalkers.bodies

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import ktx.box2d.body

fun World.LunaBody(): Body =
        body {
            type = BodyDef.BodyType.DynamicBody
            linearDamping = 0.8f
            box(2f, 1f) {
                friction = 0.5f
            }
        }