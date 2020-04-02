package ru.substancial.dreamwalkers.ecs.entity

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.World
import ru.substancial.dreamwalkers.bodies.LunaBody
import ru.substancial.dreamwalkers.ecs.component.AerialComponent
import ru.substancial.dreamwalkers.ecs.component.BodyComponent
import ru.substancial.dreamwalkers.ecs.component.LunaComponent

fun World.CreateLuna(): Entity {
    val entity = Entity()

    val body = LunaBody()

    entity.add(BodyComponent(body))
    entity.add(LunaComponent)
    entity.add(AerialComponent(false))

    return entity
}