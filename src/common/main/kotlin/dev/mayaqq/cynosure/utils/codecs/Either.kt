package dev.mayaqq.cynosure.utils.codecs

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(EitherSerializer::class)
@JvmInline
public value class Either<L, R> internal constructor(
    @PublishedApi
    internal val value: Any
) {
    public companion object {
        public fun <L, R> left(value: L): Either<L, R> = Either<L, R>(Left(value))

        public fun <L, R> right(value: R): Either<L, R> = Either<L, R>(Right(value))
    }

    public val isLeft: Boolean
        get() = value is Left

    public val isRight: Boolean
        get() = value is Right

    @Suppress("UNCHECKED_CAST")
    public val left: L? get() = (value as? Left)?.value as L?

    @Suppress("UNCHECKED_CAST")
    public val right: R? get() = (value as? Right)?.value as R?

    public inline fun ifLeft(action: (L) -> Unit): Either<L, R> {
        if(isLeft) action(left!!)
        return this
    }

    public inline fun ifRight(action: (R) -> Unit): Either<L, R> {
        if(isRight) action(right!!)
        return this
    }

    public inline fun <T> map(leftMapper: (L) -> T, rightMapper: (R) -> T): T = when {
        isLeft -> leftMapper(left!!)
        isRight -> rightMapper(right!!)
        else -> throw IllegalStateException()
    }

    public inline fun mapLeft(mapper: (R) -> L): L = when {
        isLeft -> left!!
        isRight -> mapper(right!!)
        else -> throw IllegalStateException()
    }

    public inline fun mapRight(mapper: (L) -> R): R = when {
        isLeft -> mapper(left!!)
        isRight -> right!!
        else -> throw IllegalStateException()
    }

    @JvmInline
    @PublishedApi
    internal value class Left(
        val value: Any?
    ) {
        override fun toString(): String = "Either.Left[${value.toString()}"
    }

    @JvmInline
    @PublishedApi
    internal value class Right(
        val value: Any?
    ) {
        override fun toString(): String = "Either.Right[${value.toString()}"
    }
}

public class EitherSerializer<L, R>(
    private val leftSerializer: KSerializer<L>,
    private val rightSerializer: KSerializer<R>
) : KSerializer<Either<L, R>> {

    override val descriptor: SerialDescriptor = leftSerializer.descriptor

    override fun deserialize(decoder: Decoder): Either<L, R> =
        runCatching { leftSerializer.deserialize(decoder) }.fold(
            { l -> Either.left(l) },
            { err -> runCatching { rightSerializer.deserialize(decoder) }.fold(
                { r -> Either.right(r) },
                { err2 -> throw SerializationException("failed to decode, Errors: $err /n $err2") }
            ) })

    override fun serialize(encoder: Encoder, value: Either<L, R>) {
        value.ifLeft { leftSerializer.serialize(encoder, it) }
            .ifRight { rightSerializer.serialize(encoder, it) }
    }
}