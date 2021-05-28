package ru.substancial.dreamwalkers.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.TextureAtlas

class SpriteComponent(
    val sprite: TextureAtlas.AtlasSprite
) : Component
