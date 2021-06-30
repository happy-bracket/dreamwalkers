package ru.substancial.dreamwalkers.ecs.component

import com.badlogic.ashley.core.Component
import ru.substancial.dreamwalkers.ecs.other.PointsPool

class PrismaticComponent(
    val integrity: PointsPool,
    val shield: PointsPool,
    private val shieldRestorationRate: Float
) : Component {

    fun isBroken(): Boolean = integrity.points <= 0f

    fun damageFor(impact: Float) {
        if (shield.points <= impact) {
            integrity.decreaseBy(impact - shield.points)
            shield.set(0f)
        } else {
            shield.decreaseBy(impact)
        }
    }

    fun replenishShield(delta: Float) {
        if (shield.isFull()) return
        shield.replenish(delta * shieldRestorationRate)
    }

}
