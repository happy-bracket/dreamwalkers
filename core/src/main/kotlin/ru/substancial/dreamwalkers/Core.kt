package ru.substancial.dreamwalkers

import com.badlogic.gdx.Game
import ru.substancial.dreamwalkers.screen.ControllerSetupScreen
import ru.substancial.dreamwalkers.screen.GameScreen
import ru.substancial.dreamwalkers.screen.MainScreen

class Core : Game() {

    override fun create() {
        setScreen(MainScreen())
    }

}