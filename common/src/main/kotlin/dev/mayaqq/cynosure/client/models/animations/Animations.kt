package dev.mayaqq.cynosure.client.models.animations

import net.minecraft.util.Mth
import net.minecraft.util.StringRepresentable
import org.joml.Vector3f
import org.joml.Vector3fc
import kotlin.math.max
import kotlin.math.min

private val LOCAL_VEC_CACHE: ThreadLocal<Vector3f> = ThreadLocal.withInitial(::Vector3f)

public data class AnimationDefinition(val repeats: Boolean, val animations: Map<String, List<Animation>>)

public data class Animation(val duration: Float, val target: Target, val keyframes: List<Keyframe>) {

    public enum class Target(private val serialName: String) : StringRepresentable {
        POSITION("position") {
            override fun apply(animatable: Animatable, value: Vector3fc) = animatable.offsetPosition(value)
        },
        ROTATION("rotation") {
            override fun apply(animatable: Animatable, value: Vector3fc) = animatable.offsetRotation(value)
        },
        SCALE("scale") {
            override fun apply(animatable: Animatable, value: Vector3fc) = animatable.offsetScale(value)
        };

        public abstract fun apply(animatable: Animatable, value: Vector3fc)

        override fun getSerializedName(): String = serialName
    }

}

public data class Keyframe(val timestamp: Float, val target: Vector3f, val interpolation: Interpolation) {
    public fun interface Interpolation {
        public fun apply(vector: Vector3f, delta: Float, keyframes: List<Keyframe>, currentFrame: Int, targetFrame: Int, strength: Float): Vector3f
    }
}

@JvmOverloads
public fun Animatable.Provider.animate(definition: AnimationDefinition, accumulatedTime: Long, vecCache: Vector3f = LOCAL_VEC_CACHE.get()) {
    definition.animations.forEach { (key, animations) ->
        getAny(key)?.let { animatable ->
            animations.forEach { animation ->
                val keyframes: List<Keyframe> = animation.keyframes
                val elapsed: Float = getElapsedSeconds(
                    animation,
                    accumulatedTime
                )
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

private fun getElapsedSeconds(animation: Animation, accumulatedTime: Long): Float {
    val f = (accumulatedTime / 1000.0f).toDouble()
    return f.toFloat() % animation.duration
}