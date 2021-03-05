package ru.substancial.dreamwalkers.ecs.component

import com.badlogic.ashley.core.Component

private const val WeaponDistance = 5f

data class NightsEdgeComponent(val weaponDistance: Float = WeaponDistance) : Component
