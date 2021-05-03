package ru.substancial.dreamwalkers.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.physics.box2d.Body

data class BodyComponent(
    val pushbox: Body
) : Component, DisposableComponent {

    override fun dispose() {
        pushbox.world.destroyBody(pushbox)
    }

}
