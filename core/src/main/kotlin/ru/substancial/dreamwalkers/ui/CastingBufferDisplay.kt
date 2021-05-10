package ru.substancial.dreamwalkers.ui

import ru.substancial.dreamwalkers.ecs.system.Symbol

interface CastingBufferDisplay {

    fun add(sym: Symbol)

    fun clear()

}
