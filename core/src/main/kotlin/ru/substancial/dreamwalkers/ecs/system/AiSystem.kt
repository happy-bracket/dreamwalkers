package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Family
import com.badlogic.gdx.physics.box2d.World
import ru.substancial.dreamwalkers.ai.AiEnvironment
import ru.substancial.dreamwalkers.ecs.component.AiComponent
import ru.substancial.dreamwalkers.ecs.component.LunaComponent
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.utilities.RegisteringSystem

class AiSystem(private val world: World) : RegisteringSystem() {

    private val luna by optional(Family.all(LunaComponent::class.java).get())
    private val mobs by multiple(Family.all(AiComponent::class.java).get())
    private var stepAccumulator = 0f

    override fun update(deltaTime: Float) {
        stepAccumulator += deltaTime
        if (stepAccumulator > STEP_ACCUMULATED) {
            mobs.forEach { entity ->
                val ai = entity.extract<AiComponent>()
                if (ai.behaviorTree.`object` == null)
                    ai.behaviorTree.`object` = AiEnvironment(luna, entity, world)
                ai.behaviorTree.step()
            }
            stepAccumulator = 0f
        }
    }

    companion object {

        private const val STEP_ACCUMULATED = 1 / 45f

    }

}