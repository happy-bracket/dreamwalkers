package ru.substancial.dreamwalkers.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity

class HitboxComponent(
        val owner: Entity
) : Component
