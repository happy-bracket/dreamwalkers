package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Family
import com.badlogic.gdx.physics.box2d.Fixture
import ru.substancial.dreamwalkers.ecs.component.ContactComponent
import ru.substancial.dreamwalkers.ecs.component.OnCollisionEndComponent
import ru.substancial.dreamwalkers.ecs.component.OnCollisionStartComponent
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.ecs.has
import ru.substancial.dreamwalkers.level.ScenarioHolder
import ru.substancial.dreamwalkers.physics.entity
import ru.substancial.dreamwalkers.utilities.RegisteringSystem
import ru.substancial.dreamwalkers.utilities.justListen

class ScenarioCollisionSystem(
    private val scenario: ScenarioHolder
) : RegisteringSystem() {

    private val listener by justListen(
        Family.all(ContactComponent::class.java).get(),
        { e -> reactToContact(e.extract(), true) },
        { e -> reactToContact(e.extract(), false) }
    )

    private fun reactToContact(contact: ContactComponent, begin: Boolean) {
        val entityA = contact.fixtureA.body.entity ?: return
        val entityB = contact.fixtureB.body.entity ?: return
        val functionName: String
        val initiator: Fixture
        val target: Fixture
        when {
            begin && entityA.has<OnCollisionStartComponent>() -> {
                functionName = entityA.extract<OnCollisionStartComponent>().callbackName
                initiator = contact.fixtureB
                target = contact.fixtureA
            }
            begin && entityB.has<OnCollisionStartComponent>() -> {
                functionName = entityB.extract<OnCollisionStartComponent>().callbackName
                initiator = contact.fixtureA
                target = contact.fixtureB
            }
            !begin && entityA.has<OnCollisionEndComponent>() -> {
                functionName = entityA.extract<OnCollisionEndComponent>().callbackName
                initiator = contact.fixtureB
                target = contact.fixtureA
            }
            !begin && entityB.has<OnCollisionEndComponent>() -> {
                functionName = entityB.extract<OnCollisionEndComponent>().callbackName
                initiator = contact.fixtureA
                target = contact.fixtureB
            }
            else -> return
        }
        scenario.call(
            functionName,
            initiator.body.entity!!,
            target.body.entity!!
        )
    }

}
