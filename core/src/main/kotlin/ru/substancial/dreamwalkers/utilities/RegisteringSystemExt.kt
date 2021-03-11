package ru.substancial.dreamwalkers.utilities

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family

fun <K> RegisteringSystem.map(
        family: Family,
        keyExtractor: (Entity) -> K
): RegisteringSystem.Storage.FromListener<HashMap<K, Entity>> {
    return this.listener(
            family,
            HashMap(),
            { s, e ->
                s[keyExtractor(e)] = e
            },
            { s, e ->
                s.remove(keyExtractor(e))
            },
            { s ->
                s.clear()
            }
    )
}

fun RegisteringSystem.justListen(
        family: Family,
        onAdded: (Entity) -> Unit,
        onRemoved: (Entity) -> Unit
): RegisteringSystem.Storage.FromListener<Unit> {
    return this.listener(
            family,
            Unit,
            { _, e -> onAdded(e) },
            { _, e -> onRemoved(e) },
            {}
    )
}
