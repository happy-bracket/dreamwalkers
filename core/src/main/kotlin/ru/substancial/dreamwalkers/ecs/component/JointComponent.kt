package ru.substancial.dreamwalkers.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.physics.box2d.Joint

class JointComponent(
        val joint: Joint
) : Component