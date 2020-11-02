package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import ru.substancial.dreamwalkers.ecs.component.InputComponent
import ru.substancial.dreamwalkers.ecs.component.LookComponent
import ru.substancial.dreamwalkers.ecs.component.LunaComponent
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.utilities.RegisteringSystem

class LunaLookSystem : RegisteringSystem() {

    private val luna by singular(lunaFamily)
    private val input by singular(inputFamily)

    private var turnAroundDelayProgress = 0f

    override fun update(deltaTime: Float) {
        val look = luna.extract<LookComponent>()
        val actualInput = input.extract<InputComponent>()

        val desiredMovement = actualInput.leftStick

        if (desiredMovement.isZero) {
            turnAroundDelayProgress = 0f
            return
        }

        val moveAngle = desiredMovement.angle()
        val isDesiredMovementRight = moveAngle in 315f..0f || moveAngle in 0f..45f
        val isDesiredMovementLeft = moveAngle in 135f..225f

        if (!isDesiredMovementLeft && !isDesiredMovementRight) {
            turnAroundDelayProgress = 0f
            return
        }

        val isLookingRight = look.isLookingRight()

        if (isDesiredMovementRight == isLookingRight) {
            turnAroundDelayProgress = 0f
            return
        }

        turnAroundDelayProgress += deltaTime
        if (turnAroundDelayProgress >= turnAroundDelay) {
            look.lookDirection.scl(-1f)
            turnAroundDelayProgress = 0f
        }
    }

    companion object {

        private const val turnAroundDelay = 0.6f

        private val lunaFamily = Family.all(
                LunaComponent::class.java,
                LookComponent::class.java
        ).get()

        private val inputFamily = Family.all(InputComponent::class.java).get()

    }

}