package ru.substancial.dreamwalkers

import com.badlogic.gdx.Game
import ru.substancial.dreamwalkers.screen.ControllerSetupScreen
import ru.substancial.dreamwalkers.screen.GameScreen

class Core : Game() {

    override fun create() {
        setScreen(ControllerSetupScreen {
            setScreen(GameScreen())
        })
    }

}