package ru.substancial.dreamwalkers.ecs.entity

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import ktx.box2d.body
import ru.substancial.dreamwalkers.ecs.component.BodyComponent
import ru.substancial.dreamwalkers.ecs.component.LunaComponent

fun World.CreateLuna(): Entity {
    val entity = Entity()

    val body = body {
        type = BodyDef.BodyType.DynamicBody
        linearDamping = 0.8f
        box(2f, 1f) {
            friction = 0.5f
        }
    }

    entity.add(BodyComponent(body))
    entity.add(LunaComponent)

    return entity
}