package ru.substancial.dreamwalkers.level

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.maps.objects.PolygonMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.objects.TextureMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject
import com.badlogic.gdx.math.EarClippingTriangulator
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Disposable
import ktx.box2d.BodyDefinition
import ktx.box2d.FixtureDefinition
import ktx.box2d.body
import ru.substancial.dreamwalkers.ecs.component.*
import ru.substancial.dreamwalkers.physics.entity
import ru.substancial.dreamwalkers.utilities.x
import ru.substancial.dreamwalkers.utilities.y

class Level(private val map: TiledMap) : Disposable {

    private val triangulator by lazy { EarClippingTriangulator() }

    private val entityById = HashMap<String, Entity>()

    fun inflate(world: World, engine: Engine) {
        val objects = map.layers.flatMap { it.objects }.filterNot { it is TiledMapTileMapObject || it is TextureMapObject }

        objects.forEach { mapObject ->
            val entity = Entity()
            val type = mapObject.properties[RIGIDNESS].let(::rigidnessNumberToType)
            val id = try { mapObject.properties[ID] as String? } catch (e: ClassCastException) { throw LevelException("object_id in the level was not of type String") }
            if (id != null) {
                entity.add(IdentityComponent(id))
            }
            entity.add(PositionComponent(Vector2(mapObject.x, mapObject.y)))
            when (type) {
                ObjectType.Ghost -> {
                    entity.add(GhostComponent())
                }
                ObjectType.Terrain -> {
                    val body = world.body {
                        when (mapObject) {
                            is RectangleMapObject -> fromRectangle(mapObject) {}
                            is PolygonMapObject -> fromPolygon(mapObject) {}
                        }
                    }
                    entity.add(BodyComponent(body))
                    entity.add(TerrainComponent())
                    body.entity = entity
                }
                ObjectType.Rigid -> {
                    val body = world.body {
                        when (mapObject) {
                            is RectangleMapObject -> fromRectangle(mapObject) {}
                            is PolygonMapObject -> fromPolygon(mapObject) {}
                        }
                    }
                    entity.add(BodyComponent(body))
                    body.entity = entity
                }
                ObjectType.Sensor -> {
                    val body = world.body {
                        when (mapObject) {
                            is RectangleMapObject -> fromRectangle(mapObject) { isSensor = true }
                            is PolygonMapObject -> fromPolygon(mapObject) { isSensor = true }
                        }
                    }
                    entity.add(BodyComponent(body))
                    body.entity = entity
                }
            }
            engine.addEntity(entity)
        }
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

    private val rigidnessNumberToType = mapOf(
            null to ObjectType.Ghost,
            0 to ObjectType.Ghost,
            1 to ObjectType.Sensor,
            2 to ObjectType.Rigid,
            3 to ObjectType.Terrain
    )

    private fun rigidnessNumberToType(rigidness: Any?): ObjectType =
            rigidnessNumberToType[(rigidness as? Int?)]
                    ?: throw LevelException("Rigidness was not any of: ${rigidnessNumberToType.keys.joinToString(prefix = "{", postfix = "}")}, it was: $rigidness")

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
