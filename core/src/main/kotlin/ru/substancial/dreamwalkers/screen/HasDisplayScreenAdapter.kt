package ru.substancial.dreamwalkers.screen

import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.viewport.FitViewport

abstract class HasDisplayScreenAdapter : ScreenAdapter() {

    protected val camera = OrthographicCamera()
    protected val viewport = FitViewport(16f, 9f, camera)

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

}