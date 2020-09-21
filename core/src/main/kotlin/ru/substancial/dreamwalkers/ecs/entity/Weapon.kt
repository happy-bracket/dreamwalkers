package ru.substancial.dreamwalkers.ecs.entity

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import ktx.box2d.body
import ktx.box2d.ropeJointWith
import ru.substancial.dreamwalkers.ecs.component.BodyComponent
import ru.substancial.dreamwalkers.ecs.component.WeaponComponent
import ru.substancial.dreamwalkers.physics.BodyInfo
import ru.substancial.dreamwalkers.physics.FixtureInfo

fun createWeapon(world: World, luna: Body): Entity {
    val body = world.body {
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

    val props = WeaponComponent()

    body.ropeJointWith(luna) {
        this.maxLength = props.weaponDistance
    }

    val e = Entity()
    e.add(BodyComponent(body))
    e.add(props)

    return e
}