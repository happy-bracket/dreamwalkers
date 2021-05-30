package ru.substancial.dreamwalkers.screen

sealed class ScreenImage {

    object Splash : ScreenImage()

    object MainMenu : ScreenImage()

    object Game : ScreenImage()

    class GameOver(val iconFile: String, val title: String, val description: String) : ScreenImage()

}
