package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import ru.substancial.dreamwalkers.ecs.component.ContactComponent
import ru.substancial.dreamwalkers.ecs.component.InteractionComponent
import ru.substancial.dreamwalkers.ecs.component.PositionComponent
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.ecs.has
import ru.substancial.dreamwalkers.physics.entity
import ru.substancial.dreamwalkers.utilities.RegisteringSystem
import ru.substancial.dreamwalkers.utilities.justListen

class InteractionSystem : RegisteringSystem() {

    private val listener by justListen(
        Family.all(ContactComponent::class.java).get(),
        { entity -> process(entity, true) },
        { entity -> process(entity, false) }
    )

    private val interactiveEntities by multiple(
        Family.all(InteractionComponent::class.java, PositionComponent::class.java).get()
    )

    private fun process(contactEntity: Entity, begin: Boolean) {
        val contactComponent = contactEntity.extract<ContactComponent>()
        val fa = contactComponent.fixtureA
        val fb = contactComponent.fixtureB
        val ea = fa.body.entity ?: return
        val eb = fb.body.entity ?: return
        if (ea == eb) return
        when {
            begin && ea.has<InteractionComponent>() -> {
                val interactionComponent = ea.extract<InteractionComponent>()
                val interaction = interactionComponent.interactions[fa] ?: return
            }
            begin && eb.has<InteractionComponent>() -> {
                val interactionComponent = eb.extract<InteractionComponent>()
                val interaction = interactionComponent.interactions[fb] ?: return
            }
            !begin && ea.has<InteractionComponent>() -> {
                val interactionComponent = ea.extract<InteractionComponent>()
                val interaction = interactionComponent.interactions[fa] ?: return
            }
            !begin && eb.has<InteractionComponent>() -> {
                val interactionComponent = eb.extract<InteractionComponent>()
                val interaction = interactionComponent.interactions[fb] ?: return
            }
        }
    }

    override fun update(deltaTime: Float) {
        interactiveEntities.forEach { e ->
            val position = e.extract<PositionComponent>()
            val interaction = e.extract<InteractionComponent>()
            interaction.sensorBody.let {
                it.setTransform(position.xy, it.angle)
            }
        }
    }

}
