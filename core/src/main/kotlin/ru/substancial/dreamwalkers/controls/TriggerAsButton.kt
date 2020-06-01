package ru.substancial.dreamwalkers.controls

class TriggerAsButton(val deadzone: Float) {

    var triggerDown: Boolean = false

    inline fun process(value: Float, onTriggerDown: () -> Unit = {}, onTriggerUp: () -> Unit = {}) {
        if (value >= deadzone) {
            if (!triggerDown) {
                triggerDown = true
                onTriggerDown()
            }
        } else {
            if (triggerDown) {
                triggerDown = false
                onTriggerUp()
            }
        }
    }

}