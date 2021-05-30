package ru.substancial.dreamwalkers

import com.badlogic.gdx.*
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.strongjoshua.console.Console
import com.strongjoshua.console.GUIConsole
import com.strongjoshua.console.LogLevel
import ru.substancial.dreamwalkers.dev.DwCommandExecutor
import ru.substancial.dreamwalkers.screen.*
import ru.substancial.dreamwalkers.utilities.addProcessor
import ru.substancial.dreamwalkers.utilities.removeProcessor

class Core : Game() {

    private lateinit var console: GUIConsole
    lateinit var commandExecutor: DwCommandExecutor
    private val assetManager = AssetManager()

    fun setScreen(image: ScreenImage) {
        setScreen(
            when (image) {
                is ScreenImage.Splash -> SplashScreen(assetManager, this)
                is ScreenImage.Game -> GameScreen(assetManager, this, "assets/scenarios/test", "scenario.lua", null)
                is ScreenImage.MainMenu -> MainScreen(assetManager, this)
                is ScreenImage.GameOver -> GameOverScreen(assetManager, this, image.iconFile, image.title, image.description)
            }
        )
    }

    override fun create() {
        Gdx.input.inputProcessor = InputMultiplexer()
        setScreen(ScreenImage.Splash)
        console = GUIConsole(assetManager[Assets.Skin], false)
        commandExecutor = DwCommandExecutor(this)
        console.setCommandExecutor(commandExecutor)
        Gdx.input.addProcessor(console.inputProcessor)
        setupLogger(console)
        setScreen(ScreenImage.MainMenu)
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
        assetManager.dispose()
        Gdx.input.removeProcessor(console.inputProcessor)
    }

    override fun setScreen(screen: Screen?) {
        this.screen?.dispose()
        super.setScreen(screen)
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
