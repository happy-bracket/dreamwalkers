package ru.substancial.dreamwalkers.dev

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Family
import com.strongjoshua.console.CommandExecutor
import com.strongjoshua.console.LogLevel
import ru.substancial.dreamwalkers.Core
import ru.substancial.dreamwalkers.ecs.component.AerialComponent
import ru.substancial.dreamwalkers.ecs.component.BodyComponent
import ru.substancial.dreamwalkers.ecs.component.LunaComponent
import ru.substancial.dreamwalkers.ecs.maybeExtract
import ru.substancial.dreamwalkers.screen.MainScreen

class DwCommandExecutor(private val core: Core) : CommandExecutor() {

    var currentEngine: Engine? = null

    fun exitToMainScreen() {
        core.screen = MainScreen(core)
    }

    fun isLunaAirborne() {
        currentEngine?.getEntitiesFor(Family.all(LunaComponent::class.java).get())
                ?.firstOrNull()
                ?.maybeExtract<AerialComponent>()
                ?.let { console.log(it.toString(), LogLevel.SUCCESS) }
                ?: console.log("Engine was not attached", LogLevel.ERROR)
    }

    fun lunaVelocity() {
        currentEngine?.getEntitiesFor(Family.all(LunaComponent::class.java).get())
                ?.firstOrNull()
                ?.maybeExtract<BodyComponent>()
                ?.let { console.log(it.body.linearVelocity.toString(), LogLevel.SUCCESS) }
    }

}