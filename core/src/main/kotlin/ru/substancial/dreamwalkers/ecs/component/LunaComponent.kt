package ru.substancial.dreamwalkers.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.physics.box2d.Body

data class LunaComponent(
        val weapon: Body,
        val weaponDistance: Float
) : Component