package ru.substancial.dreamwalkers

import com.badlogic.gdx.Game
import ru.substancial.dreamwalkers.screen.ControllerSetupScreen

class Core : Game() {

    override fun create() {
        setScreen(ControllerSetupScreen(this))
    }

}