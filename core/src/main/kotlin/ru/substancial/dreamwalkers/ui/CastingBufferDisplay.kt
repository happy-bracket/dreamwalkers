package ru.substancial.dreamwalkers.ui

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import ru.substancial.dreamwalkers.ecs.system.Symbol

interface CastingBufferDisplay {

    fun add(sym: Symbol)

    fun clearBuffer()

}

class DevCastingBufferDisplay(skin: Skin) : Label("N N N N", skin), CastingBufferDisplay {

    private val buffer = mutableListOf("N", "N", "N", "N")
    private var current = 0

    override fun add(sym: Symbol) {
        buffer[current] = sym::class.java.simpleName
        current++
        render()
    }

    override fun clearBuffer() {
        for (i in buffer.indices)
            buffer[i] = "N"
        current = 0
        render()
    }

    private fun render() {
        setText(buffer.joinToString(" "))
    }

}
