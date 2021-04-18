package ru.substancial.dreamwalkers.utilities

class DeltaTrigger(private val threshold: Float) {

    private var timer: Float = threshold

    /** @return whether threshold's been reached */
    fun tick(delta: Float): Boolean {
        timer -= delta
        return if (timer <= 0f) {
            timer = threshold
            true
        } else {
            false
        }
    }

}
