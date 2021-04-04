package ru.substancial.dreamwalkers.nightsedge

import com.badlogic.gdx.maps.MapProperties
import com.badlogic.gdx.maps.objects.PolygonMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.math.EarClippingTriangulator
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import ktx.box2d.body
import ktx.box2d.filter
import ru.substancial.dreamwalkers.ecs.component.DamageType
import ru.substancial.dreamwalkers.ecs.component.HitboxFragment
import ru.substancial.dreamwalkers.physics.Filters
import ru.substancial.dreamwalkers.utilities.complement
import ru.substancial.dreamwalkers.utilities.fromPolygon
import ru.substancial.dreamwalkers.utilities.fromRectangle
import ru.substancial.dreamwalkers.utilities.safeCast
import kotlin.experimental.or

class NightsEdgeLoader(
        private val triangulator: EarClippingTriangulator,
        private val loader: TmxMapLoader,
        private val world: World
) {

    fun load(modelName: String): NightsEdgeModel {
        val weaponMap = loader.load(modelName)

        val boundingBox = weaponMap.layers[L.BoundingBox].objects.first() as RectangleMapObject
        val sourceHeight = boundingBox.rectangle.height
        val targetHeight = weaponMap.properties[P.WorldHeight] as Float
        val scale = targetHeight / sourceHeight
        val offset = weaponMap.layers[L.Origin]
                ?.objects?.firstOrNull()
                ?.safeCast<RectangleMapObject>()
                ?.rectangle?.let { Vector2(it.x, it.y) }
                .complement { boundingBox.rectangle.let { Vector2(it.x + it.width / 2, it.y + it.height / 2) } }

        val allObjects = weaponMap.layers[L.Model].objects
        val relevantObjects = allObjects.filter { it is RectangleMapObject || it is PolygonMapObject }
        val fragments = mutableListOf<HitboxFragment>()
        val body = world.body {
            type = BodyDef.BodyType.DynamicBody
            gravityScale = 0f
            linearDamping = 0.8f
            fixedRotation = true
            bullet = true

            relevantObjects.forEach { mo ->
                when (mo) {
                    is RectangleMapObject -> {
                        val rect = mo.rectangle
                        rect.width *= scale
                        rect.height *= scale
                        rect.x = (rect.x - offset.x) * scale
                        rect.y = (rect.y - offset.y) * scale

                        box(
                                width = rect.width,
                                height = rect.height,
                                position = Vector2(rect.x + (rect.width / 2), rect.y + (rect.height / 2))
                        ) {
                            isSensor = true
                            creationCallback = { fixture ->
                                val fragment = HitboxFragment(
                                        fixture,
                                        extractDamageType(mo.properties),
                                        extractImpactScale(mo.properties)
                                )
                                fragments.add(fragment)
                            }
                            filter {
                                categoryBits = Filters.Hitbox
                                maskBits = Filters.Hitbox.or(Filters.Hurtbox)
                            }
                        }
                    }
                    is PolygonMapObject -> {
                        val polygon = mo.polygon
                        val vertices = polygon.transformedVertices
                        for (i in vertices.indices) {
                            if (i % 2 == 0) {
                                vertices[i] = (vertices[i] - offset.x) * scale
                            } else {
                                vertices[i] = (vertices[i] - offset.y) * scale
                            }
                        }

                        val triangles = triangulator.computeTriangles(vertices)

                        for (i in 0 until triangles.size / 3) {

                            val x1 = vertices[triangles[i * 3 + 0].toInt() * 2]
                            val y1 = vertices[triangles[i * 3 + 0].toInt() * 2 + 1]

                            val x2 = vertices[triangles[i * 3 + 1].toInt() * 2]
                            val y2 = vertices[triangles[i * 3 + 1].toInt() * 2 + 1]

                            val x3 = vertices[triangles[i * 3 + 2].toInt() * 2]
                            val y3 = vertices[triangles[i * 3 + 2].toInt() * 2 + 1]

                            polygon(floatArrayOf(x1, y1, x2, y2, x3, y3)) {
                                isSensor = true
                                creationCallback = { fixture ->
                                    val fragment = HitboxFragment(
                                            fixture,
                                            extractDamageType(mo.properties),
                                            extractImpactScale(mo.properties)
                                    )
                                    fragments.add(fragment)
                                }
                                filter {
                                    categoryBits = Filters.Hitbox
                                    maskBits = Filters.Hitbox.or(Filters.Hurtbox)
                                }
                            }
                        }
                    }
                }
            }
        }
        val handleLength = (weaponMap.properties[P.HandleLength] as? Float?) ?: 3f
        return NightsEdgeModel(body, fragments, handleLength)
    }

    private fun extractDamageType(properties: MapProperties): DamageType {
        return when (val p = properties[P.DamageType]) {
            0 -> DamageType.Cut
            1 -> {
                val offset = (properties[P.PierceOffset] as? Float?) ?: 0f
                DamageType.Pierce(offset)
            }
            2 -> DamageType.Blunt
            else -> throw IllegalArgumentException("found wrong int for damage type, must be one of { 0, 1, 2 }, was: $p")
        }
    }

    private fun extractImpactScale(properties: MapProperties): Float {
        return (properties[P.ImpactScale] as? Float?) ?: 1f
    }

    class NightsEdgeModel(
            val body: Body,
            val fragments: List<HitboxFragment>,
            val handleLength: Float
    )

    private object L {
        const val Model = "model"
        const val BoundingBox = "bounding_box"
        const val Origin = "origin"
    }

    private object P {

        const val DamageType = "damage_type"
        const val PierceOffset = "pierce_offset"
        const val ImpactScale = "impact_scale"
        const val HandleLength = "handle_length"
        const val WorldHeight = "world_height"

    }

}
