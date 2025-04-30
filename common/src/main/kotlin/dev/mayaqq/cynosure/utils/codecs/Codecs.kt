package dev.mayaqq.cynosure.utils.codecs

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.ObjectEntryByteCodec
import kotlin.reflect.KProperty1

public object Codecs {

    public fun <T> alternatives(vararg codecs: Codec<T>): Codec<T> = AlternativesCodec(codecs.toList())

    public fun <T> recursive(name: String = "", wrapped: Codec<T>.() -> Codec<T>): RecursiveCodec<T> = RecursiveCodec(name, wrapped)

}

public infix fun <O, A> Codec<A>.fieldOf(field: KProperty1<O, A>): RecordCodecBuilder<O, A> =
    fieldOf(field.name).forGetter(field)

public infix fun <A> Codec<A>.fieldOf(name: String): MapCodec<A> = fieldOf(name)

public infix fun <O, A> MapCodec<A>.forGetter(getter: (O) -> A): RecordCodecBuilder<O, A> = forGetter(getter)

public infix fun <O, T> ByteCodec<T>.fieldOf(getter: (O) -> T): ObjectEntryByteCodec<O, T> =
    ObjectEntryByteCodec(this, getter)