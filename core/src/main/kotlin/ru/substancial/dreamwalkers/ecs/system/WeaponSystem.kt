package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.math.Vector2
import ru.substancial.dreamwalkers.controls.TheController
import ru.substancial.dreamwalkers.ecs.component.*
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.utilities.RegisteringSystem

class WeaponSystem(
        private val controller: TheController
) : RegisteringSystem() {

    private val weapon by optional(weaponFamily)
    private val luna by optional(lunaFamily)

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        controller.rightTriggerDownListener = {
            weapon?.extract<HitboxComponent>()?.isActive = true
        }
        controller.rightTriggerUpListener = {
            weapon?.extract<HitboxComponent>()?.isActive = false
        }
    }

    override fun removedFromEngine(engine: Engine) {
        super.removedFromEngine(engine)
        controller.rightTriggerDownListener = {}
        controller.rightTriggerUpListener = {}
    }

    override fun update(deltaTime: Float) {
        val luna = this.luna ?: return
        val weapon = this.weapon ?: return
        val weaponProps = weapon.extract<NightsEdgeComponent>()
        val weaponBody = weapon.extract<BodyComponent>().pushbox
        val lunaBody = luna.extract<BodyComponent>().pushbox

        val isLookingRight = luna.extract<LookComponent>().isLookingRight()

        val isWeaponActive = controller.rightTriggerDown

        val rs = if (isWeaponActive) controller.rightStick else Vector2()
        val destinationRelativeToLuna = when {
            rs.isZero ->
                Vector2(weaponProps.handleLength, 0f).rotate(
                        if (isLookingRight) 225f else -45f
                )
            else -> rs.nor().setLength(weaponProps.handleLength)
        }
        val destination = lunaBody.getWorldPoint(destinationRelativeToLuna).cpy()
        val direction = destination.sub(weaponBody.position)
        val force = direction.setLength(weaponProps.pullForceMagnitude)
        weaponBody.applyForceToCenter(force, true)

        val reactionForce = weaponProps.handleJoint.getReactionForce(1f / deltaTime)
        lunaBody.applyForceToCenter(reactionForce, true)

        val weaponToLuna = lunaBody.worldCenter.cpy().sub(weaponBody.worldCenter)
        weaponBody.setTransform(weaponBody.worldCenter, weaponToLuna.rotate90(1).angleRad())
    }

    companion object {

        private val weaponFamily = Family.all(
                NightsEdgeComponent::class.java,
                BodyComponent::class.java
        ).get()

        private val lunaFamily = Family.all(
                LunaComponent::class.java,
                AerialComponent::class.java,
                BodyComponent::class.java,
                LookComponent::class.java
        ).get()

    }

}

