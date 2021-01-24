package ru.substancial.dreamwalkers.ecs

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity

inline operator fun <T : Component> Entity.contains(mapper: ComponentMapper<T>): Boolean =
        mapper.has(this)

val mappersCache = HashMap<Class<*>, ComponentMapper<*>>()
inline fun <reified T : Component> gm(): ComponentMapper<*> {
    val type = T::class.java
    return mappersCache.getOrPut(type) { ComponentMapper.getFor(type) }
}

inline fun <reified T : Component> Entity.extract(): T {
    return gm<T>()[this] as T
}

inline fun <reified T : Component> Entity.maybeExtract(): T? {
    val mapper = gm<T>()
    return if (mapper in this) {
        mapper[this] as T
    } else null
}