package ru.substancial.dreamwalkers.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import ru.substancial.dreamwalkers.ecs.system.Symbol

class CastingBindingsComponent(
    val bindings: HashMap<Combination, CastingBinding>
) : Component

typealias Combination = List<Symbol>

class CastingBinding(
    val combination: Combination,
    val exec: (Engine) -> Unit
)
