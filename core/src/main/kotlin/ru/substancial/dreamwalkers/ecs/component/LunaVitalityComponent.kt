package ru.substancial.dreamwalkers.ecs.component

import com.badlogic.ashley.core.Component

class LunaVitalityComponent(
    val health: PointsPool,
    val shield: PointsPool
) : Component

class PointsPool(
    val capacity: Float,
    var points: Float
)
