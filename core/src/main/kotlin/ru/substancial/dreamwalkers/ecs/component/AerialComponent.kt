package ru.substancial.dreamwalkers.ecs.component

import com.badlogic.ashley.core.Component

data class AerialComponent(
        var terrainContacts: Int = 0
) : Component {

    val isAirborne: Boolean
        get() = terrainContacts == 0

}
