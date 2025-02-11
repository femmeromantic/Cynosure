package dev.mayaqq.cynosure.models.animations

import org.joml.Vector3fc
import java.util.*

interface Animatable {
    fun offsetPosition(offset: Vector3fc)

    fun offsetRotation(offset: Vector3fc)

    fun offsetScale(offset: Vector3fc)

    fun reset()

    interface Provider {
        fun getAny(key: String): Animatable?
    }
}