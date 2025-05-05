package dev.mayaqq.cynosure.utils.result

import dev.mayaqq.cynosure.utils.Either
import dev.mayaqq.cynosure.utils.Either.Left
import dev.mayaqq.cynosure.utils.Either.Right


public fun <A> Result<Result<A>>.flatten(): Result<A> = flatMap { it }

public inline fun <A> Result<A>.flatRecover(func: (Throwable) -> Result<A>): Result<A> =
    if(this.isFailure) func(exceptionOrNull()!!) else this

public fun <A> Result<A>.omit(): Result<Unit> = map { }

public inline fun <A> Result<A>.always(func: () -> Unit ): Result<A> = onFailure { func() }.onSuccess { func() }

public fun <A> Result<A>.toEither(): Either<A, Throwable> = fold(::Left, ::Right)

public inline fun <A, reified X : Throwable> Result<A>.extract(): Result<Either<A, X>> =
    map { Left<A, X>(it) }.flatRecover { if (it is X) Right<A, X>(it).success() else it.failure() }

public fun Result.Companion.unit(): Result<Unit> = success(Unit)

public infix fun <A, B> Result<A>.and(b: Result<B>): Result<Pair<A, B>> =
    flatMap { it1 -> b.map { it1 to it } }

@Suppress("UNCHECKED_CAST")
public infix fun <A, B> Result<A>.or(b: Result<B>): Result<Either<A, B>> = when {
    isSuccess -> Left<A, B>(getOrThrow()).success()
    b.isSuccess -> Right<A, B>(b.getOrThrow()).success()
    else -> flatMap { mapError { it.addSuppressed(b.exceptionOrNull()!!); it } as Result<Nothing> }
}