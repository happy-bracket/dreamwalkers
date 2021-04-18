package ru.substancial.dreamwalkers.ecs.component

import com.badlogic.ashley.core.Component

class DashComponent(
        override val cooldown: Float
) : Component, Cooldown {

    override var ticks: Float = 0f

}
