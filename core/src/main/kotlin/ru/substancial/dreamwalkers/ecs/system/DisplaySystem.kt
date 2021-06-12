package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Family
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import ru.substancial.dreamwalkers.ecs.component.DashComponent
import ru.substancial.dreamwalkers.ecs.component.LunaComponent
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.utilities.RegisteringSystem

class DisplaySystem(
    private val dashCooldown: ProgressBar
) : RegisteringSystem() {

    private val luna by optional(Family.all(LunaComponent::class.java).get())

    override fun update(deltaTime: Float) {
        val dash = luna?.extract<DashComponent>() ?: return
        dashCooldown.value = dash.ticks / dash.cooldown
    }

}
