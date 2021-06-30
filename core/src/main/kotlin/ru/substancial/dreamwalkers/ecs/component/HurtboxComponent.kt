package ru.substancial.dreamwalkers.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.Body
import ru.substancial.dreamwalkers.physics.destroy

class HurtboxComponent constructor(
    val hurtboxes: Map<Body, HurtboxFragment>
) : Component, DisposableComponent {

    val hitBy: MutableSet<HitboxComponent> = mutableSetOf()

    override fun dispose() {
        hurtboxes.keys.forEach(Body::destroy)
    }

}

class HurtboxFragment(
    val impactToDestroy: Float,
    val armor: ArmorProperties,
    val onDestroy: (Engine, Entity) -> Unit = { _, _ -> }
)

sealed class ArmorProperties {

    object NoArmor : ArmorProperties()

    /**
     * @property criticalImpact weapon velocity required to break armor
     * @property restitution defines impulse length with which striking weapon will be repelled in case of unsuccessful attack
     */
    class HasArmor(val criticalImpact: Float, val restitution: Float) : ArmorProperties()

}
