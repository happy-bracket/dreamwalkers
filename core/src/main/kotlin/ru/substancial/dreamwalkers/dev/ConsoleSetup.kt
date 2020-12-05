package ru.substancial.dreamwalkers.dev

import com.strongjoshua.console.CommandExecutor
import ru.substancial.dreamwalkers.Core
import ru.substancial.dreamwalkers.screen.MainScreen

class DwCommandExecutor(private val core: Core) : CommandExecutor() {

    fun exitToMainScreen() {
        core.screen = MainScreen(core)
    }

}