package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import ru.substancial.dreamwalkers.controls.TheController
import ru.substancial.dreamwalkers.ecs.component.*
import ru.substancial.dreamwalkers.ecs.extract

class ControlsSystem(
        private val controller: TheController
) : EntitySystem() {

    init {
        controller.airTriggerDownListener = {
            targetInput.leftTriggerListener(true)
        }

        controller.airTriggerUpListener = {
            targetInput.leftTriggerListener(false)
        }
    }

    private var _targetInput: InputComponent? = null
    private val targetInput: InputComponent
        get() = _targetInput!!

    override fun addedToEngine(engine: Engine) {
        _targetInput = engine.getEntitiesFor(inputFamily).first().extract()
    }

    override fun removedFromEngine(engine: Engine?) {
        controller.airTriggerDownListener = {}
        controller.airTriggerUpListener = {}
        _targetInput = null
    }

    override fun update(deltaTime: Float) {
        targetInput.leftStick.set(controller.pollLeftStick())
        targetInput.rightStick.set(controller.pollRightStick())
        targetInput.leftTriggerDown = controller.airTriggerDown
    }

    // if I ever need it again
    private fun psychokineticSwordControl() {
        //        val lunaBody = luna.extract<BodyComponent>().body
//        val (airborne) = luna.extract<AerialComponent>()
//        val luna = luna.extract<LunaComponent>()
//        val weaponProps = weapon.extract<WeaponComponent>()
//        val weaponBody = weapon.extract<BodyComponent>().body
//
//        if (!airborne) {
//            val direction = controller.pollLeftStick()
//                    .nor()
//                    .scl(5f, 0f)
//            lunaBody.applyForceToCenter(direction, true)
//        } else {
//            if (controller.airTriggerDown)
//                lunaBody.applyForceToCenter(Vector2(0f, 8f), true)
//        }
//
//        val rs = controller.pollRightStick()
//        val destinationRelativeToLuna = when {
//            rs.isZero -> Vector2(weaponProps.weaponDistance, 0f).rotate(225f)
//            else -> rs.nor().setLength(weaponProps.weaponDistance)
//        }
//
//        val equilibrium = weaponProps.weaponDistance - 1.5f
//
//        val weaponToLuna = lunaBody.worldCenter.cpy().sub(weaponBody.worldCenter)
//        val distance = weaponToLuna.len()
//        val counterForce = if (distance >= equilibrium) {
//            weaponToLuna.nor().setLength(7f)
//        } else {
//            Vector2.Zero
//        }
//
//        weaponBody.setTransform(weaponBody.worldCenter, weaponToLuna.cpy().rotate90(1).angleRad())
//
//        val destination = lunaBody.getWorldPoint(destinationRelativeToLuna).cpy()
//
//        val applicationPoint = weaponBody.getWorldPoint(Vector2(0f, 0.75f)).cpy()
//        val counterForceApplicationPoint = weaponBody.getWorldPoint(Vector2(0f, -0.75f)).cpy()
//
//        val destinationAngle = weaponToLuna.cpy().scl(-1f).angle(destinationRelativeToLuna).let(::abs)
//
//        outside = weaponBody.worldCenter.dst(lunaBody.worldCenter) >= weaponProps.weaponDistance
//        Gdx.app.log("egor2", "dest: $destinationAngle")
//        if (destination.dst(weaponBody.position) <= 0.3f) {
//            val velocityDirection = weaponBody.linearVelocity
//            val tangent = weaponToLuna.cpy().rotate90(-velocityDirection.angle(weaponToLuna).toInt())
//            val stabilityImpulse = tangent.cpy().sub(velocityDirection).scl(weaponBody.mass)
//            weaponBody.applyImpulseToCenter(stabilityImpulse, true)
//        }
//
//        if (outside && destinationAngle <= 10f) {
//            val killVelocity = weaponBody.linearVelocity.cpy().scl(-weaponBody.mass)
//            weaponBody.applyImpulseToCenter(killVelocity, true)
//        }
//
//        val force = destination.sub(weaponBody.worldCenter).nor().setLength(7f)
//
//        /*drawer.projectionMatrix = camera.combined
//        drawer.begin(ShapeRenderer.ShapeType.Filled)
//        drawer.color = Color.BLUE
//        drawer.line(applicationPoint, applicationPoint.cpy().add(force))
//        drawer.circle(applicationPoint.x, applicationPoint.y, 0.15f)
//        drawer.color = Color.GREEN
//        drawer.line(counterForceApplicationPoint, counterForceApplicationPoint.cpy().add(counterForce))
//        drawer.circle(counterForceApplicationPoint.x, counterForceApplicationPoint.y, 0.15f)
//        drawer.end()*/
//
//        weaponBody.applyForce(force, applicationPoint, true)
//        weaponBody.applyForce(counterForce, counterForceApplicationPoint, true)
    }

    companion object {

        private val lunaFamily = Family.all(
                LunaComponent::class.java,
                AerialComponent::class.java,
                BodyComponent::class.java
        ).get()

        private val weaponFamily = Family.all(
                WeaponComponent::class.java,
                BodyComponent::class.java
        ).get()

        private val inputFamily = Family.all(InputComponent::class.java).get()

    }

}