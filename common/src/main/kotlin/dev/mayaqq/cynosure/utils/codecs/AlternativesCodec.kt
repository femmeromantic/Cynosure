package dev.mayaqq.cynosure.utils.codecs

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps

public class AlternativesCodec<A>(
    private val codecs: List<Codec<A>>
) : Codec<A> {

    override fun <T : Any> encode(input: A, ops: DynamicOps<T>, prefix: T): DataResult<T> = codecs[0].encode(input, ops, prefix)


    override fun <T : Any> decode(ops: DynamicOps<T>, input: T): DataResult<Pair<A, T>> =
        codecs.map { codec -> codec.decode(ops, input) }.find { it.result().isPresent } ?: DataResult.error { "" }
}