package ru.substancial.dreamwalkers.controls

import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor

fun Multiplex(vararg processors: InputProcessor): InputProcessor =
        when (processors.size) {
            0 -> InputAdapter()
            1 -> processors.first()
            else -> InputMultiplexer(*processors)
        }