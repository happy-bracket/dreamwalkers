package ru.substancial.dreamwalkers.bodies

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import ktx.box2d.body
import ru.substancial.dreamwalkers.physics.BodyInfo
import ru.substancial.dreamwalkers.physics.GroundSensor
import ru.substancial.dreamwalkers.physics.injectInfo

object DummyRootTag
object DummyBodyTag
object DummyLegsTag : GroundSensor

fun World.DummyBody(): Body {
    val bodyWidth = 1.8f
    val bodyHeight = 1.7f

    val area = bodyWidth * bodyHeight

    return body {
        type = BodyDef.BodyType.DynamicBody
        linearDamping = 0f
        fixedRotation = true

        box(width = bodyWidth, height = bodyHeight) {
            friction = 0f
            density = 500f / area
            injectInfo(DummyBodyTag, "dummy_body")
        }

        box(
                width = bodyWidth * 0.95f,
                height = 0.2f,
                position = Vector2(0f, -bodyHeight / 2)
        ) {
            isSensor = true
            density = 0f
            injectInfo(DummyLegsTag, "dummy_legs")
        }

        userData = BodyInfo(
                DummyRootTag,
                "dummy"
        )
    }
}