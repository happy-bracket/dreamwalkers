package ru.substancial.dreamwalkers.ecs.component

import com.badlogic.ashley.core.Component
import ru.substancial.dreamwalkers.level.Level

class LevelComponent(
    val level: Level
) : Component
