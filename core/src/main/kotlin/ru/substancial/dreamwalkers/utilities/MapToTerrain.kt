package ru.substancial.dreamwalkers.utilities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.maps.objects.PolygonMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.EarClippingTriangulator
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import ktx.box2d.body
import ru.substancial.dreamwalkers.bodies.GroundTag
import ru.substancial.dreamwalkers.physics.BodyInfo

fun TerrainBody(map: TiledMap, world: World): Body {
    val polygons = map.layers["ground"].objects.map { it as PolygonMapObject }
    val vertices = polygons.first().polygon.vertices
    for (i in vertices.indices) {
        vertices[i] = vertices[i] / 16
    }

    val triangulator = EarClippingTriangulator()
    val triangles = triangulator.computeTriangles(vertices)

    return world.body {
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