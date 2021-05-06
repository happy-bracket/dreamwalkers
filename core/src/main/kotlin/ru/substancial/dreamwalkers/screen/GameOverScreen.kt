package ru.substancial.dreamwalkers.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import ktx.scene2d.label
import ktx.scene2d.textButton
import ktx.scene2d.verticalGroup
import ru.substancial.dreamwalkers.Core
import ru.substancial.dreamwalkers.utilities.ClearScreen
import ru.substancial.dreamwalkers.utilities.addProcessor
import ru.substancial.dreamwalkers.utilities.removeProcessor

class GameOverScreen(
        private val core: Core,
        iconFile: String,
        title: String,
        description: String
) : HasDisplayScreenAdapter() {

    private val stage = Stage()
    private val skin = Skin(Gdx.files.internal("assets/testskin/uiskin.json"))

    init {
        val root = VerticalGroup()
        val titleLabel = Label(title, skin)
        val descriptionLabel = Label(description, skin)
        val toMainMenu = TextButton("To Main Menu", skin)
        toMainMenu.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                core.screen = MainScreen(core)
            }
        })
        root.addActor(titleLabel)
        root.addActor(descriptionLabel)
        root.addActor(toMainMenu)
        root.setFillParent(true)
        stage.addActor(root)

        Gdx.input.addProcessor(stage)
    }

    override fun render(delta: Float) {
        ClearScreen()
        stage.act(delta)
        stage.draw()
    }

    override fun dispose() {
        Gdx.input.removeProcessor(stage)
    }

}
