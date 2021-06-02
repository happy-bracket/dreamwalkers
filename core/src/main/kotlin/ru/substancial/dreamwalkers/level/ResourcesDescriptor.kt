package ru.substancial.dreamwalkers.level

import com.badlogic.gdx.files.FileHandle

class ResourcesDescriptor(
    val qualifiedMapName: String
) {

    val resources: HashMap<String, FileHandle> = HashMap()

}
