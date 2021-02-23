package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Family
import ru.substancial.dreamwalkers.ecs.component.IdentityComponent
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.utilities.IdentityRegistry
import ru.substancial.dreamwalkers.utilities.RegisteringSystem

class RegistrySystem(private val registry: IdentityRegistry) : RegisteringSystem() {

    private val listener by listener(
            Family.all(IdentityComponent::class.java).get(),
            registry,
            { r, e -> r.put(e) },
            { r, e -> r.remove(e.extract<IdentityComponent>().id) },
            { r -> r.clear() }
    )

}
