package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.physics.box2d.World
import ru.substancial.dreamwalkers.ai.AiPair
import ru.substancial.dreamwalkers.ecs.component.AiComponent
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.utilities.RegisteringSystem

class AiSystem(private val world: World, private val luna: Entity) : RegisteringSystem() {

    private val mobs by multiple(
            Family.all(AiComponent::class.java).get(),
            { entity -> entity.extract<AiComponent>().behaviorTree.`object` = AiPair(luna, entity, world) },
            { entity -> entity.extract<AiComponent>().behaviorTree.`object` = null }
    )
    private var stepAccumulator = 0f

    override fun update(deltaTime: Float) {
        stepAccumulator += deltaTime
        if (stepAccumulator > STEP_ACCUMULATED) {
            mobs.forEach { entity ->
                val ai = entity.extract<AiComponent>()
                ai.behaviorTree.step()
            }
            stepAccumulator = 0f
        }
    }

    companion object {

        private const val STEP_ACCUMULATED = 1 / 45f

    }

}