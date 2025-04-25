package dev.mayaqq.cynosure.client.models.animations

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.mayaqq.cynosure.client.models.animations.registry.Interpolations
import dev.mayaqq.cynosure.client.models.animations.registry.VectorType
import dev.mayaqq.cynosure.utils.codecs.fieldOf
import net.minecraft.util.StringRepresentable

public object AnimationCodecs {

    public val KEYFRAME: Codec<Keyframe> = RecordCodecBuilder.create { it.group(
        Codec.FLOAT fieldOf Keyframe::timestamp,
        VectorType.ConfiguredVec.VEC_CODEC fieldOf Keyframe::target,
        Interpolations.REGISTRY.codec() fieldOf Keyframe::interpolation,
    ).apply(it, ::Keyframe) }

    public val ANIMATION: Codec<Animation> = RecordCodecBuilder.create { it.group(
        Codec.FLOAT fieldOf Animation::duration,
        StringRepresentable.fromEnum(Animation.Target::values) fieldOf Animation::target,
        KEYFRAME.listOf() fieldOf Animation::keyframes
    ).apply(it, ::Animation) }

    public val ANIMATION_DEFINITION: Codec<AnimationDefinition> = RecordCodecBuilder.create { it.group(
        Codec.BOOL fieldOf AnimationDefinition::repeats,
        Codec.unboundedMap(Codec.STRING, ANIMATION.listOf()) fieldOf AnimationDefinition::animations
    ).apply(it, ::AnimationDefinition) }
}