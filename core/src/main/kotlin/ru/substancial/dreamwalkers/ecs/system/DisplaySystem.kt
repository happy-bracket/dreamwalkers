package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Family
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import ru.substancial.dreamwalkers.ecs.component.DashComponent
import ru.substancial.dreamwalkers.ecs.component.LunaComponent
import ru.substancial.dreamwalkers.ecs.component.PrismaticComponent
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.ecs.maybeExtract
import ru.substancial.dreamwalkers.utilities.RegisteringSystem

class DisplaySystem(
    private val dashCooldown: ProgressBar,
    private val health: ProgressBar,
    private val shields: ProgressBar,
    private val mana: ProgressBar
) : RegisteringSystem() {

    private val luna by optional(Family.all(LunaComponent::class.java).get())

    override fun update(deltaTime: Float) {
        luna?.extract<DashComponent>()?.let { dash ->
            dashCooldown.value = dash.ticks / dash.cooldown
        }
        luna?.extract<PrismaticComponent>()?.let { prism ->
            health.value = prism.integrity.run { points / capacity }
            shields.value = prism.shield.run { points / capacity }
        }
    }

}
