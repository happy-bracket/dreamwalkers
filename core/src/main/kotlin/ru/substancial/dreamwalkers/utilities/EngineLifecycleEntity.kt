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
            family: Family,
            onAdded: (Entity) -> Unit = {},
            onRemoved: (Entity) -> Unit = {}
    ): Storage.Singular {
        val storage = Storage.Singular()
        markers.add(RetrieveMarker(family, storage, onAdded, onRemoved))
        return storage
    }

    fun multiple(
            family: Family,
            onAdded: (Entity) -> Unit,
            onRemoved: (Entity) -> Unit
    ): Storage.Multiple {
        val storage = Storage.Multiple()
        markers.add(RetrieveMarker(family, storage, onAdded, onRemoved))
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

        internal abstract var _stored: T?
        internal val stored: T
            get() = _stored ?: throw IllegalStateException("System was not bound to engine on access attempt")

        operator fun getValue(thisRef: RegisteringSystem, prop: KProperty<*>): T = stored

        class Singular : Storage<Entity>() {
            override var _stored: Entity? = null
        }

        class Multiple : Storage<ImmutableArray<Entity>>() {
            override var _stored: ImmutableArray<Entity>? = null
        }

    }

    private class RetrieveMarker(
            private val family: Family,
            private val target: Storage<*>,
            private val onAdded: (Entity) -> Unit,
            private val onRemoved: (Entity) -> Unit
    ) {

        fun addedToEngine(engine: Engine) {
            val entities = engine.getEntitiesFor(family)
            when (target) {
                is Storage.Singular -> {
                    val result = entities.first()
                    onAdded(result)
                    target._stored = result
                }
                is Storage.Multiple -> {
                    entities.forEach(onAdded)
                    target._stored = entities
                }
            }
        }

        fun removedFromEngine() {
            when (target) {
                is Storage.Singular -> {
                    onRemoved(target.stored)
                }
                is Storage.Multiple -> {
                    target.stored.forEach(onRemoved)
                }
            }
            target._stored = null
        }

    }

}