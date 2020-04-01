package ru.substancial.dreamwalkers.ecs.component

import com.badlogic.ashley.core.Component

data class AerialComponent(
        var isAirborne: Boolean
) : Component