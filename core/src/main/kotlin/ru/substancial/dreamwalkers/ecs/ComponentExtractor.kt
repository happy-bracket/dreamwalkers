package ru.substancial.dreamwalkers.ecs

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity

inline operator fun <T : Component> Entity.contains(mapper: ComponentMapper<T>): Boolean =
        mapper.has(this)

inline fun <reified T :Component> Entity.has(): Boolean =
        gm<T>().has(this)

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

inline operator fun <reified T : Component> Entity.component1(): T {
    return gm<T>()[this] as T
}

inline operator fun <reified T : Component> Entity.component2(): T {
    return gm<T>()[this] as T
}

inline operator fun <reified T : Component> Entity.component3(): T {
    return gm<T>()[this] as T
}

inline operator fun <reified T : Component> Entity.component4(): T {
    return gm<T>()[this] as T
}

inline operator fun <reified T : Component> Entity.component5(): T {
    return gm<T>()[this] as T
}

inline operator fun <reified T : Component> Entity.component6(): T {
    return gm<T>()[this] as T
}

inline operator fun <reified T : Component> Entity.component7(): T {
    return gm<T>()[this] as T
}

inline operator fun <reified T : Component> Entity.component8(): T {
    return gm<T>()[this] as T
}

inline operator fun <reified T : Component> Entity.component9(): T {
    return gm<T>()[this] as T
}

inline operator fun <reified T : Component> Entity.component10(): T {
    return gm<T>()[this] as T
}
