package ru.substancial.dreamwalkers.utilities

import com.badlogic.gdx.maps.objects.PolygonMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.EarClippingTriangulator
import com.badlogic.gdx.math.Vector2
import ktx.box2d.BodyDefinition
import ktx.box2d.FixtureDefinition

fun BodyDefinition.fromRectangle(mapObject: RectangleMapObject, additionally: FixtureDefinition.() -> Unit = {}) {
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

fun BodyDefinition.fromPolygon(
        triangulator: EarClippingTriangulator,
        mapObject: PolygonMapObject,
        additionally: FixtureDefinition.() -> Unit = {}
) {
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