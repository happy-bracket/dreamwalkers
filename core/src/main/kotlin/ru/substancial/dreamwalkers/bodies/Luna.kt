package ru.substancial.dreamwalkers.bodies

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import ktx.box2d.body
import ru.substancial.dreamwalkers.physics.BodyInfo
import ru.substancial.dreamwalkers.physics.GroundSensor
import ru.substancial.dreamwalkers.physics.info
import ru.substancial.dreamwalkers.physics.injectInfo

interface LunaBody

object LunaRootTag : LunaBody
object LunaBodyTag : LunaBody
object LunaHoovesTag : GroundSensor, LunaBody

fun World.LunaBody(spawnPoint: Vector2): Body {
    val bodyWidth = 1.8f
    val bodyHeight = 1.7f

    val area = bodyWidth * bodyHeight

    return body {
        type = BodyDef.BodyType.DynamicBody
        linearDamping = 0f
        fixedRotation = true
        position.set(spawnPoint)
        circle(bodyHeight / 2f) {
            friction = 0f
            density = 500f / area
            injectInfo(LunaBodyTag, "luna_body")
        }
        box(
                width = bodyWidth * 0.95f,
                height = 0.2f,
                position = Vector2(0f, -bodyHeight / 2)
        ) {
            isSensor = true
            density = 0f
            injectInfo(LunaHoovesTag, "luna_hooves")
        }
        userData = BodyInfo(
                LunaRootTag,
                "luna"
        )

    }
}