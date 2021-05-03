package ru.substancial.dreamwalkers.physics

import com.badlogic.gdx.physics.box2d.Fixture
import ktx.box2d.FixtureDefinition

sealed class BodyProp {
    object Foot : BodyProp()
}

private var unityCounter = 0L
    get() {
        val res = field
        field += 1
        return res
    }

class FixtureProps(
    val props: Set<BodyProp>,
    val unityFactor: Long = unityCounter
)

fun FixtureDefinition.injectProps(vararg props: BodyProp) {
    userData = FixtureProps(setOf(*props))
}

private val EmptyFixtureProps = FixtureProps(emptySet())

fun Fixture.getProps(): FixtureProps = userData?.let { it as FixtureProps } ?: EmptyFixtureProps
