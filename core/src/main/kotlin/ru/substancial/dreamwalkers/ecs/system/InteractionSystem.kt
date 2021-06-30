package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.physics.box2d.World
import ktx.box2d.body
import ktx.box2d.circle
import ktx.box2d.filter
import ru.substancial.dreamwalkers.ecs.component.ContactComponent
import ru.substancial.dreamwalkers.ecs.component.Interaction
import ru.substancial.dreamwalkers.ecs.component.InteractionComponent
import ru.substancial.dreamwalkers.ecs.component.PositionComponent
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.ecs.has
import ru.substancial.dreamwalkers.ecs.maybeExtract
import ru.substancial.dreamwalkers.ecs.other.AvailableInteractions
import ru.substancial.dreamwalkers.physics.Filters
import ru.substancial.dreamwalkers.physics.entity
import ru.substancial.dreamwalkers.utilities.RegisteringSystem
import ru.substancial.dreamwalkers.utilities.justListen
import ru.substancial.dreamwalkers.utilities.namedDebug
import ru.substancial.dreamwalkers.utilities.namedError

class InteractionSystem(
    private val world: World,
    private val interactions: AvailableInteractions
) : RegisteringSystem() {

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
                interactions.add(interaction)
            }
            begin && eb.has<InteractionComponent>() -> {
                val interactionComponent = eb.extract<InteractionComponent>()
                val interaction = interactionComponent.interactions[fb] ?: return
                interactions.add(interaction)
            }
            !begin && ea.has<InteractionComponent>() -> {
                val interactionComponent = ea.extract<InteractionComponent>()
                val interaction = interactionComponent.interactions[fa] ?: return
                interactions.remove(interaction.id)
            }
            !begin && eb.has<InteractionComponent>() -> {
                val interactionComponent = eb.extract<InteractionComponent>()
                val interaction = interactionComponent.interactions[fb] ?: return
                interactions.remove(interaction.id)
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

    fun makeInteractive(
        entity: Entity,
        interactiveName: String
    ) {
        if (entity.has<InteractionComponent>()) {
            Gdx.app.namedDebug("tried to make an entity interactive, but it already was; skip")
        }
        val sensorBody = world.body {
            this.userData = entity
        }
        entity.add(InteractionComponent(sensorBody, interactiveName))
    }

    fun addInteraction(
        entity: Entity,
        radius: Float,
        interactionId: String,
        callbackName: String,
        prompt: String
    ) {
        val interaction = entity.maybeExtract<InteractionComponent>() ?: run {
            Gdx.app.namedError("tried to add interaction to entity that is not interactive; skip")
            return
        }
        val fixture = interaction.sensorBody.circle(radius = radius) {
            isSensor = true
            filter {
                categoryBits = Filters.Interactive
                maskBits = Filters.Pushbox
            }
        }
        interaction.interactions[fixture] = Interaction(interactionId, callbackName, prompt)
    }

    fun suggest(
        interactionId: String,
        callbackName: String,
        prompt: String
    ) {
        interactions.add(Interaction(interactionId, callbackName, prompt))
    }

    fun retract(interactionId: String) {
        interactions.remove(interactionId)
    }

}
