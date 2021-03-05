package ru.substancial.dreamwalkers.utilities

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body

inline class Radians(val value: Float)

inline class Degrees(val value: Float)

inline fun Radians.withValue(action: (Float) -> Float): Radians = Radians(action(value))

inline fun Degrees.withValue(action: (Float) -> Float): Degrees = Degrees(action(value))

fun Radians.toDegrees(): Degrees =
        Degrees(MathUtils.radiansToDegrees * value)

fun Degrees.toRadians(): Radians =
        Radians(MathUtils.degreesToRadians * value)

// region Extractors

fun Float.asDegrees(): Degrees = Degrees(this)
fun Float.asRadians(): Radians = Radians(this)

fun Vector2.typedAngleDegrees(): Degrees = Degrees(angle())
fun Vector2.typedAngleDegrees(reference: Vector2): Degrees = Degrees(angle(reference))
fun Vector2.typedAngleRadians(): Radians = Radians(angleRad())
fun Vector2.typedAngleRadians(reference: Vector2): Radians = Radians(angleRad(reference))

val Body.typedAngle: Radians
    get() = Radians(angle)

// endregion
