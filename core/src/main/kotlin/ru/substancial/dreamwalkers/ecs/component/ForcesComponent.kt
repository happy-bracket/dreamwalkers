package ru.substancial.dreamwalkers.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import kotlin.random.Random

class ForcesComponent : Component {

    val forces: HashMap<Reason, PendingForce> = HashMap()

}

interface Reason

class AnyReason : Reason {

    private val id: Long = Random.nextLong()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AnyReason

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

}

class PendingForce(
        val vector: Vector2,
        val applicationPoint: Vector2 = Vector2.Zero
)
