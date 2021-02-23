package ru.substancial.dreamwalkers.level

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.World
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.jse.JsePlatform
import ru.substancial.dreamwalkers.ecs.entity.EntitySpawner
import ru.substancial.dreamwalkers.utilities.IdentityRegistry
import ru.substancial.dreamwalkers.utilities.LuaFunctionName
import ru.substancial.dreamwalkers.utilities.lua
import java.util.*

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
        globals["init"](invokerLua)
    }

    fun call(functionName: LuaFunctionName, initiator: Entity, target: Entity) {
        invoker.initiator = initiator
        invoker.target = target
        globals[functionName.name](invokerLua)
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
