package ru.substancial.dreamwalkers.ai.behaviors

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.ai.btree.Task.Status.FAILED
import com.badlogic.gdx.ai.btree.Task.Status.SUCCEEDED
import ru.substancial.dreamwalkers.ai.AiPair
import ru.substancial.dreamwalkers.bodies.LunaBody
import ru.substancial.dreamwalkers.ecs.component.PositionComponent
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.physics.info

class SeesLuna : LeafTask<AiPair>() {

    override fun execute(): Status {
        val entities = `object`
        val lunaPosition = entities.luna.extract<PositionComponent>().xy
        val thisPosition = entities.thisEntity.extract<PositionComponent>().xy
        var sees = true
        entities.world.rayCast({ fixture, _, _, _ ->
            if (fixture.info?.tag !is LunaBody) {
                sees = false
                0f
            } else {
                1f
            }
        }, thisPosition, lunaPosition)
        return if (sees) {
            SUCCEEDED
        } else {
            FAILED
        }
    }

    override fun copyTo(task: Task<AiPair>?): Task<AiPair>? {
        return task
    }

}