package monads.either

// Applicative implementation for Either monad
object EitherApplicative {
    // Pure/return function - wraps a value in the Right context
    fun <A, B> pure(value: B): Either<A, B> = Either.Right(value)

    // Applicative - applies a function wrapped in Either to a value wrapped in Either
    // To make it an Applicative, we need a way to take a wrapper that contains a
    // function and apply it to a wrapper that contains a value
    fun <A, B, C> apply(
        functionEither: Either<A, (B) -> C>,
        valueEither: Either<A, B>
    ): Either<A, C> {
        return when {
            functionEither is Either.Left -> functionEither
            valueEither is Either.Left -> valueEither
            functionEither is Either.Right && valueEither is Either.Right ->
                Either.Right(functionEither.value(valueEither.value))

            else -> throw IllegalStateException("Unexpected Either state")
        }
    }

    // Lift a function to work with Either values - it is essentially a more functional way 
    // of applying map. The `lift` function is designed to transform a regular function 
    // to work with `Either` values. It returns a new function that:
    //    - Takes an `Either<A, B>` as input
    //    - Applies the original function `f` only if the `Either` is a `Right`
    //    - Preserves the `Left` case unchanged
    fun <A, B, C> lift(f: (B) -> C): (Either<A, B>) -> Either<A, C> = { myeither ->
        myeither.map(f)
    }

    // Lift a binary function to work with Either values
    fun <A, B, C, D> lift2(f: (B, C) -> D):
                (Either<A, B>, Either<A, C>) -> Either<A, D> = { either1, either2 ->
        when {
            either1 is Either.Left -> either1
            either2 is Either.Left -> either2
            either1 is Either.Right && either2 is Either.Right ->
                Either.Right(f(either1.value, either2.value))

            else -> throw IllegalStateException("Unexpected Either state")
        }
    }
}

// Example usage
fun main() {
    // Pure example
    val pureValue: Either<Nothing, Int> = EitherApplicative.pure(5)
    println(pureValue)

    // Apply example
    val addFunc: Either<Nothing, (Int) -> Int> = Either.Right { it + 1 }
    val value: Either<Nothing, Int> = Either.Right(5)
    val appliedResult = EitherApplicative.apply(addFunc, value)
    print(appliedResult)


    // Lift example
    // val lifted  = EitherApplicative.lift { it * 2 }
    // val liftedResult = lifted(Either.Right(3))
    //
    // // Lift2 example
    // val add: (Int, Int) -> Int = { a, b -> a + b }
    // val lifted2 = EitherApplicative.lift2(add)
    // val lift2Result = lifted2(Either.Right(3), Either.Right(4))
}

