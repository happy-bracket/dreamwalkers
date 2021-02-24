package ru.substancial.dreamwalkers.ecs.entity

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.DelaunayTriangulator
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import ktx.box2d.body
import ktx.box2d.ropeJointWith
import ru.substancial.dreamwalkers.ecs.component.*
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.physics.BodyProp
import ru.substancial.dreamwalkers.physics.entity
import ru.substancial.dreamwalkers.physics.injectProps
import kotlin.math.sqrt

class EntitySpawner(private val world: World, private val engine: Engine) {

    private val triangulator by lazy { DelaunayTriangulator() }

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

            val totalArea = MathUtils.PI * (width / 2) * (height / 2)
            val bodyDensity = 500f / totalArea

            val vertices = discreteEllipse(width, height)
            val triangles = triangulator.computeTriangles(vertices, true)

            for (i in 0 until triangles.size / 3) {

                val x1 = vertices[triangles[i * 3 + 0].toInt() * 2]
                val y1 = vertices[triangles[i * 3 + 0].toInt() * 2 + 1]

                val x2 = vertices[triangles[i * 3 + 1].toInt() * 2]
                val y2 = vertices[triangles[i * 3 + 1].toInt() * 2 + 1]

                val x3 = vertices[triangles[i * 3 + 2].toInt() * 2]
                val y3 = vertices[triangles[i * 3 + 2].toInt() * 2 + 1]

                polygon(floatArrayOf(x1, y1, x2, y2, x3, y3)) {
                    friction = 0f
                    density = bodyDensity
                }
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
