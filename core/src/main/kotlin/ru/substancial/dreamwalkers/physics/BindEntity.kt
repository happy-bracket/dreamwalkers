package ru.substancial.dreamwalkers.physics

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.Body

var Body.entity: Entity
    get() = userData as Entity
    set(value) { userData = value }
