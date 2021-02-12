package ru.substancial.dreamwalkers.level

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.maps.objects.PolygonMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.EarClippingTriangulator
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Disposable
import ktx.box2d.BodyDefinition
import ktx.box2d.FixtureDefinition
import ktx.box2d.body
import ru.substancial.dreamwalkers.bodies.GroundTag
import ru.substancial.dreamwalkers.ecs.component.BodyComponent
import ru.substancial.dreamwalkers.ecs.component.IdentityComponent
import ru.substancial.dreamwalkers.ecs.component.PositionComponent
import ru.substancial.dreamwalkers.physics.*
import ru.substancial.dreamwalkers.utilities.x
import ru.substancial.dreamwalkers.utilities.y
import java.util.*
import kotlin.collections.HashMap

class Level(private val map: TiledMap) : Disposable {

    private val triangulator by lazy { EarClippingTriangulator() }

    private val entityById = HashMap<String, Entity>()

    fun inflate(world: World, engine: Engine) {
        val allObjects = map.layers.flatMap { it.objects }
        val objects = allObjects
                .mapNotNull {
                    it.properties[RIGIDNESS].let { rigidness -> rigidnessNumberToType(rigidness) to it }
                }

        val entities = LinkedList<Entity>()
        world.body {
            for ((type, mapObject) in objects) {

                if (type is ObjectType.Terrain) {
                    when (mapObject) {
                        is RectangleMapObject -> fromRectangle(mapObject) { injectProps(BodyProp.Ground) }
                        is PolygonMapObject -> fromPolygon(mapObject) { injectProps(BodyProp.Ground) }
                    }
                    continue
                }

                val entity = Entity()
                entities.add(entity)
                entity.add(PositionComponent(Vector2(mapObject.x, mapObject.y)))

                val id = mapObject.properties[ID] as String?
                if (id != null) {
                    entityById[id] = entity
                    entity.add(IdentityComponent(id))
                }

                if (type is ObjectType.Ghost)
                    continue

                val objectBody = world.body {
                    this.type = BodyDef.BodyType.StaticBody
                    val onCollStart = mapObject.properties[REACT_TO_COLLISION_START]
                    val props = mutableSetOf<BodyProp>()
                    if (onCollStart != null) {
                        if (id == null) {
                            throw LevelException("interactive body must have an object_id set. Met a level object that reacts to collisions with Tiled id ${mapObject.properties["id"]}")
                        }
                        props.add(BodyProp.OnCollisionStart)
                    }
                    when (mapObject) {
                        is RectangleMapObject -> fromRectangle(mapObject) {
                            isSensor = type is ObjectType.Sensor
                            userData = FixtureProps(props)
                        }
                        is PolygonMapObject -> fromPolygon(mapObject) {
                            isSensor = type is ObjectType.Sensor
                            userData = FixtureProps(props)
                        }
                    }
                }
                entity.add(BodyComponent(objectBody))
            }
        }

        entities.forEach(engine::addEntity)
    }

    fun getEntityById(id: String): Entity = entityById[id]
            ?: throw LevelException("there was no map entity with id $id")

    override fun dispose() {
        entityById.clear()
    }

    private fun BodyDefinition.fromRectangle(mapObject: RectangleMapObject, additionally: FixtureDefinition.() -> Unit = {}) {
        val rect = mapObject.rectangle
        box(
                width = rect.width,
                height = rect.height,
                position = Vector2(
                        rect.x + rect.width / 2,
                        rect.y + rect.height / 2
                )
        ) {
            additionally()
        }
    }

    private fun BodyDefinition.fromPolygon(mapObject: PolygonMapObject, additionally: FixtureDefinition.() -> Unit = {}) {
        val polygon = mapObject.polygon
        val vertices = polygon.vertices
        val offsetX = polygon.x
        val offsetY = polygon.y

        for (i in vertices.indices) {
            vertices[i] = vertices[i]
        }
        val triangles = triangulator.computeTriangles(vertices)

        for (i in 0 until triangles.size / 3) {

            val x1 = vertices[triangles[i * 3 + 0].toInt() * 2] + offsetX
            val y1 = vertices[triangles[i * 3 + 0].toInt() * 2 + 1] + offsetY

            val x2 = vertices[triangles[i * 3 + 1].toInt() * 2] + offsetX
            val y2 = vertices[triangles[i * 3 + 1].toInt() * 2 + 1] + offsetY

            val x3 = vertices[triangles[i * 3 + 2].toInt() * 2] + offsetX
            val y3 = vertices[triangles[i * 3 + 2].toInt() * 2 + 1] + offsetY

            polygon(floatArrayOf(x1, y1, x2, y2, x3, y3)) {
                additionally()
            }
        }
    }

    class LevelException(message: String) : Exception(message)

    private fun rigidnessNumberToType(rigidness: Any?): ObjectType =
            when (rigidness) {
                0, null -> ObjectType.Ghost
                1 -> ObjectType.Sensor
                2 -> ObjectType.Rigid
                3 -> ObjectType.Terrain
                else -> throw LevelException("Rigidness was not any of: { null, 0, 1, 2, 3 }, it was: $rigidness")
            }

    private sealed class ObjectType {
        object Ghost : ObjectType()
        object Sensor : ObjectType()
        object Rigid : ObjectType()
        object Terrain : ObjectType()
    }

    companion object Tag {

        private const val RIGIDNESS = "rigidness"
        private const val REACT_TO_COLLISION_START = "on_collision"
        private const val REACT_TO_COLLISION_END = "after_collision"
        private const val ID = "object_id"

    }

}
