package ru.substancial.dreamwalkers.screen

import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import ru.substancial.dreamwalkers.Assets
import ru.substancial.dreamwalkers.Core
import ru.substancial.dreamwalkers.utilities.ClearScreen

class SplashScreen(
    private val assetManager: AssetManager,
    private val core: Core
) : ScreenAdapter() {

    init {
        assetManager.setLoader(TiledMap::class.java, TmxMapLoader())

        assetManager.load(Assets.Skin, Skin::class.java)
        assetManager.load(Assets.Armory.Sword, TiledMap::class.java)

        // TODO: later convert this to asynchronous loading to show something on the splash
        assetManager.finishLoading()
        // core.screen = MainScreen something like that
    }

    override fun render(delta: Float) {
        ClearScreen(0f, 0.2f, 1f, 1f)
    }

}
