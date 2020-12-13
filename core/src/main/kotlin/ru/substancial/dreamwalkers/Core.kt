package ru.substancial.dreamwalkers

import com.badlogic.gdx.*
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.strongjoshua.console.Console
import com.strongjoshua.console.GUIConsole
import com.strongjoshua.console.LogLevel
import ru.substancial.dreamwalkers.dev.DwCommandExecutor
import ru.substancial.dreamwalkers.screen.ControllerSetupScreen
import ru.substancial.dreamwalkers.screen.GameScreen
import ru.substancial.dreamwalkers.screen.MainScreen
import ru.substancial.dreamwalkers.utilities.addProcessor
import ru.substancial.dreamwalkers.utilities.removeProcessor

class Core : Game() {

    private lateinit var console: GUIConsole
    lateinit var commandExecutor: DwCommandExecutor

    override fun create() {
        Gdx.input.inputProcessor = InputMultiplexer()
        console = GUIConsole(Skin(Gdx.files.internal("assets/testskin/uiskin.json")), false)
        commandExecutor = DwCommandExecutor(this)
        console.setCommandExecutor(commandExecutor)
        Gdx.input.addProcessor(console.inputProcessor)
        setupLogger(console)
        setScreen(MainScreen(this))
        console.isVisible = true
        console.isDisabled = false
    }

    override fun render() {
        super.render()
        console.draw()
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        console.refresh()
    }

    override fun dispose() {
        super.dispose()
        console.dispose()
        Gdx.input.removeProcessor(console.inputProcessor)
    }

    private fun setupLogger(console: Console) {
        val originalLogger = Gdx.app.applicationLogger
        val logger = object : ApplicationLogger {

            override fun debug(tag: String?, message: String?) {
                originalLogger.debug(tag, message)
                console.log("$tag: $message")
            }

            override fun debug(tag: String?, message: String?, exception: Throwable?) {
                originalLogger.debug(tag, message, exception)
                console.log("$tag: $message")
                exception?.stackTrace?.forEach { console.log(it.toString()) }
            }

            override fun error(tag: String?, message: String?) {
                originalLogger.error(tag, message)
                console.log("$tag: $message", LogLevel.ERROR)
            }

            override fun error(tag: String?, message: String?, exception: Throwable?) {
                originalLogger.error(tag, message, exception)
                console.log("$tag, $message", LogLevel.ERROR)
                exception?.stackTrace?.forEach { console.log(it.toString(), LogLevel.ERROR) }
            }

            override fun log(tag: String?, message: String?) {
                originalLogger.log(tag, message)
                console.log("$tag: $message")
            }

            override fun log(tag: String?, message: String?, exception: Throwable?) {
                originalLogger.log(tag, message, exception)
                console.log("$tag: $message")
                exception?.stackTrace?.forEach { console.log(it.toString()) }
            }

        }

        Gdx.app.applicationLogger = logger
    }

}