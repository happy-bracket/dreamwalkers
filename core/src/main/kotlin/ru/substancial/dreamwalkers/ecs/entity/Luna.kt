package ru.substancial.dreamwalkers.ecs.entity

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.World
import ktx.box2d.body
import ktx.box2d.motorJointWith
import ktx.box2d.revoluteJointWith
import ktx.box2d.ropeJointWith
import ru.substancial.dreamwalkers.bodies.LunaBody
import ru.substancial.dreamwalkers.ecs.component.AerialComponent
import ru.substancial.dreamwalkers.ecs.component.BodyComponent
import ru.substancial.dreamwalkers.ecs.component.LunaComponent
import ru.substancial.dreamwalkers.physics.BodyInfo
import ru.substancial.dreamwalkers.physics.FixtureInfo

private val WeaponDistance = 5f

fun World.CreateLuna(): Entity {
    val entity = Entity()

    val body = LunaBody()
    val weapon = Weapon()

    entity.add(BodyComponent(body))
    entity.add(LunaComponent(weapon, WeaponDistance))
    entity.add(AerialComponent(false))

    return entity
}

private fun World.Weapon(): Body {
    val weapon = body {
        type = BodyDef.BodyType.DynamicBody
        this.gravityScale = 0f
        linearDamping = 0.8f
        this.fixedRotation = true

        box(width = 0.1f, height = 1.5f) {
            isSensor = true
            density = 1f
            userData = FixtureInfo(Unit, "luna_weapon")
        }
        userData = BodyInfo(Unit, "luna_weapon")

    }

    return weapon
}