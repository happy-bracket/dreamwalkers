package ru.substancial.dreamwalkers.ecs.component

import com.badlogic.ashley.core.Component
import ru.substancial.dreamwalkers.utilities.LuaFunctionName

class OnCollisionStartComponent(
        val callbackName: LuaFunctionName
) : Component
