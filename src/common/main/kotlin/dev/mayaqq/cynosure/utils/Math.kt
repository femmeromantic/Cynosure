package dev.mayaqq.cynosure.utils

import dev.mayaqq.cynosure.utils.colors.Color
import dev.mayaqq.cynosure.utils.colors.Colors

public const val PI: Double = java.lang.Math.PI
public const val DEG_TO_RAD = PI / 180.0
public const val RAD_TO_DEG = 180.0 / PI

public fun Float.normalized() = coerceIn(-1f, 1f)

public inline val Float.radians: Float get() = this * DEG_TO_RAD.toFloat()

public inline val Float.degrees: Float get() = this * RAD_TO_DEG.toFloat()

public inline val Double.radians: Double get() = this * DEG_TO_RAD

public inline val Double.degrees: Double get() = this * RAD_TO_DEG

fun uwu() {
    val c = Color.parse("white")
}