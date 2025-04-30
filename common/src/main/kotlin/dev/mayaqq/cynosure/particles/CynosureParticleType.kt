package dev.mayaqq.cynosure.particles

import com.mojang.brigadier.StringReader
import com.mojang.serialization.Codec
import com.teamresourceful.bytecodecs.base.ByteCodec
import dev.mayaqq.cynosure.Cynosure
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.TagParser
import net.minecraft.network.FriendlyByteBuf

public class CynosureParticleType<T : CynosureParticleOptions<T>>(
    public val codec: Codec<T>,
    public val networkCodec: ByteCodec<T>,
    overrideLimiter: Boolean = false,
    ) : ParticleType<T>(overrideLimiter, null) {

    private val deserializer = this.EnhancedDeserializer()

    override fun getDeserializer(): ParticleOptions.Deserializer<T> = deserializer

    override fun codec(): Codec<T> = codec

    private inner class EnhancedDeserializer : ParticleOptions.Deserializer<T> {

        override fun fromCommand(p0: ParticleType<T>, p1: StringReader): T {
            p1.expect(' ')
            return codec.parse(NbtOps.INSTANCE, TagParser(p1).readValue())
                .getOrThrow(false) { Cynosure.error(it) }
        }

        override fun fromNetwork(p0: ParticleType<T>, p1: FriendlyByteBuf): T {
            return networkCodec.decode(p1)
        }

    }
}

public interface CynosureParticleOptions<S : CynosureParticleOptions<S>> : ParticleOptions {

    override fun getType(): CynosureParticleType<S>

    @Suppress("UNCHECKED_CAST")
    override fun writeToNetwork(p0: FriendlyByteBuf) {
        type.networkCodec.encode(this as S, p0)
    }
}