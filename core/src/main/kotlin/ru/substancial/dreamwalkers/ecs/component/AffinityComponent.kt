package ru.substancial.dreamwalkers.ecs.component

import com.badlogic.ashley.core.Component

class AffinityComponent(
        val affinityGroup: AffinityGroup
) : Component

enum class AffinityGroup {
    Player,
    Opponent
}

infix fun AffinityGroup.isHostileTo(other: AffinityGroup): Boolean {
    return when (this) {
        AffinityGroup.Player ->
            when (other) {
                AffinityGroup.Player -> false
                AffinityGroup.Opponent -> true
            }
        AffinityGroup.Opponent ->
            when (other) {
                AffinityGroup.Player -> true
                AffinityGroup.Opponent -> false
            }
    }
}