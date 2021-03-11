package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.physics.box2d.Fixture
import ru.substancial.dreamwalkers.ecs.component.ContactComponent
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
            { e -> onBegin(e.extract()) },
            { }
    )

    private fun onBegin(contact: ContactComponent) {
        val entityA = contact.fixtureA.body.entity
        val entityB = contact.fixtureB.body.entity
        if ((entityA.has<OnCollisionStartComponent>()).xor(entityB.has<OnCollisionStartComponent>())) {
            val functionName: String
            val initiator: Fixture
            val target: Fixture
            if (entityA.has<OnCollisionStartComponent>()) {
                functionName = entityA.extract<OnCollisionStartComponent>().callbackName
                initiator = contact.fixtureB
                target = contact.fixtureA
            } else {
                functionName = entityB.extract<OnCollisionStartComponent>().callbackName
                initiator = contact.fixtureA
                target = contact.fixtureB
            }
            scenario.call(
                    functionName,
                    initiator.body.entity,
                    target.body.entity
            )
        }
    }

}
