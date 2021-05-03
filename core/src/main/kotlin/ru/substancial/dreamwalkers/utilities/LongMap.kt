package ru.substancial.dreamwalkers.utilities

import com.badlogic.gdx.utils.LongMap

operator fun <V> LongMap<V>.set(key: Long, value: V) {
    put(key, value)
}
