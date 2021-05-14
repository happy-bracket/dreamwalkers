package ru.substancial.dreamwalkers.ecs.component

import box2dLight.RayHandler
import com.badlogic.ashley.core.Component

class RayHandlerComponent(
    val handler: RayHandler
) : Component
