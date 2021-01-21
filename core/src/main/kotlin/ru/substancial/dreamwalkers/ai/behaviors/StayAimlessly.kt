package ru.substancial.dreamwalkers.ai.behaviors

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import ru.substancial.dreamwalkers.ai.AiEnvironment
import ru.substancial.dreamwalkers.ecs.component.MovementComponent
import ru.substancial.dreamwalkers.ecs.extract

class StayAimlessly : LeafTask<AiEnvironment>() {

    override fun execute(): Status {
        `object`.thisEntity.extract<MovementComponent>().desiresToMove = false
        return Status.RUNNING
    }

    override fun copyTo(task: Task<AiEnvironment>?): Task<AiEnvironment>? {
        return task
    }

}