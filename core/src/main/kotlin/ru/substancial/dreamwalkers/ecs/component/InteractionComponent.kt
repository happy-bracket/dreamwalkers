package ru.substancial.dreamwalkers.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Fixture

class InteractionComponent(
    val sensorBody: Body,
    val interactiveName: String
) : Component {

    val interactions: MutableMap<Fixture, Interaction> = mutableMapOf()

}

class Interaction(
    val id: String,
    val functionName: String,
    val prompt: String
)
