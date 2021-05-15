package ru.substancial.dreamwalkers.ecs.component

import box2dLight.Light
import com.badlogic.ashley.core.Component

class LightComponent(
    val light: Light
) : Component, DisposableComponent {

    override fun dispose() {
        light.dispose()
    }

}
