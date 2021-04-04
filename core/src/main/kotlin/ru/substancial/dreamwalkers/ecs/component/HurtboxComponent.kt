package ru.substancial.dreamwalkers.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.Body

class HurtboxComponent(
        val hitBy: MutableSet<Entity>,
        val hurtboxes: Map<Body, HurtboxFragment>
) : Component

class HurtboxFragment(
        val impactToDestroy: Float,
        val armor: ArmorProperties
)

sealed class ArmorProperties {

    object NoArmor : ArmorProperties()

    /**
     * @property criticalImpact weapon velocity required to break armor
     * @property restitution defines impulse length with which striking weapon will be repelled in case of unsuccessful attack
     */
    class HasArmor(val criticalImpact: Float, val restitution: Float) : ArmorProperties()

}
