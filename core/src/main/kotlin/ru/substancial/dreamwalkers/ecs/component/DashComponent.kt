package ru.substancial.dreamwalkers.ecs.component

import com.badlogic.ashley.core.Component

class DashComponent(
        override val cooldown: Float,
        override var ticks: Float
) : Component, Cooldown
