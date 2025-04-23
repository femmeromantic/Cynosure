package dev.mayaqq.cynosure.utils.particles

import com.mojang.brigadier.StringReader
import com.mojang.serialization.Codec
import com.teamresourceful.bytecodecs.base.ByteCodec
import dev.mayaqq.cynosure.Cynosure
import net.minecraft.commands.arguments.NbtTagArgument
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.FriendlyByteBuf

public class CynosureParticleType<T : CynosureParticleOptions<T>>(
    overrideLimiter: Boolean,
    public val codec: Codec<T>,
    public val networkCodec: ByteCodec<T>,
) : ParticleType<T>(overrideLimiter, null) {

    private val deserializer = EnhancedDeserializer()

    override fun getDeserializer(): ParticleOptions.Deserializer<T> = deserializer

    override fun codec(): Codec<T> = codec

    private inner class EnhancedDeserializer : ParticleOptions.Deserializer<T> {

        private val nbtArg = NbtTagArgument.nbtTag()

        override fun fromCommand(p0: ParticleType<T>, p1: StringReader): T {
            p1.expect(' ')
            val nbt = nbtArg.parse(p1) as CompoundTag
            return codec.parse(NbtOps.INSTANCE, nbt).getOrThrow(false) { Cynosure.error(it) }
        }

        override fun fromNetwork(p0: ParticleType<T>, p1: FriendlyByteBuf): T {
            return networkCodec.decode(p1)
        }

    }
}

public interface CynosureParticleOptions<S : CynosureParticleOptions<S>> : ParticleOptions {

    override fun getType(): CynosureParticleType<out S>

    @Suppress("UNCHECKED_CAST")
    override fun writeToNetwork(p0: FriendlyByteBuf) {
        (type as CynosureParticleType<S>).networkCodec.encode(this as S, p0)
    }
}