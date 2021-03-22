package ru.substancial.dreamwalkers.ecs.component

interface Cooldown {

    var ticks: Float
    val cooldown: Float

    fun isAvailable() = ticks <= 0f
    fun tickAway(delta: Float) {
        ticks -= delta
    }
    fun startCooldown() {
        ticks = cooldown
    }

}
