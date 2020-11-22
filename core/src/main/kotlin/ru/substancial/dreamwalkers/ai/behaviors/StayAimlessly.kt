package ru.substancial.dreamwalkers.ai.behaviors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import ru.substancial.dreamwalkers.ai.AiPair
import ru.substancial.dreamwalkers.ecs.component.MovementComponent
import ru.substancial.dreamwalkers.ecs.extract

class StayAimlessly : LeafTask<AiPair>() {

    override fun execute(): Status {
        `object`.thisEntity.extract<MovementComponent>().desiresToMove = false
        return Status.RUNNING
    }

    override fun copyTo(task: Task<AiPair>?): Task<AiPair>? {
        return task
    }

}