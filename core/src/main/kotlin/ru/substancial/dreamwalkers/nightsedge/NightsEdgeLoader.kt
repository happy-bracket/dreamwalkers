package ru.substancial.dreamwalkers.nightsedge

import com.badlogic.gdx.maps.MapProperties
import com.badlogic.gdx.maps.objects.PolygonMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.math.EarClippingTriangulator
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import ktx.box2d.body
import ru.substancial.dreamwalkers.ecs.component.DamageType
import ru.substancial.dreamwalkers.ecs.component.HitboxFragment
import ru.substancial.dreamwalkers.utilities.fromPolygon
import ru.substancial.dreamwalkers.utilities.fromRectangle

class NightsEdgeLoader(
        private val triangulator: EarClippingTriangulator,
        private val loader: TmxMapLoader,
        private val world: World
) {

    fun load(modelName: String): NightsEdgeModel {
        val weaponMap = loader.load(modelName)
        val allObjects = weaponMap.layers.flatMap { it.objects }
        val relevantObjects = allObjects.filter { it is RectangleMapObject || it is PolygonMapObject }
        val fragments = mutableListOf<HitboxFragment>()
        val body = world.body {
            type = BodyDef.BodyType.KinematicBody
            gravityScale = 0f
            linearDamping = 0.8f
            fixedRotation = true

            relevantObjects.forEach {
                when (it) {
                    is RectangleMapObject -> fromRectangle(it) {
                        isSensor = true
                        creationCallback = { fixture ->
                            val fragment = HitboxFragment(
                                    fixture,
                                    extractDamageType(it.properties),
                                    extractImpactScale(it.properties)
                            )
                            fragments.add(fragment)
                        }
                    }
                    is PolygonMapObject -> fromPolygon(triangulator, it) {
                        isSensor = true
                        creationCallback = { fixture ->
                            val fragment = HitboxFragment(
                                    fixture,
                                    extractDamageType(it.properties),
                                    extractImpactScale(it.properties)
                            )
                            fragments.add(fragment)
                        }
                    }
                }
            }
        }
        val handleLength = (weaponMap.properties["handle_length"] as? Float?) ?: 3f
        return NightsEdgeModel(body, fragments, handleLength)
    }

    private fun extractDamageType(properties: MapProperties): DamageType {
        return when (val p = properties["damage_type"]) {
            0 -> DamageType.Cut
            1 -> {
                val offset = (properties["pierce_offset"] as? Float?) ?: 0f
                DamageType.Pierce(offset)
            }
            2 -> DamageType.Blunt
            else -> throw IllegalArgumentException("found wrong int for damage type, must be one of { 0, 1, 2 }, was: $p")
        }
    }

    private fun extractImpactScale(properties: MapProperties): Float {
        return (properties["impact_scale"] as? Float?) ?: 1f
    }

    class NightsEdgeModel(
            val body: Body,
            val fragments: List<HitboxFragment>,
            val handleLength: Float
    )

}