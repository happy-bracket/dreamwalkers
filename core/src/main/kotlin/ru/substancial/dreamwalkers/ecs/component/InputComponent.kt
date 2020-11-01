package ru.substancial.dreamwalkers.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2

data class InputComponent(
        val leftStick: Vector2 = Vector2(),
        val rightStick: Vector2 = Vector2(),
        var leftTriggerDown: Boolean = false,
        var rightTriggerDown: Boolean = false,
        var leftTriggerListener: (isDown: Boolean) -> Unit = {},
        var rightTriggerListener: (isDown: Boolean) -> Unit = {}
) : Component