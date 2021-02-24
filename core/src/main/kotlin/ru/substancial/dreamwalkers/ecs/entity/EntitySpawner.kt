package ru.substancial.dreamwalkers.ecs.entity

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import ktx.box2d.body
import ktx.box2d.distanceJointWith
import ktx.box2d.ropeJointWith
import ru.substancial.dreamwalkers.bodies.LunaRootTag
import ru.substancial.dreamwalkers.ecs.component.*
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.physics.*
import kotlin.math.sqrt

class EntitySpawner(private val world: World, private val engine: Engine) {

    fun spawn(
            width: Float,
            height: Float,
            spawnPosition: Vector2,
            maxSpeed: Float
    ): Entity {
        val entity = Entity()
        val body = world.body {
            type = BodyDef.BodyType.DynamicBody
            linearDamping = 0f
            fixedRotation = true
            position.set(spawnPosition)

            val area = MathUtils.PI * (width / 2) * (height / 2)

            val vertices = discreteEllipse(width, height)

            loop(vertices) {
                friction = 0f
                density = 500f / area
            }

            box(
                    width = width * 0.8f,
                    height = 0.2f,
                    position = Vector2(0f, -height / 2)
            ) {
                isSensor = true
                density = 0f
                injectProps(BodyProp.Foot)
            }
        }
        val hurtbox = world.body {
            type = BodyDef.BodyType.DynamicBody
            gravityScale = 0f
            position.set(spawnPosition)
            box(width = width * 0.9f, height = height * 0.9f) {
                density = 0f
                isSensor = true
            }
        }
        body.distanceJointWith(hurtbox) {
            length = 0f
        }
        entity.add(HurtboxComponent(listOf(hurtbox)))
        entity.add(BodyComponent(body))
        entity.add(PositionComponent())
        entity.add(MovementComponent(maxSpeed, body.mass, false))
        engine.addEntity(entity)

        body.entity = entity
        hurtbox.entity = entity

        return entity
    }

    fun equip(luna: Entity) {
        val body = world.body {
            type = BodyDef.BodyType.DynamicBody
            this.gravityScale = 0f
            linearDamping = 0.8f
            this.fixedRotation = true

            box(width = 0.1f, height = 1.5f) {
                isSensor = true
                density = 1f
            }
        }

        val props = WeaponComponent()

        body.ropeJointWith(luna.extract<BodyComponent>().pushbox) {
            this.maxLength = props.weaponDistance
        }

        val e = Entity()
        e.add(BodyComponent(body))
        e.add(HitboxComponent(luna))
        e.add(props)

        body.entity = e

        engine.addEntity(e)
    }

    private fun discreteEllipse(width: Float, height: Float, step: Float = 0.1f): FloatArray {
        val halfWidth = width / 2
        val halfHeight = height / 2

        val steps = (width / step).toInt() + 1

        val result = FloatArray(steps * 4 - 4) { Float.NaN }

        for (i in 1 until steps) {
            val x = i * step - halfWidth
            val xbw = x / halfWidth
            val y = halfHeight * sqrt((1 - xbw * xbw))

            result[i * 2] = x
            result[i * 2 + 1] = y

            result[(steps * 4 - i * 2) - 4] = x
            result[(steps * 4 - i * 2) - 3] = -y
        }

        result[0] = -halfWidth
        result[1] = 0f

        result[result.size / 2] = halfWidth
        result[result.size / 2 + 1] = 0f

        return result
    }

}
