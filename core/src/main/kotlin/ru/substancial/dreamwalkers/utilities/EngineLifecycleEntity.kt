package ru.substancial.dreamwalkers.utilities

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import java.util.*
import kotlin.reflect.KProperty

abstract class RegisteringSystem : EntitySystem() {

    private val markers: LinkedList<RetrieveMarker> = LinkedList()

    fun singular(
            family: Family
    ): Storage.Singular {
        val storage = Storage.Singular()
        markers.add(RetrieveMarker(family, storage))
        return storage
    }

    fun multiple(
            family: Family
    ): Storage.Multiple {
        val storage = Storage.Multiple()
        markers.add(RetrieveMarker(family, storage))
        return storage
    }

    override fun addedToEngine(engine: Engine) {
        markers.forEach {
            it.addedToEngine(engine)
        }
    }

    override fun removedFromEngine(engine: Engine) {
        markers.forEach {
            it.removedFromEngine()
        }
    }

    sealed class Storage<T : Any> {

        internal var _stored: ImmutableArray<Entity>? = null
        internal val stored: ImmutableArray<Entity>
            get() = _stored ?: throw IllegalStateException("System was not bound to engine on access attempt")

        class Singular : Storage<Entity>() {
            operator fun getValue(thisRef: RegisteringSystem, prop: KProperty<*>): Entity = stored.first()
        }

        class Multiple : Storage<ImmutableArray<Entity>>() {
            operator fun getValue(thisRef: RegisteringSystem, prop: KProperty<*>): ImmutableArray<Entity> = stored
        }

    }

    private class RetrieveMarker(
            private val family: Family,
            private val target: Storage<*>
    ) {

        fun addedToEngine(engine: Engine) {
            val entities = engine.getEntitiesFor(family)
            target._stored = entities
        }

        fun removedFromEngine() {
            target._stored = null
        }

    }

}