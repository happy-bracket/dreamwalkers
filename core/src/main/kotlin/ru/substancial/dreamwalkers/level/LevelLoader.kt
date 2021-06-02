package ru.substancial.dreamwalkers.level

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import ktx.assets.load
import ktx.assets.unload
import ru.substancial.dreamwalkers.files.DreamwalkersAssetManager
import java.util.*

class LevelLoader(
    private val scenarioPath: String,
    private val assetManager: DreamwalkersAssetManager
) {

    private val loadedResources = LinkedList<String>()
    private var whenLoaded: ((TiledMap) -> Unit)? = null
    private var mapName: String? = null

    fun loadLevel(name: String, whenLoaded: (map: TiledMap) -> Unit) {
        val levelPath = "$scenarioPath/$name/"
        val levelFolder = Gdx.files.internal(levelPath)
        val levelFolderContents = levelFolder.list()

        assetManager.setLevelFolder(levelPath)

        levelFolderContents.forEach {
            loadResource(it)
            loadedResources.add(it.path())
        }

        this.whenLoaded = whenLoaded
    }

    fun unloadLevel() {
        loadedResources.forEach(assetManager::unload)
        loadedResources.clear()
    }

    fun update() {
        if (assetManager.update()) {
            whenLoaded?.invoke(assetManager[mapName])
        }
    }

    private fun loadResource(handle: FileHandle) {
        when (handle.extension()) {
            "tmx" -> loadMap(handle)
            "png" -> loadTexture(handle)
            "atlas" -> loadAtlas(handle)
        }
    }

    private fun loadMap(handle: FileHandle) {
        mapName = handle.path()
        assetManager.load(
            mapName,
            TiledMap::class.java,
            TmxMapLoader.Parameters().apply { convertObjectToTileSpace = true })
    }

    private fun loadTexture(handle: FileHandle) {
        assetManager.load(
            handle.path(),
            Texture::class.java
        )
    }

    private fun loadAtlas(handle: FileHandle) {
        assetManager.load(
            handle.path(),
            TextureAtlas::class.java
        )
    }

}
