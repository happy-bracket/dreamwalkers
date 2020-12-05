package ru.substancial.dreamwalkers.utilities

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor

fun Input.addProcessor(processor: InputProcessor) {
    when (val current = inputProcessor) {
        is InputProcessor -> {
            val mplx = InputMultiplexer()
            mplx.addProcessor(current)
            mplx.addProcessor(processor)
            inputProcessor = mplx
        }
        is InputMultiplexer -> {
            current.addProcessor(processor)
        }
    }
}

fun Input.removeProcessor(processor: InputProcessor) {
    when (val current = inputProcessor) {
        is InputProcessor -> {
            if (current == processor) {
                inputProcessor = null
            }
        }
        is InputMultiplexer -> {
            current.removeProcessor(processor)
        }
    }
}