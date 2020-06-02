package ru.substancial.dreamwalkers.controls

import com.badlogic.gdx.math.Vector2

/**
 * A universal contract that represents constructive features that most controllers share.
 * It does not have any callbacks, only reports current state of a controller.
 */
interface ControllerReporter {

    /**
     * Position of specified analog stick, usually within {-1.0..1.0, -1.0..1.0}
     */
    fun stick(side: Side): Vector2

    /**
     * Reports if a stick is pressed down like a button.
     */
    fun stickPressed(side: Side): Boolean

    /**
     * Extent at which specified trigger is pressed down.
     * Some controllers report this as an analog input, providing value within {0.0..1.0}, for example Xbox Controller.
     * Controllers that do not have this feature (e.g. some report it as a button) should report either 0.0 or 1.0.
     */
    fun trigger(side: Side): Float

    /**
     * Whether specified bumper is pressed down or not.
     */
    fun bumper(side: Side): Boolean

    /**
     * Whether specified dpad arrow is pressed down.
     */
    fun dpad(direction: Direction): Boolean

    /**
     * This is substantial difference between different controllers.
     * Generally, this method represents 4 buttons on the right side of a controller.
     *
     * [Direction.Down] - "A" on Xbox, Cross on DualShock
     *
     * [Direction.Left] - "X" on Xbox, Rectangle on DualShock
     *
     * [Direction.Up] - "Y" on Xbox, Triangle on DualShock
     *
     * [Direction.Right] "B" on Xbox, Circle on DualShock
     *
     * You get the idea.
     */
    fun action(direction: Direction): Boolean

    sealed class Side {
        object Left : Side()
        object Right : Side()
    }

    sealed class Direction {
        object Up : Direction()
        object Left : Direction()
        object Right : Direction()
        object Down : Direction()
    }

}

private fun  ControllerReporter.triggerDown(side: ControllerReporter.Side, deadzone: Deadzone): Boolean =
        deadzone.tryReport(trigger(side)) { true } ?: false

/**
 * Returns `x` on trigger axis, since some controllers report them as one axis.
 * Probably won't work if a controller does not.
 */
fun ControllerReporter.triggerAxis(): Float {
    return trigger(ControllerReporter.Side.Left) - trigger(ControllerReporter.Side.Right)
}