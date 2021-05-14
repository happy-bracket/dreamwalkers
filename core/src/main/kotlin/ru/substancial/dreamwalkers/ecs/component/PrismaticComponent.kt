package ru.substancial.dreamwalkers.ecs.component

import com.badlogic.ashley.core.Component

class PrismaticComponent(
        val integrity: PointsPool,
        val shield: PointsPool,
        private val shieldRestorationRate: Float
) : Component {

    fun isBroken(): Boolean = integrity.capacity <= 0f

    fun replenishShield(delta: Float) {
        if (shield.isFull()) return
        shield.replenish(delta * shieldRestorationRate)
    }

}
