package ru.substancial.dreamwalkers.dev

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import ktx.box2d.body
import ru.substancial.dreamwalkers.bodies.GroundTag
import ru.substancial.dreamwalkers.physics.BodyInfo

fun World.SuperFlat(): Body {
    return body {
        type = BodyDef.BodyType.StaticBody
        val length = 100f
        box(
                width = length,
                height = 0.1f,
                position = Vector2(0f, -1f)
        ) {
            friction = 0.1f
        }
        box(
                width = 0.1f,
                height = 10f,
                position = Vector2(5f, 0f)
        ) {
        }
        userData = BodyInfo(
                GroundTag,
                "ground"
        )
    }
}