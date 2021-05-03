package ru.substancial.dreamwalkers.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.Fixture

// TODO: rewrite this completely and attach HitboxComponent directly to owner entity, similar to HurtboxComponent
// because it's easier to control
class HitboxComponent(
    val owner: Entity,
    val fragments: Map<Fixture, HitboxFragment>,
    var isActive: Boolean = false
) : Component

class HitboxFragment(
    val fixture: Fixture,
    val damageType: DamageType,
    val impactScale: Float = 1f
)

sealed class DamageType {
    object Cut : DamageType()
    class Pierce(val offsetAngle: Float = 0f) : DamageType()
    object Blunt : DamageType()
}
