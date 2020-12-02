package ru.substancial.dreamwalkers.utilities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20

fun ClearScreen(
        r: Float = 0f,
        g: Float = 0f,
        b: Float = 0f,
        a: Float = 1f
) {
    Gdx.gl.glClearColor(r, g, b, a)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
}