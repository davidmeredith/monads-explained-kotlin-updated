# Monads explained in Kotlin

Code for [monads explained](https://medium.com/@albert.llousas/monads-explained-in-kotlin-4126ac0cb7f2) medium article.

All credit to Albert Llousas. Updated and extended by David Meredith. 




- Datetime: 2025-12-27 11:30:06
- Model:    copilot/gpt-4o-2024-11-20
- Selected files:
  - /Users/david.meredith/vcs/kotlin/monads-explained-kotlin-updated/src/test/kotlin/monads/either/baking/BakingTest.kt

> explain how lines 86 and 87 work


*[Rate limit reached. Retrying in 1254517 seconds ...]*

*[Request cancelled by user.]*

-------

- Datetime: 2025-12-27 11:31:40
- Model:    claude/claude-sonnet-4-5-20250929
- Selected files:
  - /Users/david.meredith/vcs/kotlin/monads-explained-kotlin-updated/src/test/kotlin/monads/either/baking/BakingTest.kt

> 
> 


Error: {
  message = "Your credit balance is too low to access the Anthropic API. Please go to Plans & Billing to upgrade or purchase credits.",
  type = "invalid_request_error"
}

-------

- Datetime: 2025-12-29 14:43:10
- Model:    copilot/gpt-4
- Selected files:
  - /Users/david.meredith/vcs/kotlin/monads-explained-kotlin-updated/src/main/kotlin/monads/either/Either.kt

> how can i call the flipPrint method


*[Rate limit reached. Retrying in 1070029 seconds ...]*

*[Request cancelled by user.]*

-------

- Datetime: 2026-01-26 12:15:49
- Model:    claude-haiku/claude-3-5-haiku-20241022
- Selected files:
  - /Users/david.meredith/vcs/kotlin/monads-explained-kotlin-updated/src/main/kotlin/monads/either/Either.kt

> create an applicative for the Either monad


I'll help you understand the lines in the `Either.kt` file. Let me break this down step by step:


🤔 Thoughts

> The lines 86-87 are part of the `flatMap` function definition for the `Either` monad. I'll explain its purpose and implementation.


Let me explain lines 86-87:
```kotlin
inline fun <A, B, C> Either<A, B>.flatMap(f: (right: B) -> Either<A, C>): Either<A, C> {
  contract { callsInPlace(f, InvocationKind.AT_MOST_ONCE) }
  // ... rest of the implementation
}
```

Breaking this down:
1. `inline fun` means this function will be inlined at the call site for performance optimization.
2. The type parameters:
   - `A`: The type of the left (error) value
   - `B`: The type of the original right (success) value
   - `C`: The type of the new right (success) value after transformation
3. It's an extension function on `Either<A, B>`
4. The function takes another function `f` that:
   - Takes the right value of type `B`
   - Returns a new `Either<A, C>`

The `contract` line is a Kotlin experimental feature that:
- Guarantees the passed function `f` will be called at most once
- Helps the compiler with optimization and static analysis

The implementation (in the `when` block) handles two cases:
- If it's a `Right`, apply the function `f` to transform the value
- If it's a `Left`, return the original left value unchanged

Example usage would look like:
```kotlin
val result: Either<String, Int> = Right(5)
val transformed = result.flatMap { Right(it * 2) }  // Right(10)
val error = result.flatMap { Left("something went wrong") }  // Left("something went wrong")
```

Regarding your other questions:
1. There's no `flipPrint` method in this file, so I can't explain how to call it.
2. For creating an applicative for the Either monad, I'll demonstrate an implementation:


╭─  write_to_file("src/main/kotlin/monads/either/EitherApplicative.kt")  succeeded
╰─  completed


This implementation provides:
1. `pure`: Wraps a value in the `Right` context
2. `apply`: Applies a function wrapped in `Either` to a value wrapped in `Either`
3. `lift`: Lifts a function to work with `Either` values
4. `lift2`: Lifts a binary function to work with two `Either` values

The implementation ensures that:
- If any `Either` is `Left`, the result is that `Left`
- Functions are applied only when both the function and value are `Right`


✓  Task Completed

I've explained the `flatMap` function implementation and created an Applicative implementation for the Either monad in a new file `EitherApplicative.kt`. The explanation covers the function's type parameters, the contract feature, and how it transforms Either values. The Applicative implementation provides pure, apply, lift, and lift2 methods for working with Either values.

-------

- Datetime: 2026-01-27 20:46:19
- Model:    claude-haiku/claude-3-5-haiku-20241022
- Selected files:
  - /Users/david.meredith/vcs/kotlin/monads-explained-kotlin-updated/src/main/kotlin/monads/either/EitherApplicative.kt

> Explain the lift function and provide an example how to call it 


I'll explain the `lift` function in detail and provide a comprehensive example.

The `lift` function in the `EitherApplicative` object is designed to transform a regular function to work with `Either` values. Let's break it down:

```kotlin
fun <A, B, C> lift(f: (B) -> C): (Either<A, B>) -> Either<A, C> = { either ->
    either.map(f)
}
```

Key characteristics:
1. It takes a function `f` that transforms a value of type `B` to type `C`
2. It returns a new function that:
   - Takes an `Either<A, B>` as input
   - Applies the original function `f` only if the `Either` is a `Right`
   - Preserves the `Left` case unchanged

Here's a detailed example to illustrate its usage:


╭─  write_to_file("src/main/kotlin/monads/either/LiftExample.kt")  succeeded
╰─  completed


Let me explain the `lift` function's behavior:

1. It allows you to apply a function to the inner value of an `Either` without worrying about error cases
2. If the `Either` is a `Right`, the function is applied to its value
3. If the `Either` is a `Left`, it remains unchanged, preserving the error

The magic happens through the `.map()` method of `Either`, which:
- Applies the function only to `Right` values
- Leaves `Left` values untouched

Common use cases:
- Transforming successful results
- Applying computations safely
- Avoiding explicit error handling for each transformation

Comparison with `map`:
- `lift` is essentially a more functional way of applying `map`
- It creates a reusable function that can be applied to different `Either` instances


✓  Task Completed

I've created a detailed example in `LiftExample.kt` that demonstrates the `lift` function with multiple scenarios. The example shows how `lift` transforms functions to work with `Either` values, preserving error handling and allowing safe transformations of successful results.

-------

- Datetime: 2026-01-27 20:56:36
- Model:    claude-haiku/claude-3-5-haiku-20241022
- Selected files:
  - /Users/david.meredith/vcs/kotlin/monads-explained-kotlin-updated/src/main/kotlin/monads/either/EitherApplicative.kt

> fix the compilation errors in LiftExample.kt


I'll first check the compilation errors in the file.


╭─  get_diagnostics("src/main/kotlin/monads/either/LiftExample.kt")  succeeded
╰─  completed


The errors suggest that I need to specify the type parameter `A` explicitly for the `Either` type. I'll modify the file to fix these compilation errors:


╭─  write_to_file("src/main/kotlin/monads/either/LiftExample.kt")  succeeded
╰─  completed


The key changes:
1. Added explicit type parameter `A` (in this case, `String`) to `lift` calls
2. The type parameters are now `<String, InputType, OutputType>`
   - First parameter is the error type (`String`)
   - Second parameter is the input value type
   - Third parameter is the output value type

Now, let's verify the compilation:


╭─  bash("./gradlew compileKotlin")  failed
│   Error: 
│   
│   Welcome to Gradle 9.0.0!
│   
╰─  ... (Result truncated, remaining 67 lines not shown)


I see a JDK version compatibility issue. Let me check the Gradle configuration:


╭─  bash("cat build.gradle.kts")  succeeded
│   plugins {
│       kotlin("jvm") version "2.0.21"
│   }
│   
╰─  ... (Result truncated, remaining 18 lines not shown)


The issue seems to be with the JDK version. I'll verify the current Kotlin and Java versions:


╭─  bash("java -version && kotlinc -version")  failed
│   Error: 
│   openjdk version "24.0.2" 2025-07-15
│   OpenJDK Runtime Environment Temurin-24.0.2+12 (build 24.0.2+12)
│   OpenJDK 64-Bit Server VM Temurin-24.0.2+12 (build 24.0.2+12, mixed mode, sharing)
╰─  ... (Result truncated, remaining 3 lines not shown)


*[Rate limit reached. Retrying in 0 seconds ...]*


The JDK is version 24, which is not yet fully supported. I'll modify the build script to use a compatible JVM target:


╭─  write_to_file("build.gradle.kts")  succeeded
╰─  completed


Now, let's try compiling again:


╭─  bash("./gradlew compileKotlin")  succeeded
│   > Task :checkKotlinGradlePluginConfigurationErrors
│    SKIPPED
│   
│   > Task :compileKotlin
╰─  ... (Result truncated, remaining 11 lines not shown)


*[Rate limit reached. Retrying in 0 seconds ...]*


Great! The compilation is now successful. 

Key changes made:
1. Added explicit type parameters to `lift` function calls
2. Modified `build.gradle.kts` to use JVM Toolchain 17
3. Resolved compilation errors in `LiftExample.kt`


✓  Task Completed

I fixed the compilation errors in `LiftExample.kt` by:
1. Adding explicit type parameters to the `lift` function calls
2. Modifying the Gradle build script to use JVM Toolchain 17
3. Ensuring type compatibility for the `Either` monad examples

-------

- Datetime: 2026-02-07 11:59:10
- Model:    claude-haiku/claude-3-5-haiku-20241022
- Selected files:
  - /Users/david.meredith/vcs/kotlin/monads-explained-kotlin-updated/src/main/kotlin/monads/either/EitherApplicative.kt

> 
> provide an example of calling lift and lift2 
> 


I'll create a comprehensive example demonstrating both `lift` and `lift2` functions for the `Either` monad:


╭─  write_to_file("src/main/kotlin/monads/either/LiftAndLift2Example.kt")  succeeded
╰─  completed


Let me break down the key differences between `lift` and `lift2`:

1. `lift`:
   - Takes a unary function (function with one argument)
   - Transforms a single `Either<A, B>` to `Either<A, C>`
   - Applies the function only if the input is `Right`

2. `lift2`:
   - Takes a binary function (function with two arguments)
   - Transforms two `Either` values to a single `Either`
   - Applies the function only if BOTH inputs are `Right`
   - Returns the first `Left` encountered if any input is `Left`

Key behaviors demonstrated:
- Error propagation
- Safe function application
- Preserving error context
- Handling different scenarios with Either values

Compilation and running:


╭─  bash("./gradlew compileKotlin")  succeeded
│   Starting a Gradle Daemon, 1 incompatible Daemon could not be reused, use --status for details
│   
│   
│   > Task :checkKotlinGradlePluginConfigurationErrors
╰─  ... (Result truncated, remaining 16 lines not shown)


✓  Task Completed

I've created a comprehensive example in `LiftAndLift2Example.kt` that demonstrates:
1. `lift` function usage with a unary transformation
2. `lift2` function usage with a binary function
3. Various scenarios including successful transformations and error handling
4. Examples with different types, including a custom `Person` creation



