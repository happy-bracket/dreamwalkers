package ru.substancial.dreamwalkers.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.physics.box2d.joints.RopeJoint

data class NightsEdgeComponent(
        val handleJoint: RopeJoint,
        val mass: Float,
        val handleLength: Float
) : Component {

    val pullForceMagnitude = (2 * handleLength * mass) / (0.5f * 0.5f)

}
