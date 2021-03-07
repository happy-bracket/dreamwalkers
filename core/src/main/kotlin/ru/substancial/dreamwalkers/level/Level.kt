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
import ktx.box2d.BodyDefinition
import ktx.box2d.FixtureDefinition
import ktx.box2d.body
import ru.substancial.dreamwalkers.ecs.component.*
import ru.substancial.dreamwalkers.physics.entity
import ru.substancial.dreamwalkers.utilities.fromPolygon
import ru.substancial.dreamwalkers.utilities.fromRectangle
import ru.substancial.dreamwalkers.utilities.x
import ru.substancial.dreamwalkers.utilities.y

class Level(private val map: TiledMap) {

    private val triangulator by lazy { EarClippingTriangulator() }

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
                            is PolygonMapObject -> fromPolygon(triangulator, mapObject) {}
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
                            is PolygonMapObject -> fromPolygon(triangulator, mapObject) {}
                        }
                    }
                    entity.add(BodyComponent(body))
                    body.entity = entity
                }
                ObjectType.Sensor -> {
                    val body = world.body {
                        when (mapObject) {
                            is RectangleMapObject -> fromRectangle(mapObject) { isSensor = true }
                            is PolygonMapObject -> fromPolygon(triangulator, mapObject) { isSensor = true }
                        }
                    }
                    entity.add(BodyComponent(body))
                    body.entity = entity
                }
            }
            engine.addEntity(entity)
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
        private const val ID = "object_id"

    }

}
