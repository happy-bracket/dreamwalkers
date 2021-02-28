package ru.substancial.dreamwalkers.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.Body

class HurtboxComponent(
        val hitBy: MutableSet<Entity>,
        val hurtboxes: Set<Body>
) : Component
