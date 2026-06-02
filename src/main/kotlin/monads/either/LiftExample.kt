package monads.either

fun main() {
    // Example 1: Lifting a simple transformation function
    // Original function that doubles a number
    val doubleNumber: (Int) -> Int = { it * 2 }

    // Lift the function to work with Either
    val liftedDouble = EitherApplicative.lift<String, Int, Int>(doubleNumber)

    // Successful case
    val successfulResult: Either<String, Int> = Either.Right(5)
    val doubledSuccess = liftedDouble(successfulResult)
    println("Doubled success: $doubledSuccess")  // Prints: Doubled success: Right(10)

    // Error case
    val errorResult: Either<String, Int> = Either.Left("Something went wrong")
    val doubledError = liftedDouble(errorResult)
    println("Doubled error: $doubledError")  // Prints: Doubled error: Left(Something went wrong)

    // Example 2: Lifting a function that converts to string
    val toString: (Int) -> String = { it.toString() }
    val liftedToString = EitherApplicative.lift<String, Int, String>(toString)

    val stringSuccess = liftedToString(Either.Right(42))
    println("String conversion: $stringSuccess")  // Prints: String conversion: Right(42)

    // Example 3: More complex transformation
    data class User(val name: String)

    val createUser: (String) -> User = { User(it) }
    val liftedUserCreation = EitherApplicative.lift<String, String, User>(createUser)

    val userSuccess = liftedUserCreation(Either.Right("Alice"))
    println("User creation: $userSuccess")  // Prints: User creation: Right(User(name=Alice))

    // Error propagation
    val userError = liftedUserCreation(Either.Left("Invalid input"))
    println("User creation error: $userError")  // Prints: User creation error: Left(Invalid input)
}

