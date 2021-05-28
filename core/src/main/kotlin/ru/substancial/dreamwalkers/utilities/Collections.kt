package ru.substancial.dreamwalkers.utilities

import com.badlogic.gdx.utils.IntMap
import com.badlogic.gdx.utils.LongMap

operator fun <V> LongMap<V>.set(key: Long, value: V) {
    put(key, value)
}

operator fun <V> IntMap<V>.set(key: Int, value: V) {
    put(key, value)
}

inline fun <V : Any> IntMap<V>.getOrPut(key: Int, compute: () -> V): V {
    val value = get(key)
    return if (value == null) {
        val ret = compute()
        put(key, ret)
        ret
    } else {
        value
    }
}
