package ru.substancial.dreamwalkers.files

import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.assets.loaders.resolvers.PrefixFileHandleResolver
import com.badlogic.gdx.files.FileHandle

class DreamwalkersHandleResolver : FileHandleResolver {

    private val internalResolver = InternalFileHandleResolver()
    private val backingResolver = PrefixFileHandleResolver(internalResolver, "")

    fun setLevelFolder(folder: String) {
        backingResolver.prefix = folder
    }

    override fun resolve(path: String): FileHandle {
        val internal = internalResolver.resolve(path)
        return if (internal.exists()) internal
        else backingResolver.resolve(path)
    }

}
