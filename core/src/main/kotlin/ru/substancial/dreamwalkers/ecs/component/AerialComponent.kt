package ru.substancial.dreamwalkers.ecs.component

import com.badlogic.ashley.core.Component

data class AerialComponent(
        var terrainContacts: Int = 0,
        val maxSpeed: Float,
        val mass: Float
) : Component {

    val maxSpeed2 = maxSpeed * maxSpeed
    val airFriction = (maxSpeed * mass) / 2f

    val isAirborne: Boolean
        get() = terrainContacts == 0

}