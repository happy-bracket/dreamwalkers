package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.physics.box2d.Fixture
import ru.substancial.dreamwalkers.ecs.component.AerialComponent
import ru.substancial.dreamwalkers.ecs.component.ContactComponent
import ru.substancial.dreamwalkers.ecs.component.TerrainComponent
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.ecs.has
import ru.substancial.dreamwalkers.ecs.maybeExtract
import ru.substancial.dreamwalkers.physics.BodyProp
import ru.substancial.dreamwalkers.physics.entity
import ru.substancial.dreamwalkers.physics.getProps
import ru.substancial.dreamwalkers.utilities.RegisteringSystem
import ru.substancial.dreamwalkers.utilities.justListen

class AerialSystem : RegisteringSystem() {

    private val contacts by justListen(
            Family.all(ContactComponent::class.java).get(),
            { e ->
                val contact = e.extract<ContactComponent>()
                tryReactToTerrainContact(contact.fixtureA, contact.fixtureB, true)
            },
            { e ->
                val contact = e.extract<ContactComponent>()
                tryReactToTerrainContact(contact.fixtureA, contact.fixtureB, false)
            }
    )

    private fun tryReactToTerrainContact(fixtureA: Fixture, fixtureB: Fixture, begin: Boolean) {
        val target: Entity
        try {
            val ea = fixtureA.body.entity ?: return
            val eb = fixtureB.body.entity ?: return

            target = when {
                fixtureA.getProps().props.contains(BodyProp.Foot) && eb.has<TerrainComponent>() -> ea
                fixtureB.getProps().props.contains(BodyProp.Foot) && ea.has<TerrainComponent>() -> eb
                else -> return
            }
            target.maybeExtract<AerialComponent>()?.let { it.terrainContacts += if (begin) 1 else -1 }
        } catch (e: TypeCastException) {
            Gdx.app.log("egor2", "${fixtureA.getProps().props}")
            Gdx.app.log("egor2", "${fixtureB.getProps().props}")
            return
        }
    }

}
