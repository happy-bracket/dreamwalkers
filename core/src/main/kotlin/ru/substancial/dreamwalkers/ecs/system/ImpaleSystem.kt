package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Family
import com.badlogic.gdx.physics.box2d.World
import ktx.box2d.distanceJointWith
import ru.substancial.dreamwalkers.ecs.component.BodyComponent
import ru.substancial.dreamwalkers.ecs.component.ImpaleComponent
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.ecs.maybeExtract
import ru.substancial.dreamwalkers.utilities.RegisteringSystem

class ImpaleSystem(
        private val world: World
) : RegisteringSystem() {

    private val impales by multiple(
            Family.all(ImpaleComponent::class.java).get()
    )
    private val listener: Unit by listener(
            Family.all(ImpaleComponent::class.java).get(),
            Unit,
            { _, e ->
                val sc = e.extract<ImpaleComponent>()
                val anchorBody = sc.anchor.maybeExtract<BodyComponent>()?.pushbox ?: return@listener
                val draggedBody = e.maybeExtract<BodyComponent>()?.pushbox ?: return@listener
                sc.joint = anchorBody.distanceJointWith(draggedBody) {
                    length = 0f
                }
            },
            { _, _ -> },
            {}
    )

    override fun update(deltaTime: Float) {
        impales.forEach {
            val sc = it.extract<ImpaleComponent>()
            sc.durationLeft -= deltaTime
            if (sc.durationLeft <= 0f) {
                world.destroyJoint(sc.joint)
                it.remove(ImpaleComponent::class.java)
            }
        }
    }

}
