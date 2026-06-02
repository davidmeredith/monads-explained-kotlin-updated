package monads.either

fun main() {
    // LIFT EXAMPLE
    // 1. Lift a simple transformation function

    // Original function that doubles a number
    val doubleNumber: (Int) -> Int = { it * 2 }

    // Lift the function to work with Either
    val liftedDouble = EitherApplicative.lift<String, Int, Int>(doubleNumber)

    // Apply to a successful Either
    val successResult: Either<String, Int> = Either.Right(5)
    val doubledResult = liftedDouble(successResult)
    println("Lifted double result: $doubledResult")  // Prints: Lifted double result: Right(10)

    // Apply to an error Either
    val errorResult: Either<String, Int> = Either.Left("Error occurred")
    val doubledError = liftedDouble(errorResult)
    println("Lifted double error: $doubledError")  // Prints: Lifted double error: Left(Error occurred)

    // LIFT2 EXAMPLE
    // 2. Lift a binary function (function with two arguments)

    // Original binary function to add two numbers
    val addNumbers: (Int, Int) -> Int = { a, b -> a + b }

    // Lift the binary function to work with Either
    val liftedAdd = EitherApplicative.lift2<String, Int, Int, Int>(addNumbers)

    // Scenario 1: Both inputs are successful
    val result1 = liftedAdd(Either.Right(3), Either.Right(4))
    println("Lifted add successful: $result1")  // Prints: Lifted add successful: Right(7)

    // Scenario 2: First input is an error
    val result2 = liftedAdd(Either.Left("First error"), Either.Right(4))
    println("Lifted add first error: $result2")  // Prints: Lifted add first error: Left(First error)

    // Scenario 3: Second input is an error
    val result3 = liftedAdd(Either.Right(3), Either.Left("Second error"))
    println("Lifted add second error: $result3")  // Prints: Lifted add second error: Left(Second error)

    // Scenario 4: Both inputs are errors (first error takes precedence)
    val result4 = liftedAdd(Either.Left("First error"), Either.Left("Second error"))
    println("Lifted add both errors: $result4")  // Prints: Lifted add both errors: Left(First error)

    // Advanced example: Combining different types
    data class Person(val name: String, val age: Int)

    // Lift a function that creates a Person from name and age
    val createPerson: (String, Int) -> Person = { name, age -> Person(name, age) }
    val liftedPersonCreation = EitherApplicative.lift2<String, String, Int, Person>(createPerson)

    val personResult1 = liftedPersonCreation(Either.Right("Alice"), Either.Right(30))
    println("Person creation success: $personResult1")  // Prints: Person creation success: Right(Person(name=Alice, age=30))

    val personResult2 = liftedPersonCreation(Either.Left("Invalid name"), Either.Right(30))
    println("Person creation name error: $personResult2")  // Prints: Person creation name error: Left(Invalid name)
}

