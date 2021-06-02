package ru.substancial.dreamwalkers.files

import com.badlogic.gdx.assets.AssetManager

class DreamwalkersAssetManager : AssetManager() {

    private var levelFolder = ""

    fun setLevelFolder(folder: String) {
        levelFolder = folder
    }

    override fun <T : Any?> get(fileName: String?): T {
        return if (contains(fileName)) super.get(fileName)
        else super.get(levelFolder + fileName)
    }

    override fun <T : Any?> get(fileName: String?, type: Class<T>?): T {
        return if (contains(fileName)) super.get(fileName, type)
        else super.get(levelFolder + fileName, type)
    }

}
