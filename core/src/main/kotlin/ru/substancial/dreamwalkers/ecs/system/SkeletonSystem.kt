package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Family
import ru.substancial.dreamwalkers.ecs.component.PositionComponent
import ru.substancial.dreamwalkers.ecs.component.SkeletonComponent
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.ecs.maybeExtract
import ru.substancial.dreamwalkers.utilities.RegisteringSystem

class SkeletonSystem : RegisteringSystem() {

    private val entities by multiple(Family.all(SkeletonComponent::class.java).get())

    override fun update(deltaTime: Float) {
        entities.forEach {
            val component = it.extract<SkeletonComponent>()
            val animationState = component.animationState
            val skeleton = component.skeleton
            it.maybeExtract<PositionComponent>()?.let { (x, y) ->
                skeleton.setPosition(x, y)
            }
            animationState.update(deltaTime)
            animationState.apply(skeleton)
            animationState.getCurrent(0)
            skeleton.updateWorldTransform()
        }
    }

}
