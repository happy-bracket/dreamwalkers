package ru.substancial.dreamwalkers.level

import com.badlogic.gdx.maps.objects.*
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.EarClippingTriangulator
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import ktx.box2d.body
import ru.substancial.dreamwalkers.bodies.GroundTag
import ru.substancial.dreamwalkers.physics.BodyInfo

class Level(private val map: TiledMap, private val scale: Int) {

    val playerSpawnPoint: Vector2 by lazy { extractSpawnPoint() }

    fun constructGround(world: World) {
        val polygons = map.layers[TAG_TERRAIN].objects.map { it as PolygonMapObject }
        val vertices = polygons.first().polygon.vertices
        for (i in vertices.indices) {
            vertices[i] = vertices[i] / 16
        }

        val triangulator = EarClippingTriangulator()
        val triangles = triangulator.computeTriangles(vertices)

        world.body {
            type = BodyDef.BodyType.StaticBody
            userData = BodyInfo(GroundTag, "terrain")
            position.set(polygons.first().polygon.x / 16, polygons.first().polygon.y / 16)
            for (i in 0 until triangles.size / 3) {

                val x1 = vertices[triangles[i * 3 + 0].toInt() * 2]
                val y1 = vertices[triangles[i * 3 + 0].toInt() * 2 + 1]

                val x2 = vertices[triangles[i * 3 + 1].toInt() * 2]
                val y2 = vertices[triangles[i * 3 + 1].toInt() * 2 + 1]

                val x3 = vertices[triangles[i * 3 + 2].toInt() * 2]
                val y3 = vertices[triangles[i * 3 + 2].toInt() * 2 + 1]

                polygon(floatArrayOf(x1, y1, x2, y2, x3, y3)) {}
            }
        }
    }

    private fun extractSpawnPoint(): Vector2 {
        val spawnPointMarkerObject = map.layers[TAG_SPAWN_POINT].objects.first()
        return when (val mo = spawnPointMarkerObject) {
            is CircleMapObject -> Vector2(mo.circle.x / scale, mo.circle.y / scale)
            is EllipseMapObject -> Vector2(mo.ellipse.x / scale, mo.ellipse.y / scale)
            is PolygonMapObject -> Vector2(mo.polygon.x / scale, mo.polygon.y / scale)
            is PolylineMapObject -> Vector2(mo.polyline.x / scale, mo.polyline.y / scale)
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