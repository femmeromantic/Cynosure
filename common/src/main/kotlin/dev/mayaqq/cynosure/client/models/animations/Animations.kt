package dev.mayaqq.cynosure.client.models.animations

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.util.Mth
import org.joml.Vector3f
import org.joml.Vector3fc
import kotlin.math.max
import kotlin.math.min

private val LOCAL_VEC_CACHE: ThreadLocal<Vector3f> = ThreadLocal.withInitial(::Vector3f)

@Serializable
public data class AnimationDefinition(val duration: Float, val looping: Boolean, val bones: Map<String, List<Animation>>)

@Serializable
public data class Animation(val target: Target, val keyframes: List<Keyframe>) {

    @Serializable
    public enum class Target {
        @SerialName("position") POSITION {
            override fun apply(animatable: Animatable, value: Vector3fc) = animatable.offsetPosition(value)
        },
        @SerialName("rotation") ROTATION {
            override fun apply(animatable: Animatable, value: Vector3fc) = animatable.offsetRotation(value)
        },
        @SerialName("scale") SCALE {
            override fun apply(animatable: Animatable, value: Vector3fc) = animatable.offsetScale(value)
        };

        public abstract fun apply(animatable: Animatable, value: Vector3fc)
    }

}

@Serializable
public data class Keyframe(val timestamp: Float, val target: @Serializable(ConfiguredVecSerializer::class) Vector3f, val interpolation: Interpolation) {
    public fun interface Interpolation {
        public fun apply(vector: Vector3f, delta: Float, keyframes: List<Keyframe>, currentFrame: Int, targetFrame: Int, strength: Float): Vector3f
    }
}

@JvmOverloads
public fun Animatable.Provider.animate(definition: AnimationDefinition, accumulatedTime: Long, vecCache: Vector3f = LOCAL_VEC_CACHE.get()) {
    definition.bones.forEach { (key, animations) ->
        getAny(key)?.let { animatable ->
            animations.forEach { animation ->
                val keyframes: List<Keyframe> = animation.keyframes
                val elapsed: Float = definition.getElapsedSeconds(accumulatedTime)
                val last = max(0.0, (Mth.binarySearch(0, keyframes.size) { index: Int -> elapsed <= keyframes[index].timestamp } - 1).toDouble()).toInt()
                val next = min((keyframes.size - 1).toDouble(), (last + 1).toDouble()).toInt()

                val lastFrame = keyframes[last]
                val nextFrame = keyframes[next]
                val sinceLast: Float = elapsed - lastFrame.timestamp
                val delta = if (next != last) Mth.clamp(sinceLast / (nextFrame.timestamp - lastFrame.timestamp), 0.0f, 1.0f) else 0.0f

                nextFrame.interpolation.apply(vecCache, delta, keyframes, last, next, 1f)
                animation.target.apply(animatable, vecCache)
            }
        }
    }
}

private fun AnimationDefinition.getElapsedSeconds(accumulatedTime: Long): Float {
    val f = accumulatedTime / 1000.0
    return if (looping) f.toFloat() % duration else f.toFloat()
}