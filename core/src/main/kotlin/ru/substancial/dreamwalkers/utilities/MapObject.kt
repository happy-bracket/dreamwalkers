package ru.substancial.dreamwalkers.utilities

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.PolygonMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.objects.TextureMapObject

val MapObject.x: Float
    get() = when (this) {
        is RectangleMapObject -> rectangle.x
        is PolygonMapObject -> polygon.x
        is TextureMapObject -> x
        else -> throw IllegalArgumentException("temporal f off")
    }

val MapObject.y: Float
    get() = when (this) {
        is RectangleMapObject -> rectangle.y
        is PolygonMapObject -> polygon.y
        is TextureMapObject -> y
        else -> throw IllegalArgumentException("temporal f off")
    }