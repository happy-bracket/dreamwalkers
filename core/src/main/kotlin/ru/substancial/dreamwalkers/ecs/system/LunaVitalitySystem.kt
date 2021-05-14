package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Family
import ru.substancial.dreamwalkers.ecs.component.LunaComponent
import ru.substancial.dreamwalkers.ecs.component.PrismaticComponent
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.level.ScenarioCallbacks
import ru.substancial.dreamwalkers.utilities.RegisteringSystem
import ru.substancial.dreamwalkers.utilities.justListen

class LunaVitalitySystem(
    private val callbacks: ScenarioCallbacks
) : RegisteringSystem() {

    private val exclusionListener by justListen(
        Family.all(LunaComponent::class.java).get(),
        {},
        { e ->
            if (e.extract<PrismaticComponent>().isBroken())
                callbacks.gameOver("", "Pathetic Ending", "Is this how you intended it to be?")
        }
    )

}
