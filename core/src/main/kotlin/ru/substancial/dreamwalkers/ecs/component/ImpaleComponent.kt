package ru.substancial.dreamwalkers.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.Joint

class ImpaleComponent(
        val anchor: Entity,
        var durationLeft: Float = 3f,
        var joint: Joint? = null
) : Component
