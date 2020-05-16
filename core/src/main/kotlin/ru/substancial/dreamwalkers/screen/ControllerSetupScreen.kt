package ru.substancial.dreamwalkers.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerAdapter
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.controllers.PovDirection
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ru.substancial.dreamwalkers.ecs.component.ButtonLayout
import ru.substancial.dreamwalkers.utilities.ClearScreen
import java.util.*

class ControllerSetupScreen : ScreenAdapter(), HasDisplay by HasDisplayMixin() {

    private val assignQueue = LinkedList(ButtonLayout.buttonToIndex.keys)

    private val textRenderer = SpriteBatch()
    private val text = BitmapFont()

    init {
        Controllers.addListener(object : ControllerAdapter() {
            private var button: Int? = null
            private var axis: Int? = null
            private var pov: Int? = null

            override fun buttonUp(controller: Controller?, buttonIndex: Int): Boolean {
                Gdx.app.log("button", buttonIndex.toString())
                if (buttonIndex != button) {
                    button = buttonIndex
                    assign(buttonIndex)
                }
                return true
            }

            override fun axisMoved(controller: Controller?, axisIndex: Int, value: Float): Boolean {
                Gdx.app.log("axis", axisIndex.toString())
                if (axisIndex != axis) {
                    axis = axisIndex
                    assign(axisIndex)
                }
                return true
            }

            override fun povMoved(controller: Controller?, povIndex: Int, value: PovDirection?): Boolean {
                Gdx.app.log("pov", povIndex.toString())
                if (pov != povIndex) {
                    pov = povIndex
                    assign(povIndex)
                }
                return true
            }

            private fun assign(index: Int) {/*
                val current = assignQueue.pop()
                ButtonLayout.buttonToIndex[current] = index
                if (assignQueue.isEmpty()) {}*/
            }

        })
    }

    override fun render(delta: Float) {
        ClearScreen()
        textRenderer.begin()
        text.draw(textRenderer, assignQueue.peek(), viewport.screenWidth / 2f, viewport.screenHeight / 2f)
        textRenderer.end()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

}