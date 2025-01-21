@file:OptIn(ExperimentalContracts::class)

package monads.either

import monads.either.Either.Left
import monads.either.Either.Right
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

// Very basic implementation, just for the sake of the explanation
sealed class Either<out A, out B> {
    data class Left<A>(val value: A) : Either<A, Nothing>()
    data class Right<B>(val value: B) : Either<Nothing, B>()

    fun <C> map(f: (B) -> C): Either<A, C> = flatMap { Right(f(it)) }

//    fun <A, C> flatMap(f: (B) -> Either<A, C>): Either<A, C> = when (this) {
//        is Right -> f(this.value)
//        is Left -> this as Either<A, C>
//    }

    fun isRight(): Boolean {
        return when (this) {
            is Right -> true
            is Left -> false
        }
    }

    fun isLeft(): Boolean {
        return when (this) {
            is Right -> false
            is Left -> true
        }
    }
}


/**
 * Binds the given function across [Right], that is,
 * Map, or transform, the right value [B] of this [Either] into a new [Either] with a right value of type [C].
 * Returns a new [Either] with either the original left value of type [A] or the newly transformed right value of type [C].
 *
 * @param f The function to bind across [Right].
 */
inline fun <A, B, C> Either<A, B>.flatMap(f: (right: B) -> Either<A, C>): Either<A, C> {
    contract { callsInPlace(f, InvocationKind.AT_MOST_ONCE) }
    return when (this) {
        is Right -> f(this.value)
        is Left -> this
    }
}
