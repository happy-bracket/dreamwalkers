package ru.substancial.dreamwalkers.utilities

import com.badlogic.gdx.Gdx

fun Any.namedError(message: String) {
    Gdx.app.error(this::class.java.simpleName, message)
}

fun Any.namedDebug(message: String) {
    Gdx.app.debug(this::class.java.simpleName, message)
}

fun Any.namedLog(message: String) {
    Gdx.app.log(this::class.java.simpleName, message)
}
