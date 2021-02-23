package ru.substancial.dreamwalkers.physics

import com.badlogic.gdx.physics.box2d.Fixture
import ktx.box2d.FixtureDefinition

sealed class BodyProp {
    object Foot : BodyProp()
}

class FixtureProps(
        val props: Set<BodyProp>
)

fun FixtureDefinition.injectProps(vararg props: BodyProp) {
    userData = FixtureProps(setOf(*props))
}

private val EmptyFixtureProps = FixtureProps(emptySet())

fun Fixture.getProps(): FixtureProps = userData?.let { it as FixtureProps } ?: EmptyFixtureProps
