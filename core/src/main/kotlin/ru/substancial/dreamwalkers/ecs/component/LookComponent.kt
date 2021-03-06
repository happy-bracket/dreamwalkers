package ru.substancial.dreamwalkers.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2

class LookComponent(
        val lookDirection: Vector2 = Vector2(1f, 0f)
) : Component {

    fun isLookingRight(): Boolean = lookDirection.angle().let { lookAngle ->
        lookAngle in 270f..0f || lookAngle in 0f..90f
    }

}