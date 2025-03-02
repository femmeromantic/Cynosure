package dev.mayaqq.cynosure.utils.codecs

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.world.entity.animal.Cod
import java.util.function.Function
import java.util.stream.Stream


public fun <T> alternatives(vararg codecs: Codec<T>): Codec<T> = AlternativesCodec(codecs.toList())

public fun <T> Result<T>.toDataResult(): DataResult<T> = when {
    isSuccess -> DataResult.success(getOrThrow())
    else -> DataResult.error { exceptionOrNull()!!.message }
}

public class CodecSerializer<T>(codec: Codec<T>): KSerializer<T> {
    override fun deserialize(decoder: Decoder): T {
        TODO("Not yet implemented")
    }

    override fun serialize(encoder: Encoder, value: T) {
        TODO("Not yet implemented")
    }

    override val descriptor: SerialDescriptor
        get() = TODO("Not yet implemented")
}

public class AlternativesCodec<A>(
    private val codecs: List<Codec<A>>
) : Codec<A> {

    override fun <T : Any?> encode(input: A, ops: DynamicOps<T>, prefix: T): DataResult<T> = codecs[0].encode(input, ops, prefix)


    override fun <T : Any?> decode(ops: DynamicOps<T>, input: T): DataResult<Pair<A, T>> =
        codecs.map { codec -> codec.decode(ops, input) }.find { it.result().isPresent } ?: DataResult.error {""}
}