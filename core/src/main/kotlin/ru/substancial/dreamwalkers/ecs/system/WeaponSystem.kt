package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Family
import com.badlogic.gdx.math.Vector2
import ru.substancial.dreamwalkers.ecs.component.*
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.utilities.RegisteringSystem

class WeaponSystem : RegisteringSystem() {

    private val weapon by singular(weaponFamily)
    private val input by singular(inputFamily)
    private val luna by singular(lunaFamily)

    override fun update(deltaTime: Float) {
        val weaponProps = weapon.extract<WeaponComponent>()
        val lunaBody = luna.extract<BodyComponent>().body
        val weaponBody = weapon.extract<BodyComponent>().body
        val input = input.extract<InputComponent>()
        val isLookingRight = luna.extract<LookComponent>().isLookingRight()

        val rs = input.rightStick.cpy()
        val destinationRelativeToLuna = when {
            rs.isZero ->
                Vector2(weaponProps.weaponDistance, 0f).rotate(
                        if (isLookingRight) 225f else -45f
                )
            else -> rs.nor().setLength(weaponProps.weaponDistance)
        }
        val destination = lunaBody.getWorldPoint(destinationRelativeToLuna).cpy()
        val force = destination.sub(weaponBody.worldCenter).nor().setLength(7f)
        weaponBody.applyForceToCenter(force, true)

        val weaponToLuna = lunaBody.worldCenter.cpy().sub(weaponBody.worldCenter)
        weaponBody.setTransform(weaponBody.worldCenter, weaponToLuna.cpy().rotate90(1).angleRad())
    }

    companion object {

        private val weaponFamily = Family.all(
                WeaponComponent::class.java,
                BodyComponent::class.java
        ).get()

        private val lunaFamily = Family.all(
                LunaComponent::class.java,
                AerialComponent::class.java,
                BodyComponent::class.java,
                LookComponent::class.java
        ).get()

        private val inputFamily = Family.all(InputComponent::class.java).get()

    }

}