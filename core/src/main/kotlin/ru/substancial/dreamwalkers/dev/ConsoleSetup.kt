package ru.substancial.dreamwalkers.dev

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Family
import com.strongjoshua.console.CommandExecutor
import com.strongjoshua.console.LogLevel
import ru.substancial.dreamwalkers.Core
import ru.substancial.dreamwalkers.ecs.component.AerialComponent
import ru.substancial.dreamwalkers.ecs.component.BodyComponent
import ru.substancial.dreamwalkers.ecs.component.LunaComponent
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.ecs.maybeExtract
import ru.substancial.dreamwalkers.ecs.system.CameraSystem
import ru.substancial.dreamwalkers.screen.MainScreen

class DwCommandExecutor(private val core: Core) : CommandExecutor() {

    var currentEngine: Engine? = null

    fun exitToMainScreen() {
        core.screen = MainScreen(core)
    }

    fun isLunaAirborne() {
        val engine = currentEngine ?: run {
            console.log("Engine is not attached", LogLevel.ERROR)
            return
        }
        val luna = engine.getEntitiesFor(Family.all(LunaComponent::class.java).get())
                ?.firstOrNull()
                ?: run {
                    console.log("No Luna in the engine", LogLevel.ERROR)
                    return
                }

        console.log(luna.extract<AerialComponent>().isAirborne.toString(), LogLevel.SUCCESS)
    }

    fun setCameraZoom(zoom: Float) {
        val engine = currentEngine ?: run {
            console.log("Engine is not attached", LogLevel.ERROR)
            return
        }
        val camSystem = engine.getSystem(CameraSystem::class.java)
        camSystem.setZoom(zoom)
        console.log("Zoom set to $zoom", LogLevel.SUCCESS)
    }

}