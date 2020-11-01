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
import ru.substancial.dreamwalkers.ecs.component.PositionComponent
import ru.substancial.dreamwalkers.physics.BodyInfo
import ru.substancial.dreamwalkers.physics.FixtureInfo

fun World.CreateLuna(): Entity {
    val entity = Entity()

    val body = LunaBody()

    entity.add(BodyComponent(body))
    entity.add(LunaComponent)
    entity.add(AerialComponent(false))
    entity.add(PositionComponent())

    return entity
}