package ru.substancial.dreamwalkers.ecs.component

import com.badlogic.ashley.core.Component

class DashComponent(
        val cooldown: Float,
        var ticks: Float
) : Component