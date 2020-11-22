package ru.substancial.dreamwalkers.ecs.component

import com.badlogic.ashley.core.Component

class MovementComponent(
        val maxSpeed: Float,
        val mass: Float,
        var desiresToMove: Boolean
) : Component {

    val maxSpeed2 = maxSpeed * maxSpeed
    val pullForceMagnitude = (maxSpeed * mass) / 0.5f

}