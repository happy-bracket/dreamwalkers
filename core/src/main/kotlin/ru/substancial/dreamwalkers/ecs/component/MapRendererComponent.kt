package ru.substancial.dreamwalkers.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.maps.tiled.TiledMapRenderer

class MapRendererComponent(
    val renderer: TiledMapRenderer
) : Component
