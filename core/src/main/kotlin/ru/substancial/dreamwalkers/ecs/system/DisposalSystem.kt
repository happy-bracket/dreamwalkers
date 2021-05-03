package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Family
import ru.substancial.dreamwalkers.ecs.component.DisposableComponent
import ru.substancial.dreamwalkers.utilities.RegisteringSystem
import ru.substancial.dreamwalkers.utilities.justListen

class DisposalSystem : RegisteringSystem() {

    private val listener by justListen(
        Family.exclude().get(),
        {},
        { e ->
            e.components.forEach {
                if (it is DisposableComponent) it.dispose()
            }
        }
    )

}
