package ru.substancial.dreamwalkers.controls

import com.badlogic.gdx.math.Vector2

/**
 * A universal contract that represents constructive features that most controllers share.
 * It does not have any callbacks, only reports current state of a controller.
 */
interface ControllerReporter {

    /**
     * Position of the left analog stick, usually within {-1.0..1.0, -1.0..1.0}
     */
    fun leftStick(): Vector2

    /**
     * @see leftStick
     */
    fun rightStick(): Vector2

    /**
     * Reports if a stick is pressed down like a button.
     */
    fun leftStickPressed(): Boolean

    /**
     * @see leftStickPressed
     */
    fun rightStickPressed(): Boolean

    /**
     * Extent at which left trigger is pressed down.
     * Some controllers report this as an analog input, providing value within {0.0..1.0}, for example Xbox Controller.
     * Controllers that do not have this feature (e.g. some report it as a button) should report either 0.0 or 1.0.
     */
    fun leftTrigger(): Float

    /**
     * @see leftTrigger
     */
    fun rightTrigger(): Float

    /**
     * Whether bottom dpad arrow is pressed down.
     */
    fun dpadDown(): Boolean

    /**
     * @see dpadDown
     */
    fun dpadUp(): Boolean

    /**
     * @see dpadDown
     */
    fun dpadLeft(): Boolean

    /**
     * @see dpadDown
     */
    fun dpadRight(): Boolean

    /**
     * These are the substantial difference between different controllers.
     * Generally, following methods with `action*` naming pattern represent 4 buttons on the right side of a controller.
     *
     * "A" on Xbox, Cross on DualShock
     */
    fun actionDown(): Boolean

    /**
     * "X" on Xbox, Rectangle on DualShock
     */
    fun actionLeft(): Boolean

    /**
     * "Y" on Xbox, Triangle on DualShock
     */
    fun actionUp(): Boolean

    /**
     * "B" on Xbox, Circle on DualShock
     *
     * You get the idea.
     */
    fun actionRight(): Boolean

}

private fun triggerDown(extent: Float, deadzone: Deadzone): Boolean =
        deadzone.tryReport(extent) { true } ?: false

fun ControllerReporter.leftTriggerDown(deadzone: Deadzone): Boolean =
        triggerDown(leftTrigger(), deadzone)

fun ControllerReporter.rightTriggerDown(deadzone: Deadzone): Boolean =
        triggerDown(rightTrigger(), deadzone)

/**
 * Returns `x` on trigger axis, since some controllers report them as one axis.
 * Probably won't work if a controller does not.
 */
fun ControllerReporter.triggerAxis(): Float {
    return leftTrigger() - rightTrigger()
}