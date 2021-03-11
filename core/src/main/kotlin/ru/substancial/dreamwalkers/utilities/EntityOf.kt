package ru.substancial.dreamwalkers.utilities

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity

fun EntityOf(vararg components: Component): Entity = Entity().apply {
    components.forEach { add(it) }
}
