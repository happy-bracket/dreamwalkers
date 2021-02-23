package ru.substancial.dreamwalkers.ecs.component

import com.badlogic.ashley.core.Component

class VitalityComponent(
        var vitalityLevel: Int,
        val terminalThreshold: Int,
        val exhaustionThreshold: Int
) : Component
