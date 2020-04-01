package ru.substancial.dreamwalkers.physics

import com.badlogic.gdx.physics.box2d.Body

data class BodyInfo<Tag>(
        val tag: Tag,
        val id: String
)

val Body.info: BodyInfo<*>
    get() = userData as BodyInfo<*>