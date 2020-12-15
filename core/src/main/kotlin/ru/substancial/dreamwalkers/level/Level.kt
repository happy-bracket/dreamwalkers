package ru.substancial.dreamwalkers.level

import com.badlogic.gdx.maps.objects.PolygonMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.EarClippingTriangulator
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import ktx.box2d.BodyDefinition
import ktx.box2d.body
import ru.substancial.dreamwalkers.bodies.GroundTag
import ru.substancial.dreamwalkers.physics.BodyInfo

class Level(private val map: TiledMap, private val scale: Int) {

    val playerSpawnPoint: Vector2 by lazy { extractSpawnPoint() }

    private val triangulator by lazy { EarClippingTriangulator() }

    fun constructGround(world: World) {
        world.body {
            type = BodyDef.BodyType.StaticBody
            userData = BodyInfo(GroundTag, "terrain")

            map.layers[TAG_TERRAIN].objects.forEach {
                when (it) {
                    is RectangleMapObject -> fromRectangle(it)
                    is PolygonMapObject -> fromPolygon(it)
                    else -> throw LevelException("unsupported shape found on ground layer: $it")
                }
            }

        }
    }

    private fun BodyDefinition.fromRectangle(mapObject: RectangleMapObject) {
        val rect = mapObject.rectangle
        box(
                width = rect.width / scale,
                height = rect.height / scale,
                position = Vector2(rect.x / scale, rect.y / scale)
        ) {}
    }

    private fun BodyDefinition.fromPolygon(mapObject: PolygonMapObject) {
        val polygon = mapObject.polygon
        val vertices = polygon.vertices
        val offsetX = polygon.x / scale
        val offsetY = polygon.y / scale

        for (i in vertices.indices) {
            vertices[i] = vertices[i] / scale
        }
        val triangles = triangulator.computeTriangles(vertices)

        for (i in 0 until triangles.size / 3) {

            val x1 = vertices[triangles[i * 3 + 0].toInt() * 2] + offsetX
            val y1 = vertices[triangles[i * 3 + 0].toInt() * 2 + 1] + offsetY

            val x2 = vertices[triangles[i * 3 + 1].toInt() * 2] + offsetX
            val y2 = vertices[triangles[i * 3 + 1].toInt() * 2 + 1] + offsetY

            val x3 = vertices[triangles[i * 3 + 2].toInt() * 2] + offsetX
            val y3 = vertices[triangles[i * 3 + 2].toInt() * 2 + 1] + offsetY

            polygon(floatArrayOf(x1, y1, x2, y2, x3, y3)) {}
        }
    }

    private fun extractSpawnPoint(): Vector2 {
        val spawnPointMarkerObject =
                map.layers[TAG_SPAWN_POINT]
                        .let { it ?: throw LevelException("no layer for spawn point found on the map") }
                        .objects.takeIf { it.count in 0..1 }
                        .let {
                            it ?: throw LevelException("only one spawn point must be located on the spawn point layer")
                        }
                        .firstOrNull() ?: throw LevelException("no spawn point found on the map")
        return when (val mo = spawnPointMarkerObject) {
            is RectangleMapObject -> Vector2(mo.rectangle.x / scale, mo.rectangle.y / scale)
            else -> throw LevelException(
                    "Loaded map did not contain required form of marking a spawn-point"
            )
        }
    }

    class LevelException(message: String) : Exception(message)

    companion object {

        private const val TAG_SPAWN_POINT = "spawn_point"
        private const val TAG_TERRAIN = "ground"

    }

}