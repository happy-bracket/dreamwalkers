package ru.substancial.dreamwalkers.utilities

import com.badlogic.ashley.core.Entity
import ru.substancial.dreamwalkers.ecs.component.IdentityComponent
import ru.substancial.dreamwalkers.ecs.maybeExtract

class IdentityRegistry {

    private val map = HashMap<String, Entity>()

    fun put(entity: Entity) {
        entity.maybeExtract<IdentityComponent>()?.id?.let {
            map[it] = entity
        }
    }

    fun remove(id: String) {
        map.remove(id)
    }

    fun clear() {
        map.clear()
    }

    fun get(id: String): Entity {
        return map[id] ?: throw IdentityException(id)
    }

    fun has(id: String): Boolean {
        return map.containsKey(id)
    }

    class IdentityException(id: String) : Exception("No entity was found with id $id")

}
