package ru.substancial.dreamwalkers.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import ru.substancial.dreamwalkers.Core
import ru.substancial.dreamwalkers.utilities.ClearScreen
import ru.substancial.dreamwalkers.utilities.addProcessor
import ru.substancial.dreamwalkers.utilities.removeProcessor

class MainScreen(private val core: Core) : ScreenAdapter(), HasDisplay by HasDisplayMixin() {

    private val skin = Skin(Gdx.files.internal("assets/testskin/uiskin.json"))
    private val stage: Stage = Stage(FitViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(), camera))

    init {
        val textLogo = Label("DREAMWALKERS", skin)
        val startButton = TextButton("start", skin)
        val settingsButton = TextButton("settings", skin)

        startButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                core.screen = GameScreen(core, "assets/scenarios/test", "scenario.lua", null)
            }
        })

        val root = VerticalGroup()
        root.setFillParent(true)
        root.align(Align.center)
        root.addActor(textLogo)
        root.addActor(startButton)
        root.addActor(settingsButton)

        stage.addActor(root)

        stage.isDebugAll = true
        Gdx.input.addProcessor(stage)
    }

    override fun render(delta: Float) {
        ClearScreen()
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun dispose() {
        stage.dispose()
        skin.dispose()
        Gdx.input.removeProcessor(stage)
    }

}