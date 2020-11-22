package ru.substancial.dreamwalkers.ai.behaviors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.ai.btree.Task.Status.RUNNING
import com.badlogic.gdx.math.Vector2
import ru.substancial.dreamwalkers.ai.AiPair
import ru.substancial.dreamwalkers.ecs.component.BodyComponent
import ru.substancial.dreamwalkers.ecs.component.MovementComponent
import ru.substancial.dreamwalkers.ecs.component.PositionComponent
import ru.substancial.dreamwalkers.ecs.extract

class ChaseLuna : LeafTask<AiPair>() {

    override fun execute(): Status {
        val entities = `object`
        val luna = entities.luna
        val thisEntity = entities.thisEntity
        val lunaPosition = luna.extract<PositionComponent>().xy
        val thisPosition = thisEntity.extract<PositionComponent>().xy
        val thisMovement = thisEntity.extract<MovementComponent>()
        val thisBody = thisEntity.extract<BodyComponent>().body
        val pullForce = Vector2(lunaPosition.x - thisPosition.x, 0f).nor().scl(thisMovement.pullForceMagnitude)
        thisMovement.desiresToMove = true
        thisBody.applyForceToCenter(pullForce, true)
        return RUNNING
    }

    override fun copyTo(task: Task<AiPair>?): Task<AiPair>? {
        return task
    }

}