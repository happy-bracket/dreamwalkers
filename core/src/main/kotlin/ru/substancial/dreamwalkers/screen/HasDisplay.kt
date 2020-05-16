package ru.substancial.dreamwalkers.screen

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.viewport.FitViewport

interface HasDisplay {

    val camera: OrthographicCamera
    val viewport: FitViewport

}

class HasDisplayMixin : HasDisplay {

    override val camera = OrthographicCamera()
    override val viewport = FitViewport(16f, 9f, camera)

}