package ru.substancial.dreamwalkers.ecs.entity

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import ru.substancial.dreamwalkers.bodies.LunaBody
import ru.substancial.dreamwalkers.ecs.component.*

fun Entities.Luna(
        spawnPoint: Vector2,
        engine: Engine,
        world: World
) {
    val lunaEntity = Entity()

    val lunaBody = world.LunaBody(spawnPoint)

    lunaEntity.add(BodyComponent(lunaBody))
    lunaEntity.add(LunaComponent)
    lunaEntity.add(AerialComponent())
    lunaEntity.add(PositionComponent())
    lunaEntity.add(LookComponent())
    lunaEntity.add(MovementComponent(7.5f, lunaBody.mass, false))

    val weaponEntity = Weapon(world, lunaBody)

    engine.addEntity(lunaEntity)
    engine.addEntity(weaponEntity)
}

private fun World.CreateLuna(spawnPoint: Vector2): Entity {
    val entity = Entity()

    val body = LunaBody(spawnPoint)

    entity.add(BodyComponent(body))
    entity.add(LunaComponent)
    entity.add(AerialComponent())
    entity.add(PositionComponent())
    entity.add(LookComponent())
    entity.add(MovementComponent(7.5f, body.mass, false))

    return entity
}