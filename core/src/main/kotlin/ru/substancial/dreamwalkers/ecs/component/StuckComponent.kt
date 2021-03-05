package ru.substancial.dreamwalkers.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity

class StuckComponent(
        val anchor: Entity,
        val dragged: Entity,
        var durationLeft: Float = 3f
) : Component
