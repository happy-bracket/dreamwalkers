package ru.substancial.dreamwalkers.physics

import com.badlogic.gdx.physics.box2d.Fixture
import ktx.box2d.FixtureDefinition

data class FixtureInfo<Tag>(
        val tag: Tag,
        val id: String
)

val Fixture.info: FixtureInfo<*>?
    get() = userData as? FixtureInfo<*>

fun <T> FixtureDefinition.injectInfo(tag: T, id: String) {
    userData = FixtureInfo(tag, id)
}