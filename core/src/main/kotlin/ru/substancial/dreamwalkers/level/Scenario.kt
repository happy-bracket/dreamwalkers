package ru.substancial.dreamwalkers.level

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import org.luaj.vm2.lib.jse.JsePlatform
import ru.substancial.dreamwalkers.ecs.entity.EntitySpawner
import ru.substancial.dreamwalkers.utilities.IdentityRegistry
import ru.substancial.dreamwalkers.utilities.lua

interface SaveFile

class ScenarioHolder(
        scenarioName: String,
        interactor: ScenarioCallbacks,
        engine: Engine,
        registry: IdentityRegistry,
        spawner: EntitySpawner
) {

    private val globals = JsePlatform.standardGlobals()

    private val invoker = Invoker(null, null, interactor, engine, registry, spawner, null)
    private val invokerLua = invoker.lua

    init {
        val scenarioScript = globals.loadfile(scenarioName)
        scenarioScript.call()
    }

    fun initialize(saveFile: SaveFile?) {
        invoker.saveFile = saveFile
        globals["init"]?.invoke(invokerLua)
    }

    fun update(delta: Float) {
        globals["update"]?.invoke(delta.lua)
    }

    fun call(functionName: String, initiator: Entity, target: Entity) {
        invoker.initiator = initiator
        invoker.target = target
        globals[functionName]?.invoke(invokerLua)
    }

}

class Invoker(
        var initiator: Entity?,
        var target: Entity?,
        val interactor: ScenarioCallbacks,
        val engine: Engine,
        val registry: IdentityRegistry,
        val spawner: EntitySpawner,
        var saveFile: SaveFile?
)

interface ScenarioCallbacks {

    fun loadLevel(name: String)

}
