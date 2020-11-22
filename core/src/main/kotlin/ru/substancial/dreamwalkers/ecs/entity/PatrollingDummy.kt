package ru.substancial.dreamwalkers.ecs.entity

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.World
import ru.substancial.dreamwalkers.ai.trees.PatrollingDummyTree
import ru.substancial.dreamwalkers.bodies.DummyBody
import ru.substancial.dreamwalkers.ecs.component.*

fun World.CreateDummy(): Entity {
    val entity = Entity()

    val body = DummyBody()

    entity.add(BodyComponent(body))
    entity.add(PositionComponent())
    entity.add(AiComponent(PatrollingDummyTree()))
    entity.add(MovementComponent(7.5f, body.mass, false))

    return entity
}