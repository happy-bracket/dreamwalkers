package ru.substancial.dreamwalkers.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2

class PositionComponent(
        val xy: Vector2 = Vector2()
) : Component {

    operator fun component1(): Float = xy.x
    operator fun component2(): Float = xy.y

}
