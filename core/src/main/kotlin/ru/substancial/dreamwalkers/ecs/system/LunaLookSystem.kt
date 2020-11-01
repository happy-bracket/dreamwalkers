package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Family
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

        val desiredMovement = actualInput.rightStick.cpy()

        val moveAngle = desiredMovement.angle()
        val isDesiredMovementRight = moveAngle >= 315f || moveAngle <= 45f
        val isDesiredMovementLeft = moveAngle >= 135f || moveAngle <= 225f

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