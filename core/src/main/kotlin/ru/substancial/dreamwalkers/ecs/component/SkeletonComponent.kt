package ru.substancial.dreamwalkers.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.esotericsoftware.spine.AnimationState
import com.esotericsoftware.spine.Skeleton

class SkeletonComponent(
    val skeleton: Skeleton,
    val animationState: AnimationState,
    val originOffset: Vector2
) : Component
