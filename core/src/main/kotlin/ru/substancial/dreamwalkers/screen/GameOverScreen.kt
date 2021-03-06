package ru.substancial.dreamwalkers.screen

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import ktx.scene2d.label
import ktx.scene2d.textButton
import ktx.scene2d.verticalGroup
import ru.substancial.dreamwalkers.Core

class GameOverScreen(
        private val core: Core,
        iconFile: String,
        title: String,
        description: String
) : HasDisplayScreenAdapter() {

    private val stage = Stage(viewport)

    init {
        val root = verticalGroup {
            setFillParent(true)
            label(title)
            label(description)
            textButton("To Main Menu") {
                this.addListener(object : ClickListener() {
                    override fun clicked(event: InputEvent?, x: Float, y: Float) {
                        core.screen = MainScreen(core)
                    }
                })
            }
        }
        stage.addActor(root)
    }

}