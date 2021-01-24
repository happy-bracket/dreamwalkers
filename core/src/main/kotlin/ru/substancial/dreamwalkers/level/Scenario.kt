package ru.substancial.dreamwalkers.level

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.World
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.jse.JsePlatform
import ru.substancial.dreamwalkers.ecs.entity.EntitySpawner
import ru.substancial.dreamwalkers.utilities.lua
import java.util.*

interface SaveFile

class ScenarioHolder(
        scenarioName: String,
        interactor: ScenarioCallbacks,
        engine: Engine,
        world: World,
        spawner: EntitySpawner
) {

    private val globals = JsePlatform.standardGlobals()

    private val cachedEntities: WeakHashMap<Entity, LuaValue> = WeakHashMap<Entity, LuaValue>()

    private val interactor = interactor.lua
    private val spawner = spawner.lua
    private val world = world.lua

    // (initiator, target, interactor, level, engine, world, spawner)
    private val invoker: Array<LuaValue?> = arrayOf(
            null, null,
            this.interactor, null,
            engine.lua, this.world,
            this.spawner
    )

    init {
        val scenarioScript = globals.loadfile(scenarioName)
        scenarioScript.call()
    }

    fun initialize(saveFile: SaveFile?) {
        globals["init"](arrayOf(interactor, saveFile.lua, world, spawner))
    }

    fun processInteraction(initiator: Entity, target: Entity) {
        callEventFunction("process_interaction", initiator, target)
    }

    fun processCollision(initiator: Entity, target: Entity) {
        callEventFunction("process_collision", initiator, target)
    }

    fun setLevel(level: Level) {
        invoker[3] = level.lua
    }

    private fun callEventFunction(
            functionName: String,
            initiator: Entity,
            target: Entity
    ) {
        val cachedInitiator = cachedEntities.getOrPut(initiator) { initiator.lua }
        val cachedTarget = cachedEntities.getOrPut(target) { target.lua }
        invoker[0] = cachedInitiator
        invoker[1] = cachedTarget
        globals[functionName](invoker)
    }

}

interface ScenarioCallbacks {

    fun loadLevel(name: String): Level

}