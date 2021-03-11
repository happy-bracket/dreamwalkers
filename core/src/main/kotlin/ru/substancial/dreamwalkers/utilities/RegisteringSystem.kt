package ru.substancial.dreamwalkers.utilities

import com.badlogic.ashley.core.*
import com.badlogic.ashley.utils.ImmutableArray
import ru.substancial.dreamwalkers.utilities.RegisteringSystem.Storage.GetEntitiesStorage.*
import ru.substancial.dreamwalkers.utilities.RegisteringSystem.Storage.GetEntitiesStorage.Optional
import java.util.*
import kotlin.reflect.KProperty

abstract class RegisteringSystem : EntitySystem() {

    private val markers: LinkedList<RetrieveMarker> = LinkedList()

    fun singular(
            family: Family
    ): Singular {
        val storage = Singular()
        markers.add(RetrieveMarker(family, storage))
        return storage
    }

    fun optional(
            family: Family
    ) : Optional {
        val storage = Optional()
        markers.add(RetrieveMarker(family, storage))
        return storage
    }

    fun multiple(
            family: Family
    ): Multiple {
        val storage = Multiple()
        markers.add(RetrieveMarker(family, storage))
        return storage
    }

    fun <T : Any> listener(
            family: Family,
            target: T,
            onAdded: (T, Entity) -> Unit,
            onRemoved: (T, Entity) -> Unit,
            clear: (T) -> Unit
    ): Storage.FromListener<T> {
        val storage = Storage.FromListener(target, onAdded, onRemoved, clear)
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
            it.removedFromEngine(engine)
        }
    }

    sealed class Storage {

        abstract fun addedToEngine(engine: Engine, family: Family)
        abstract fun removedFromEngine(engine: Engine)

        sealed class GetEntitiesStorage : Storage() {

            protected var stored: ImmutableArray<Entity>? = null

            override fun addedToEngine(engine: Engine, family: Family) {
                stored = engine.getEntitiesFor(family)
            }

            override fun removedFromEngine(engine: Engine) {
                stored = null
            }

            class Singular : GetEntitiesStorage() {

                operator fun getValue(thisRef: RegisteringSystem, prop: KProperty<*>): Entity {
                    val s = stored ?: throw EngineNotBoundException()
                    return s.firstOrNull() ?: throw IllegalStateException("No entity with required family was found in engine on access")
                }

            }

            class Optional : GetEntitiesStorage() {

                operator fun getValue(thisRef: RegisteringSystem, prop: KProperty<*>): Entity? {
                    val s = stored ?: throw EngineNotBoundException()
                    return s.firstOrNull()
                }

            }

            class Multiple : GetEntitiesStorage() {

                operator fun getValue(thisRef: RegisteringSystem, prop: KProperty<*>): ImmutableArray<Entity> =
                        stored ?: throw EngineNotBoundException()

            }

        }

        class FromListener<T : Any>(
                private val target: T,
                private val onAdded: (T, Entity) -> Unit,
                private val onRemoved: (T, Entity) -> Unit,
                private val clear: (T) -> Unit
        ) : Storage() {

            private var listener: EntityListener? = null

            private val isBound: Boolean
                get() = listener != null

            operator fun getValue(thisRef: RegisteringSystem, prop: KProperty<*>): T =
                    if (isBound) target else throw EngineNotBoundException()

            override fun addedToEngine(engine: Engine, family: Family) {
                val initial = engine.getEntitiesFor(family)
                initial.forEach { onAdded(target, it) }
                val newListener = object : EntityListener {
                    override fun entityAdded(entity: Entity) {
                        onAdded(target, entity)
                    }

                    override fun entityRemoved(entity: Entity) {
                        onRemoved(target, entity)
                    }
                }
                engine.addEntityListener(family, newListener)
                listener = newListener
            }

            override fun removedFromEngine(engine: Engine) {
                engine.removeEntityListener(listener)
                listener = null
                clear(target)
            }

        }

    }

    class EngineNotBoundException : IllegalStateException("Accessing entity when engine was not bound")

    private class RetrieveMarker(
            private val family: Family,
            private val target: Storage
    ) {

        fun addedToEngine(engine: Engine) {
            target.addedToEngine(engine, family)
        }

        fun removedFromEngine(engine: Engine) {
            target.removedFromEngine(engine)
        }

    }

}