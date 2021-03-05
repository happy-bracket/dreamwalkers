package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Family
import ktx.box2d.distanceJointWith
import ru.substancial.dreamwalkers.ecs.component.BodyComponent
import ru.substancial.dreamwalkers.ecs.component.StuckComponent
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.ecs.maybeExtract
import ru.substancial.dreamwalkers.utilities.RegisteringSystem

class StuckSystem : RegisteringSystem() {

    private val sticks by multiple(
            Family.all(StuckComponent::class.java).get()
    )
    private val listener: Unit by listener(
            Family.all(StuckComponent::class.java).get(),
            Unit,
            { _, e ->
                val sc = e.extract<StuckComponent>()
                val anchorBody = sc.anchor.maybeExtract<BodyComponent>()?.pushbox ?: return@listener
                val draggedBody = sc.dragged.maybeExtract<BodyComponent>()?.pushbox ?: return@listener
                anchorBody.distanceJointWith(draggedBody) {
                    length = 0f
                }
            },
            { _, _ -> },
            {}
    )

    override fun update(deltaTime: Float) {
        sticks.forEach {
            var dl = it.extract<StuckComponent>().durationLeft
            dl -= deltaTime
        }
    }

}
