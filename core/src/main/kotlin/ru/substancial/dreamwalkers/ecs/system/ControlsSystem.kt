package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import ru.substancial.dreamwalkers.controls.TheController
import ru.substancial.dreamwalkers.ecs.component.AerialComponent
import ru.substancial.dreamwalkers.ecs.component.BodyComponent
import ru.substancial.dreamwalkers.ecs.component.LunaComponent
import ru.substancial.dreamwalkers.ecs.get
import ru.substancial.dreamwalkers.utilities.applyImpulseToCenter
import ru.substancial.dreamwalkers.utilities.checkDeadzone
import kotlin.math.abs
import kotlin.math.acos
import ru.substancial.dreamwalkers.ecs.ComponentExtractor as CE

class ControlsSystem(
        private val camera: OrthographicCamera,
        private val controller: TheController
) : EntitySystem() {

    init {
        controller.airTriggerDownListener = {
            if (isAirborne()) {
                val rawDir = controller.pollLeftStick()
                if (rawDir.checkDeadzone(0.3f)) {
                    val direction = rawDir.nor().scl(8f, 8f)
                    luna[CE.Body].body.applyImpulseToCenter(direction, true)
                }
            }
        }

        controller.airTriggerUpListener = {
            if (!isAirborne()) {
                luna[CE.Body].body.applyImpulseToCenter(Vector2(0f, 10f), true)
            }
        }
    }

    private val drawer = ShapeRenderer()

    private var outside: Boolean = false

    private var _luna: Entity? = null
    private val luna: Entity
        get() = _luna!!

    override fun addedToEngine(engine: Engine) {
        _luna = engine.getEntitiesFor(family).first()
    }

    override fun removedFromEngine(engine: Engine?) {
        _luna = null
        controller.airTriggerDownListener = {}
        controller.airTriggerUpListener = {}
    }

    override fun update(deltaTime: Float) {
        val lunaBody = extractBody()
        val (airborne) = luna[CE.Aerial]
        val luna = luna[CE.Luna]
        val weapon = luna.weapon

        if (!airborne) {
            val direction = controller.pollLeftStick()
                    .nor()
                    .scl(5f, 0f)
            lunaBody.applyForceToCenter(direction, true)
        } else {
            if (controller.airTriggerDown)
                lunaBody.applyForceToCenter(Vector2(0f, 8f), true)
        }

        val rs = controller.pollRightStick()
        val destinationRelativeToLuna = when {
            rs.isZero -> Vector2(luna.weaponDistance, 0f).rotate(225f)
            else -> rs.nor().setLength(luna.weaponDistance)
        }

        val equilibrium = luna.weaponDistance - 1.5f

        val weaponToLuna = lunaBody.worldCenter.cpy().sub(weapon.worldCenter)
        val distance = weaponToLuna.len()
        val counterForce = if (distance >= equilibrium) {
            weaponToLuna.nor().setLength(4f)
        } else {
            Vector2.Zero
        }

        weapon.setTransform(weapon.worldCenter, weaponToLuna.cpy().rotate90(1).angleRad())

        val destination = lunaBody.getWorldPoint(destinationRelativeToLuna).cpy()

        val applicationPoint = weapon.getWorldPoint(Vector2(0f, 0.75f)).cpy()
        val counterForceApplicationPoint = weapon.getWorldPoint(Vector2(0f, -0.75f)).cpy()

        outside = if (weapon.worldCenter.dst(lunaBody.worldCenter) >= luna.weaponDistance) {
            if (!outside) {
                val velocityDirection = weapon.linearVelocity
                val radiusVector = weaponToLuna.cpy().rotate90(1)
                val dot = velocityDirection.dot(radiusVector)
                val mag = velocityDirection.len() * radiusVector.len()
                val angle = MathUtils.radiansToDegrees * acos(abs(dot / mag))

                if (angle >= 60f) {
                    val counterImpulse = weapon.linearVelocity.cpy().rotate(180f).scl(weapon.mass, weapon.mass)
                    weapon.applyLinearImpulse(counterImpulse, weapon.worldCenter, true)
                }
            }
            true
        } else {
            false
        }

        val force = destination.sub(weapon.worldCenter).nor().setLength(4f)

        drawer.projectionMatrix = camera.combined
        drawer.begin(ShapeRenderer.ShapeType.Filled)
        drawer.color = Color.BLUE
        drawer.line(applicationPoint, applicationPoint.cpy().add(force))
        drawer.circle(applicationPoint.x, applicationPoint.y, 0.15f)
        drawer.color = Color.GREEN
        drawer.line(counterForceApplicationPoint, counterForceApplicationPoint.cpy().add(counterForce))
        drawer.circle(counterForceApplicationPoint.x, counterForceApplicationPoint.y, 0.15f)
        drawer.end()

        weapon.applyForce(force, applicationPoint, true)
        weapon.applyForce(counterForce, counterForceApplicationPoint, true)
    }

    private fun extractBody(): Body = luna[CE.Body].body

    private fun isAirborne(): Boolean = luna[CE.Aerial].isAirborne

    companion object {

        private val family = Family.all(
                LunaComponent::class.java,
                AerialComponent::class.java,
                BodyComponent::class.java
        ).get()

    }

}