package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Family
import ru.substancial.dreamwalkers.ecs.component.LunaComponent
import ru.substancial.dreamwalkers.ecs.component.PrismaticComponent
import ru.substancial.dreamwalkers.ecs.has
import ru.substancial.dreamwalkers.ecs.maybeExtract
import ru.substancial.dreamwalkers.level.ScenarioCallbacks
import ru.substancial.dreamwalkers.utilities.RegisteringSystem

class DeathSystem(private val callbacks: ScenarioCallbacks) : RegisteringSystem() {

    private val entities by multiple(Family.one(PrismaticComponent::class.java).get())

    override fun update(deltaTime: Float) {
        entities.forEach { e ->
            e.maybeExtract<PrismaticComponent>()?.let { prism ->
                when {
                    !prism.isBroken() -> {}
                    e.has<LunaComponent>() -> processLunaDeath()
                    else -> engine.removeEntity(e)
                }
            }
        }
    }

    private fun processLunaDeath() {
        callbacks.gameOver("", "Luna freaking dies", "lmao")
    }

}
