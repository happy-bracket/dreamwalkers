package ru.substancial.dreamwalkers.ecs.entity

import box2dLight.PointLight
import box2dLight.RayHandler
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.DelaunayTriangulator
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import ktx.box2d.body
import ktx.box2d.circle
import ktx.box2d.filter
import ktx.box2d.ropeJointWith
import ru.substancial.dreamwalkers.Assets
import ru.substancial.dreamwalkers.ecs.component.*
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.ecs.has
import ru.substancial.dreamwalkers.ecs.maybeExtract
import ru.substancial.dreamwalkers.ecs.other.PointsPool
import ru.substancial.dreamwalkers.files.NightsEdgeLoader
import ru.substancial.dreamwalkers.physics.BodyProp
import ru.substancial.dreamwalkers.physics.Filters
import ru.substancial.dreamwalkers.physics.entity
import ru.substancial.dreamwalkers.physics.injectProps
import kotlin.experimental.or
import kotlin.math.sqrt

class EntitySpawner(
    private val world: World,
    private val engine: Engine,
    private val nightsEdgeLoader: NightsEdgeLoader,
    private val rayHandler: RayHandler
) {

    private val triangulator by lazy { DelaunayTriangulator() }

    fun spawn(
        width: Float,
        height: Float,
        spawnPosition: Vector2,
        maxSpeed: Float,
        mass: Float
    ): Entity {
        val entity = Entity()
        val pushbox = world.body {
            type = BodyDef.BodyType.DynamicBody
            linearDamping = 0f
            fixedRotation = true
            position.set(spawnPosition)

            val totalArea = MathUtils.PI * (width / 2) * (height / 2)
            val bodyDensity = mass / totalArea

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
                    filter {
                        categoryBits = Filters.Pushbox
                        maskBits = Filters.LevelPushbox or Filters.Interactive
                    }
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
                filter {
                    categoryBits = Filters.Foot
                    maskBits = Filters.LevelPushbox
                }
            }
        }
        val hurtbox = world.body {
            type = BodyDef.BodyType.DynamicBody
            gravityScale = 0f
            position.set(spawnPosition)
            box(width = width * 0.9f, height = height * 0.9f) {
                density = 0f
                isSensor = true
                filter {
                    categoryBits = Filters.Hurtbox
                    maskBits = Filters.Hitbox
                }
            }
        }
        val armor = ArmorProperties.HasArmor(1f, 7.5f)
        val fragments = mapOf(hurtbox to HurtboxFragment(1f, armor))
        entity.add(HurtboxComponent(fragments))
        entity.add(BodyComponent(pushbox))
        entity.add(PositionComponent())
        entity.add(TerrainMovementComponent(maxSpeed, mass, false))
        entity.add(ForcesComponent())
        entity.add(AerialComponent())
        engine.addEntity(entity)

        pushbox.entity = entity
        hurtbox.entity = entity

        return entity
    }

    fun spawnLuna(
        spawnPosition: Vector2
    ): Entity {
        val entity = spawn(1.8f, 1.7f, spawnPosition, 7.5f, 500f)
        entity.add(LunaComponent())
        entity.add(AerialComponent())
        entity.add(LookComponent())
        entity.add(IdentityComponent("Luna"))
        entity.add(DashComponent(1.0f))
        entity.add(PrismaticComponent(PointsPool.Full(10f), PointsPool.Full(10f), 1f))
        equip(entity, Assets.Armory.Sword)
        return entity
    }

    private fun equip(luna: Entity, weaponModel: String) {
        val model = nightsEdgeLoader.load(weaponModel)
        val body = model.body
        val fragments = model.fragments.associateBy { it.fixture }

        val joint = luna.extract<BodyComponent>().pushbox.ropeJointWith(body) {
            maxLength = model.handleLength
        }

        val e = Entity()
        e.add(BodyComponent(body))
        e.add(NightsEdgeComponent(joint, model.body.mass, model.handleLength))
        e.add(IdentityComponent("NightsEdge"))

        e.add(HitboxComponent(luna, fragments))

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

    /**
     * @param color color in RGBA hex format
     */
    fun spawnLightAt(
        position: Vector2,
        color: String,
        rays: Int,
        distance: Float,
        id: String?
    ) {
        val entity = spawnLight(color, rays, distance, id)
        entity.add(PositionComponent(position.cpy()))
        val light = entity.extract<LightComponent>().light
        light.position = position
    }

    fun spawnAttachedLight(
        anchor: Body,
        color: String,
        rays: Int,
        distance: Float,
        id: String?
    ) {
        val entity = spawnLight(color, rays, distance, id)
        val light = entity.extract<LightComponent>().light
        light.attachToBody(anchor)
    }

    private fun spawnLight(
        color: String,
        rays: Int,
        distance: Float,
        id: String?
    ): Entity {
        val light = PointLight(rayHandler, rays, Color.valueOf(color), distance, 0f, 0f)
        val entity = Entity()
        entity.add(LightComponent(light))
        if (id != null)
            entity.add(IdentityComponent(id))
        return entity
    }

}
